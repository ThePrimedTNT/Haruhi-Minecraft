package ai.haruhi.nbt

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicKind
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.Serializer
import kotlinx.serialization.StructureKind
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.mapDescriptor

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
        }
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
        PrimitiveDescriptor("ai.haruhi.nbt.NBTDouble", PrimitiveKind.FLOAT)

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

    override fun deserialize(decoder: Decoder): NBTByteArray =
        NBTByteArray(ByteArray(decoder.decodeInt()) { decoder.decodeByte() })

    override fun serialize(encoder: Encoder, value: NBTByteArray) {
        val byteArray = value.value
        encoder.encodeInt(byteArray.size)
        byteArray.forEach { encoder.encodeByte(it) }
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

    override fun deserialize(decoder: Decoder): NBTList<NBTElement> =
        NBTList(ListSerializer(NBTElementSerializer).deserialize(decoder))

    override fun serialize(encoder: Encoder, value: NBTList<NBTElement>) {
        ListSerializer(NBTElementSerializer).serialize(encoder, value.value)
    }
}

@Serializer(forClass = NBTCompound::class)
object NBTCompoundSerializer : KSerializer<NBTCompound> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor("ai.haruhi.nbt.NBTCompound", kind = StructureKind.MAP) {
            mapDescriptor(
                String.serializer().descriptor,
                NBTElementSerializer.descriptor
            )
        }

    override fun deserialize(decoder: Decoder): NBTCompound {
        return NBTCompound(
            MapSerializer(String.serializer(), NBTElementSerializer).deserialize(decoder)
        )
    }

    override fun serialize(encoder: Encoder, value: NBTCompound) {
        MapSerializer(String.serializer(), NBTElementSerializer).serialize(encoder, value.value)
    }
}

@Serializer(forClass = NBTIntArray::class)
object NBTIntArraySerializer : KSerializer<NBTIntArray> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor("ai.haruhi.nbt.NBTIntArray", StructureKind.LIST)

    override fun deserialize(decoder: Decoder): NBTIntArray =
        NBTIntArray(IntArray(decoder.decodeInt()) { decoder.decodeInt() })

    override fun serialize(encoder: Encoder, value: NBTIntArray) {
        val byteArray = value.value
        encoder.encodeInt(byteArray.size)
        byteArray.forEach { encoder.encodeInt(it) }
    }
}

@Serializer(forClass = NBTLongArray::class)
object NBTLongArraySerializer : KSerializer<NBTLongArray> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor("ai.haruhi.nbt.NBTLongArray", StructureKind.LIST)

    override fun deserialize(decoder: Decoder): NBTLongArray =
        NBTLongArray(LongArray(decoder.decodeInt()) { decoder.decodeLong() })

    override fun serialize(encoder: Encoder, value: NBTLongArray) {
        val byteArray = value.value
        encoder.encodeInt(byteArray.size)
        byteArray.forEach { encoder.encodeLong(it) }
    }
}