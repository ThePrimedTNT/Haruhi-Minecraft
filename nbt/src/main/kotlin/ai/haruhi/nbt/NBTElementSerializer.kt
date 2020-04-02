package ai.haruhi.nbt

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicKind
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.Serializer
import kotlinx.serialization.StructureKind
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

internal const val TAG_END: Byte = 0
internal const val TAG_BYTE: Byte = 1
internal const val TAG_SHORT: Byte = 2
internal const val TAG_INT: Byte = 3
internal const val TAG_LONG: Byte = 4
internal const val TAG_FLOAT: Byte = 5
internal const val TAG_DOUBLE: Byte = 6
internal const val TAG_BYTE_ARRAY: Byte = 7
internal const val TAG_STRING: Byte = 8
internal const val TAG_LIST: Byte = 9
internal const val TAG_COMPOUND: Byte = 10
internal const val TAG_INT_ARRAY: Byte = 11
internal const val TAG_LONG_ARRAY: Byte = 12

val defaultNBTFormatModule = SerializersModule {
    contextual(NBTByteSerializer)
    contextual(NBTShortSerializer)
    contextual(NBTIntSerializer)
    contextual(NBTLongSerializer)
    contextual(NBTFloatSerializer)
    contextual(NBTDoubleSerializer)
    contextual(NBTByteArraySerializer)
    contextual(NBTStringSerializer)
    contextual(NBTListSerializer)
    contextual(NBTCompoundSerializer)
    contextual(NBTIntArraySerializer)
    contextual(NBTLongArraySerializer)
}

internal enum class CodecMode {
    TOP_LEVEL,
    MAP,
    LIST,
    ARRAY,
    CLASS
}

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class NBTListTag(val type: NBTListType = NBTListType.AUTO)

enum class NBTListType {
    AUTO,
    ARRAY,
    LIST
}

@Serializer(forClass = NBTElement::class)
object NBTElementSerializer : KSerializer<NBTElement> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor("ai.haruhi.nbt.NBTElement", PolymorphicKind.SEALED) {
            element("NBTByte", NBTByteSerializer.descriptor)
            // element("NBTCompound", NBTCompoundSerializer.descriptor)
        }

    override fun deserialize(decoder: Decoder): NBTElement =
        when (val tagType = (decoder as NBTDecoder).tagType) {
            TAG_BYTE -> decoder.decodeSerializableValue(NBTByteSerializer)
            TAG_SHORT -> decoder.decodeSerializableValue(NBTShortSerializer)
            TAG_INT -> decoder.decodeSerializableValue(NBTIntSerializer)
            TAG_LONG -> decoder.decodeSerializableValue(NBTLongSerializer)
            TAG_FLOAT -> decoder.decodeSerializableValue(NBTFloatSerializer)
            TAG_DOUBLE -> decoder.decodeSerializableValue(NBTDoubleSerializer)
            TAG_BYTE_ARRAY -> decoder.decodeSerializableValue(NBTByteArraySerializer)
            TAG_STRING -> decoder.decodeSerializableValue(NBTStringSerializer)
            TAG_LIST -> decoder.decodeSerializableValue(NBTListSerializer)
            TAG_COMPOUND -> decoder.decodeSerializableValue(NBTCompoundSerializer)
            TAG_INT_ARRAY -> decoder.decodeSerializableValue(NBTIntArraySerializer)
            TAG_LONG_ARRAY -> decoder.decodeSerializableValue(NBTLongArraySerializer)
            else -> error("Unsupported type: $tagType")
        }

    override fun serialize(encoder: Encoder, value: NBTElement) {
        when (value) {
            is NBTByte -> encoder.encodeSerializableValue(NBTByteSerializer, value)
            is NBTShort -> encoder.encodeSerializableValue(NBTShortSerializer, value)
            is NBTInt -> encoder.encodeSerializableValue(NBTIntSerializer, value)
            is NBTLong -> encoder.encodeSerializableValue(NBTLongSerializer, value)
            is NBTFloat -> encoder.encodeSerializableValue(NBTFloatSerializer, value)
            is NBTDouble -> encoder.encodeSerializableValue(NBTDoubleSerializer, value)
            is NBTByteArray -> encoder.encodeSerializableValue(NBTByteArraySerializer, value)
            is NBTString -> encoder.encodeSerializableValue(NBTStringSerializer, value)
            is NBTList<*> -> @Suppress("UNCHECKED_CAST")
            encoder.encodeSerializableValue(NBTListSerializer, value as NBTList<NBTElement>)
            is NBTCompound -> encoder.encodeSerializableValue(NBTCompoundSerializer, value)
            is NBTIntArray -> encoder.encodeSerializableValue(NBTIntArraySerializer, value)
            is NBTLongArray -> encoder.encodeSerializableValue(NBTLongArraySerializer, value)
            else -> error("Unsupported NBTElement: ${value::class.simpleName}")
        }
    }

    override fun patch(decoder: Decoder, old: NBTElement): NBTElement {
        // TODO figure this out
        return deserialize(decoder)
    }
}

