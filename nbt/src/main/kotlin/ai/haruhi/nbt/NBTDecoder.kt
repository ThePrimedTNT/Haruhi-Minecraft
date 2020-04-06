package ai.haruhi.nbt

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.Decoder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.StructureKind
import kotlinx.serialization.UpdateMode
import kotlinx.serialization.modules.SerialModule
import java.io.IOException
import java.io.InputStream

internal class NBTTopLevelDecoder(
    private val inputStream: InputStream,
    override val context: SerialModule
) : Decoder {
    override val updateMode = UpdateMode.UPDATE

    var tagType: Byte
        private set

    init {
        tagType = decodeByte()
        if (tagType != TAG_END) {
            // Ignore tag name
            decodeString()
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeDecoder =
        when (val kind = descriptor.kind) {
            is StructureKind.MAP -> NBTCompoundDecoder(this) { tagType = it }
            is StructureKind.LIST -> when (tagType) {
                TAG_BYTE_ARRAY, TAG_INT_ARRAY, TAG_LONG_ARRAY -> NBTArrayDecoder(this)
                TAG_LIST -> NBTListDecoder(this) { tagType = it }
                else -> error("Invalid tag type for list: $tagType")
            }
            is StructureKind.CLASS -> NBTCompoundClassDecoder(this) { tagType = it }
            else -> error("Unsupported kind: $kind")
        }

    override fun decodeByte(): Byte {
        val i = inputStream.read()
        if (i == -1) throw IOException("Unexpected EOF")
        return (i and 0xFF).toByte()
    }

    override fun decodeFloat(): Float = Float.fromBits(decodeInt())
    override fun decodeDouble(): Double = Double.fromBits(decodeLong())
    override fun decodeInt(): Int = combineInt(inputStream.readNBytes(4))
    override fun decodeLong(): Long = combine(inputStream.readNBytes(8), 8)
    override fun decodeShort(): Short = combine(inputStream.readNBytes(2), 2).toShort()
    override fun decodeString(): String = String(inputStream.readNBytes(decodeShort().toInt()), Charsets.UTF_8)

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = error("not implemented")

    override fun decodeChar(): Char = error("NBT does not support char types.")
    override fun decodeBoolean(): Boolean = error("NBT does not support boolean types.")
    override fun decodeNotNullMark(): Boolean = error("NBT does not support null types.")
    override fun decodeNull(): Nothing? = error("NBT does not support null types.")
    override fun decodeUnit() = error("NBT does not support unit types.")

    private fun combine(byteArray: ByteArray, length: Int): Long {
        if (byteArray.size != length) throw IOException("Unexpected EOF")
        var combinedLong: Long = 0
        for (i in 0 until length) {
            combinedLong = combinedLong or (byteArray[i].toLong() and 0xFF shl ((length - i - 1) * 8L).toInt())
        }
        return combinedLong
    }

    private fun combineInt(byteArray: ByteArray): Int {
        var combinedInt = 0
        for (i in 0..3) {
            combinedInt = combinedInt or (byteArray[i].toInt() and 0xFF shl ((3 - i) * 8L).toInt())
        }
        return combinedInt
    }
}

internal class NBTCompoundDecoder(
    topLevelDecoder: NBTTopLevelDecoder,
    private val changeTagType: (Byte) -> Unit
) : AbstractNBTCompositeDecoder(topLevelDecoder) {
    private var currentIndex: Int = -1

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        // Maps use even indexes as key and odd as value
        currentIndex++
        if (currentIndex.rem(2) == 0) {
            val tagType = topLevelDecoder.decodeByte()
            changeTagType(tagType)
            if (tagType == TAG_END) {
                return CompositeDecoder.READ_DONE
            }
        }
        return currentIndex
    }
}

internal class NBTArrayDecoder(
    topLevelDecoder: NBTTopLevelDecoder
) : AbstractNBTCompositeDecoder(topLevelDecoder) {

    private var currentIndex = -1

    private var collectionSize: Int = topLevelDecoder.decodeInt()

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        currentIndex++
        if (currentIndex >= collectionSize) {
            return CompositeDecoder.READ_DONE
        }
        return currentIndex
    }

    override fun decodeSequentially(): Boolean = true
    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = collectionSize
}

internal class NBTListDecoder(
    topLevelDecoder: NBTTopLevelDecoder,
    changeTagType: (Byte) -> Unit
) : AbstractNBTCompositeDecoder(topLevelDecoder) {

    private var currentIndex = -1

    private var collectionSize: Int

    init {
        changeTagType(topLevelDecoder.decodeByte())
        collectionSize = topLevelDecoder.decodeInt()
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        currentIndex++
        if (currentIndex >= collectionSize) {
            return CompositeDecoder.READ_DONE
        }
        return currentIndex
    }

    override fun decodeSequentially(): Boolean = true
    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = collectionSize
}

internal class NBTCompoundClassDecoder(
    topLevelDecoder: NBTTopLevelDecoder,
    private val changeTagType: (Byte) -> Unit
) : AbstractNBTCompositeDecoder(topLevelDecoder) {
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        val tagType = topLevelDecoder.decodeByte()
        changeTagType(tagType)
        if (tagType == TAG_END) {
            return CompositeDecoder.READ_DONE
        }
        return descriptor.getElementIndex(topLevelDecoder.decodeString())
    }
}

internal abstract class AbstractNBTCompositeDecoder(
    protected val topLevelDecoder: NBTTopLevelDecoder
) : CompositeDecoder {
    override val context: SerialModule get() = topLevelDecoder.context
    override val updateMode: UpdateMode get() = topLevelDecoder.updateMode

    override fun endStructure(descriptor: SerialDescriptor) {
    }

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean =
        topLevelDecoder.decodeBoolean()

    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte = topLevelDecoder.decodeByte()
    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char = topLevelDecoder.decodeChar()
    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double = topLevelDecoder.decodeDouble()
    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float = topLevelDecoder.decodeFloat()
    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int = topLevelDecoder.decodeInt()
    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long = topLevelDecoder.decodeLong()
    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short = topLevelDecoder.decodeShort()
    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String = topLevelDecoder.decodeString()
    override fun decodeUnitElement(descriptor: SerialDescriptor, index: Int) = topLevelDecoder.decodeUnit()

    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>
    ): T? = topLevelDecoder.decodeNullableSerializableValue(deserializer)

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>
    ): T = topLevelDecoder.decodeSerializableValue(deserializer)

    override fun <T : Any> updateNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        old: T?
    ): T? = topLevelDecoder.updateNullableSerializableValue(deserializer, old)

    override fun <T> updateSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        old: T
    ): T = topLevelDecoder.updateSerializableValue(deserializer, old)
}