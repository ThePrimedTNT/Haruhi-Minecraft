package ai.haruhi.nbt

import kotlinx.serialization.CompositeEncoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StructureKind
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerialModule
import java.io.OutputStream

internal class NBTTopLevelEncoder(
    private val outputStream: OutputStream,
    override val context: SerialModule
) : Encoder {

    init {
        encodeByte(TAG_COMPOUND)
        encodeString("")
    }

    override fun beginStructure(
        descriptor: SerialDescriptor,
        vararg typeSerializers: KSerializer<*>
    ): CompositeEncoder =
        when (val kind = descriptor.kind) {
            StructureKind.MAP -> NBTCompoundEncoder(this, false)
            StructureKind.CLASS -> NBTCompoundEncoder(this, true)
            else -> error("Unsupported kind: $kind")
        }

    private var collectionTagType: Byte? = null

    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int,
        vararg typeSerializers: KSerializer<*>
    ): CompositeEncoder =
        when (descriptor.kind) {
            StructureKind.LIST -> {
                when (val tagType = collectionTagType) {
                    TAG_LIST -> NBTListEncoder(this, collectionSize)
                    TAG_BYTE_ARRAY, TAG_INT_ARRAY, TAG_LONG_ARRAY ->
                        NBTArrayEncoder(this, tagType, collectionSize)
                    else -> error("Could not auto resolve type for variable: ${descriptor.serialName}")
                }
            }
            else -> super.beginCollection(descriptor, collectionSize, *typeSerializers)
        }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        collectionTagType = when (value) {
            is NBTList<*>, is List<*> -> TAG_LIST
            is NBTByteArray, is ByteArray -> TAG_BYTE_ARRAY
            is NBTIntArray, is IntArray -> TAG_INT_ARRAY
            is NBTLongArray, is LongArray -> TAG_LONG_ARRAY
            else -> null
        }
        super.encodeSerializableValue(serializer, value)
    }

    override fun encodeByte(value: Byte) {
        outputStream.write(value.toInt())
    }

    override fun encodeDouble(value: Double) {
        encodeLong(value.toBits())
    }

    override fun encodeFloat(value: Float) {
        encodeInt(value.toBits())
    }

    override fun encodeInt(value: Int) {
        outputStream.write(value ushr 24)
        outputStream.write(value ushr 16)
        outputStream.write(value ushr 8)
        outputStream.write(value)
    }

    override fun encodeLong(value: Long) {
        encodeInt((value ushr 32 and 0xFFFFFFFFL).toInt())
        encodeInt((value and 0xFFFFFFFFL).toInt())
    }

    override fun encodeShort(value: Short) {
        val intValue = value.toInt()
        outputStream.write(intValue ushr 8)
        outputStream.write(intValue)
    }

    override fun encodeString(value: String) {
        val byteArray = value.toByteArray(Charsets.UTF_8)
        require(byteArray.size <= Short.MAX_VALUE) {
            "String cannot be longer than ${Short.MAX_VALUE} characters"
        }
        encodeShort(byteArray.size.toShort())
        outputStream.write(byteArray)
    }

    override fun encodeBoolean(value: Boolean) {
        error("NBT does not support boolean types.")
    }

    override fun encodeChar(value: Char) {
        error("NBT does not support char types.")
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        error("NBT does not support enum types.")
    }

    override fun encodeNotNullMark() {
        error("NBT does not support null types.")
    }

    override fun encodeNull() {
        error("NBT does not support null types.")
    }

    override fun encodeUnit() {
        error("NBT does not support unit types.")
    }
}

internal class NBTListEncoder(
    topLevelEncoder: NBTTopLevelEncoder,
    private val collectionSize: Int
) : AbstractNBTCompositeEncoder(topLevelEncoder) {
    private var internalType: Byte = -1

    override fun <T> encodeValue(
        descriptor: SerialDescriptor,
        index: Int,
        tagType: Byte,
        value: T,
        encodeFunction: (T) -> Unit
    ) {
        if (internalType == (-1).toByte()) {
            internalType = tagType
            topLevelEncoder.encodeByte(internalType)
            topLevelEncoder.encodeInt(collectionSize)
        }
        require(internalType == tagType) {
            "List type mismatch (got: $tagType, expected: $internalType)"
        }
        encodeFunction(value)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        if (internalType == (-1).toByte()) {
            // never set, put end tag as type instead
            topLevelEncoder.encodeByte(TAG_END)
        }
    }
}