@Serializer(forClass = NBTByte::class)
object NBTByteSerializer : KSerializer<NBTByte> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("ai.haruhi.nbt.NBTByte", PrimitiveKind.BYTE)

    override fun deserialize(decoder: Decoder): NBTByte =
        NBTByte(decoder.decodeByte())

    override fun serialize(encoder: Encoder, value: NBTByte) {
        encoder.encodeByte(value.value)
    }
}

@Serializer(forClass = NBTShort::class)
object NBTShortSerializer : KSerializer<NBTShort> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("ai.haruhi.nbt.NBTShort", PrimitiveKind.SHORT)

    override fun deserialize(decoder: Decoder): NBTShort =
        NBTShort(decoder.decodeShort())

    override fun serialize(encoder: Encoder, value: NBTShort) {
        encoder.encodeShort(value.value)
    }
}

@Serializer(forClass = NBTInt::class)
object NBTIntSerializer : KSerializer<NBTInt> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("ai.haruhi.nbt.NBTInt", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): NBTInt =
        NBTInt(decoder.decodeInt())

    override fun serialize(encoder: Encoder, value: NBTInt) {
        encoder.encodeInt(value.value)
    }
}

@Serializer(forClass = NBTLong::class)
object NBTLongSerializer : KSerializer<NBTLong> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("ai.haruhi.nbt.NBTLong", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): NBTLong =
        NBTLong(decoder.decodeLong())

    override fun serialize(encoder: Encoder, value: NBTLong) {
        encoder.encodeLong(value.value)
    }
}

@Serializer(forClass = NBTFloat::class)
object NBTFloatSerializer : KSerializer<NBTFloat> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("ai.haruhi.nbt.NBTFloat", PrimitiveKind.FLOAT)

    override fun deserialize(decoder: Decoder): NBTFloat =
        NBTFloat(decoder.decodeFloat())

    override fun serialize(encoder: Encoder, value: NBTFloat) {
        encoder.encodeFloat(value.value)
    }
}

@Serializer(forClass = NBTDouble::class)
object NBTDoubleSerializer : KSerializer<NBTDouble> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("ai.haruhi.nbt.NBTDouble", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): NBTDouble =
        NBTDouble(decoder.decodeDouble())

    override fun serialize(encoder: Encoder, value: NBTDouble) {
        encoder.encodeDouble(value.value)
    }
}

@Serializer(forClass = NBTByteArray::class)
object NBTByteArraySerializer : KSerializer<NBTByteArray> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor("ai.haruhi.nbt.NBTByteArray", StructureKind.LIST)

    private val serializer = ByteArraySerializer()

    override fun deserialize(decoder: Decoder): NBTByteArray =
        NBTByteArray(serializer.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: NBTByteArray) {
        serializer.serialize(encoder, value.value)
    }
}

@Serializer(forClass = NBTString::class)
object NBTStringSerializer : KSerializer<NBTString> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("ai.haruhi.nbt.NBTString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): NBTString =
        NBTString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: NBTString) {
        encoder.encodeString(value.value)
    }
}

@Serializer(forClass = NBTList::class)
object NBTListSerializer : KSerializer<NBTList<NBTElement>> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor("ai.haruhi.nbt.NBTList", StructureKind.LIST)

    private val serializer = ListSerializer(NBTElementSerializer)

    override fun deserialize(decoder: Decoder): NBTList<NBTElement> =
        NBTList(serializer.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: NBTList<NBTElement>) {
        serializer.serialize(encoder, value.value)
    }
}

@Serializer(forClass = NBTCompound::class)
object NBTCompoundSerializer : KSerializer<NBTCompound> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor("ai.haruhi.nbt.NBTCompound", kind = StructureKind.MAP)

    private val serializer = MapSerializer(String.serializer(), NBTElementSerializer)

    override fun deserialize(decoder: Decoder): NBTCompound {
        return NBTCompound(serializer.deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: NBTCompound) {
        serializer.serialize(encoder, value.value)
    }
}

@Serializer(forClass = NBTIntArray::class)
object NBTIntArraySerializer : KSerializer<NBTIntArray> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor("ai.haruhi.nbt.NBTIntArray", StructureKind.LIST)

    private val serializer = IntArraySerializer()

    override fun deserialize(decoder: Decoder): NBTIntArray =
        NBTIntArray(serializer.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: NBTIntArray) {
        serializer.serialize(encoder, value.value)
    }
}

@Serializer(forClass = NBTLongArray::class)
object NBTLongArraySerializer : KSerializer<NBTLongArray> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor("ai.haruhi.nbt.NBTLongArray", StructureKind.LIST)

    private val serializer = LongArraySerializer()

    override fun deserialize(decoder: Decoder): NBTLongArray =
        NBTLongArray(serializer.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: NBTLongArray) {
        serializer.serialize(encoder, value.value)
    }
}