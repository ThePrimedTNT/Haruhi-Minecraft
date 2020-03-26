package ai.haruhi.minecraft.networking.serialization

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind

object VarIntSerializer : KSerializer<Int> {
    override val descriptor = PrimitiveDescriptor("VarIntSerializer", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Int {
        val packetDecoder = decoder as PacketFormat.PacketDecoder
        return packetDecoder.decodeVarInt()
    }

    override fun serialize(encoder: Encoder, value: Int) {
        val packetEncoder = encoder as PacketFormat.PacketEncoder
        packetEncoder.encodeVarInt(value)
    }
}

object VarLongSerializer : KSerializer<Long> {
    override val descriptor = PrimitiveDescriptor("VarLongSerializer", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Long {
        val packetDecoder = decoder as PacketFormat.PacketDecoder
        return packetDecoder.decodeVarLong()
    }

    override fun serialize(encoder: Encoder, value: Long) {
        val packetEncoder = encoder as PacketFormat.PacketEncoder
        packetEncoder.encodeVarLong(value)
    }
}