internal class NBTArrayEncoder(
    topLevelEncoder: NBTTopLevelEncoder,
    arrayTagType: Byte,
    collectionSize: Int
) : AbstractNBTCompositeEncoder(topLevelEncoder) {

    private val internalType = when (arrayTagType) {
        TAG_BYTE, TAG_BYTE_ARRAY -> TAG_BYTE
        TAG_INT, TAG_INT_ARRAY -> TAG_INT
        TAG_LONG, TAG_LONG_ARRAY -> TAG_LONG
        else -> error("Invalid array tag type: $arrayTagType")
    }

    init {
        topLevelEncoder.encodeInt(collectionSize)
    }

    override fun <T> encodeValue(
        descriptor: SerialDescriptor,
        index: Int,
        tagType: Byte,
        value: T,
        encodeFunction: (T) -> Unit
    ) {
        require(internalType == tagType) {
            "List type mismatch (got: $tagType, expected: $internalType)"
        }
        encodeFunction(value)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
    }
}

internal class NBTCompoundEncoder(
    topLevelEncoder: NBTTopLevelEncoder,
    private val isClass: Boolean
) : AbstractNBTCompositeEncoder(topLevelEncoder) {
    private lateinit var key: String

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        if (!isClass && index.rem(2) == 0) {
            key = value
        } else {
            super.encodeStringElement(descriptor, index, value)
        }
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) {
        if (!isClass && value is String) {
            encodeStringElement(descriptor, index, value)
            return
        }

        // Try to resolve type based on explicit annotation, else base it off last type
        val nbtTagListAnnotation = descriptor.getElementAnnotations(index)
            .find { it is NBTListTag } as? NBTListTag

        if (nbtTagListAnnotation != null) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (nbtTagListAnnotation.type) {
                NBTListType.ARRAY -> if (forceSerializeArray(descriptor, index, value as Any)) return
                NBTListType.LIST -> if (forceSerializeList(descriptor, index, value as Any)) return
            }
        }

        super.encodeSerializableElement(descriptor, index, serializer, value)
    }

    private fun forceSerializeArray(descriptor: SerialDescriptor, index: Int, value: Any): Boolean {
        if (value is Array<*>) return false
        if (value !is List<*>) error("Can only force lists to serialize as arrays. got: ${value::class.simpleName}")
        @Suppress("UNCHECKED_CAST")
        when (val kind = descriptor.getElementDescriptor(index).getElementDescriptor(0).kind) {
            PrimitiveKind.BYTE -> super.encodeSerializableElement(
                descriptor, index, ByteArraySerializer(), (value as List<Byte>).toByteArray()
            )
            PrimitiveKind.INT -> super.encodeSerializableElement(
                descriptor, index, IntArraySerializer(), (value as List<Int>).toIntArray()
            )
            PrimitiveKind.LONG -> super.encodeSerializableElement(
                descriptor, index, LongArraySerializer(), (value as List<Long>).toLongArray()
            )
            else -> error("Can only convert lists to arrays of types: Byte, Int, and Long. Got: $kind")
        }
        return true
    }

    private fun <T> forceSerializeList(descriptor: SerialDescriptor, index: Int, value: T): Boolean {
        if (value is List<*>) return false
        when (value) {
            is ByteArray -> super.encodeSerializableElement(descriptor, index, BYTE_LIST_SERIALIZER, value.toList())
            is ShortArray -> super.encodeSerializableElement(descriptor, index, SHORT_LIST_SERIALIZER, value.toList())
            is IntArray -> super.encodeSerializableElement(descriptor, index, INT_LIST_SERIALIZER, value.toList())
            is LongArray -> super.encodeSerializableElement(descriptor, index, LONG_LIST_SERIALIZER, value.toList())
            is FloatArray -> super.encodeSerializableElement(descriptor, index, FLOAT_LIST_SERIALIZER, value.toList())
            is DoubleArray -> super.encodeSerializableElement(descriptor, index, DOUBLE_LIST_SERIALIZER, value.toList())
            else -> error("Can only convert arrays to primitive lists")
        }
        return true
    }

    override fun <T> encodeValue(
        descriptor: SerialDescriptor,
        index: Int,
        tagType: Byte,
        value: T,
        encodeFunction: (T) -> Unit
    ) {
        if (isClass) {
            topLevelEncoder.encodeByte(tagType)
            topLevelEncoder.encodeString(descriptor.getElementName(index))
        } else {
            require(index.rem(2) == 1) { "Compound map key must be a String" }
            topLevelEncoder.encodeByte(tagType)
            topLevelEncoder.encodeString(key)
        }
        encodeFunction(value)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        topLevelEncoder.encodeByte(TAG_END)
    }

    companion object {
        private val BYTE_LIST_SERIALIZER = ListSerializer(Byte.serializer())
        private val SHORT_LIST_SERIALIZER = ListSerializer(Short.serializer())
        private val INT_LIST_SERIALIZER = ListSerializer(Int.serializer())
        private val LONG_LIST_SERIALIZER = ListSerializer(Long.serializer())
        private val FLOAT_LIST_SERIALIZER = ListSerializer(Float.serializer())
        private val DOUBLE_LIST_SERIALIZER = ListSerializer(Double.serializer())
    }
}

internal abstract class AbstractNBTCompositeEncoder(
    val topLevelEncoder: NBTTopLevelEncoder
) : CompositeEncoder {
    override val context: SerialModule get() = topLevelEncoder.context

    abstract fun <T> encodeValue(
        descriptor: SerialDescriptor,
        index: Int,
        tagType: Byte,
        value: T,
        encodeFunction: (T) -> Unit
    )

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        encodeValue(descriptor, index, TAG_BYTE, value, topLevelEncoder::encodeByte)
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        encodeValue(descriptor, index, TAG_DOUBLE, value, topLevelEncoder::encodeDouble)
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        encodeValue(descriptor, index, TAG_FLOAT, value, topLevelEncoder::encodeFloat)
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        encodeValue(descriptor, index, TAG_INT, value, topLevelEncoder::encodeInt)
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        encodeValue(descriptor, index, TAG_LONG, value, topLevelEncoder::encodeLong)
    }

    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) {
        TODO("Not yet implemented")
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) {
        val tagType = when (value as Any) {
            is Byte, is NBTByte -> TAG_BYTE
            is Short, is NBTShort -> TAG_SHORT
            is Int, is NBTInt -> TAG_INT
            is Long, is NBTLong -> TAG_LONG
            is Float, is NBTFloat -> TAG_FLOAT
            is Double, is NBTDouble -> TAG_DOUBLE
            is ByteArray, is NBTByteArray -> TAG_BYTE_ARRAY
            is String, is NBTString -> TAG_STRING
            is List<*>, is NBTList<*> -> TAG_LIST
            is Map<*, *>, is NBTCompound -> TAG_COMPOUND
            is IntArray, is NBTIntArray -> TAG_INT_ARRAY
            is LongArray, is NBTLongArray -> TAG_LONG_ARRAY
            else -> if (descriptor.kind == StructureKind.CLASS) {
                TAG_COMPOUND
            } else error("Unsupported type: $value ${descriptor.kind}")
        }

        encodeValue(descriptor, index, tagType, value) {
            topLevelEncoder.encodeSerializableValue(serializer, value)
        }
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        encodeValue(descriptor, index, TAG_SHORT, value, topLevelEncoder::encodeShort)
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        encodeValue(descriptor, index, TAG_STRING, value, topLevelEncoder::encodeString)
    }

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        error("NBT does not support boolean types")
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        error("NBT does not support char types")
    }

    override fun encodeUnitElement(descriptor: SerialDescriptor, index: Int) {
        error("NBT does not support unit types")
    }
}