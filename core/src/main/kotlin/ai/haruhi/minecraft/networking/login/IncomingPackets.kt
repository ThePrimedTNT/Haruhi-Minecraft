package ai.haruhi.minecraft.networking.login

import ai.haruhi.minecraft.networking.serialization.IncomingPacket
import ai.haruhi.minecraft.networking.serialization.PacketFormat
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class IncomingLoginStartPacket(
    val username: String
) : IncomingPacket

@Serializable
data class IncomingEncryptionResponsePacket(
    val sharedSecret: ByteArray,
    val verifyToken: ByteArray
) : IncomingPacket {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IncomingEncryptionResponsePacket

        if (!sharedSecret.contentEquals(other.sharedSecret)) return false
        if (!verifyToken.contentEquals(other.verifyToken)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sharedSecret.contentHashCode()
        result = 31 * result + verifyToken.contentHashCode()
        return result
    }

    @Serializer(forClass = IncomingEncryptionResponsePacket::class)
    companion object PacketSerializer : KSerializer<IncomingEncryptionResponsePacket> {
        override fun serialize(encoder: Encoder, value: IncomingEncryptionResponsePacket) {
            @Suppress("NAME_SHADOWING")
            val encoder = encoder as PacketFormat.PacketEncoder
            encoder.encodeVarInt(value.sharedSecret.size)
            encoder.encodeBytes(value.sharedSecret)
            encoder.encodeVarInt(value.verifyToken.size)
            encoder.encodeBytes(value.verifyToken)
        }

        override fun deserialize(decoder: Decoder): IncomingEncryptionResponsePacket {
            @Suppress("NAME_SHADOWING")
            val decoder = decoder as PacketFormat.PacketDecoder
            val sharedSecret = decoder.decodeBytes(decoder.decodeVarInt())
            val verifyToken = decoder.decodeBytes(decoder.decodeVarInt())
            return IncomingEncryptionResponsePacket(
                sharedSecret = sharedSecret,
                verifyToken = verifyToken
            )
        }
    }
}

@Serializable
data class IncomingLoginPluginResponse(
    val messageId: Int,
    val data: ByteArray?
) : IncomingPacket {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IncomingLoginPluginResponse

        if (messageId != other.messageId) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = messageId
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }

    @Serializer(forClass = IncomingLoginPluginResponse::class)
    companion object PacketSerializer : KSerializer<IncomingLoginPluginResponse> {
        override fun serialize(encoder: Encoder, value: IncomingLoginPluginResponse) {
            @Suppress("NAME_SHADOWING")
            val encoder = encoder as PacketFormat.PacketEncoder
            encoder.encodeVarInt(value.messageId)
            val data = value.data
            if (data != null) {
                encoder.encodeBoolean(true)
                encoder.encodeBytes(data)
            } else {
                encoder.encodeBoolean(false)
            }
        }

        override fun deserialize(decoder: Decoder): IncomingLoginPluginResponse {
            @Suppress("NAME_SHADOWING")
            val decoder = decoder as PacketFormat.PacketDecoder
            val messageId = decoder.decodeVarInt()
            val successful = decoder.decodeBoolean()
            val data = if (successful) {
                decoder.decodeBytes(decoder.remainingBytes())
            } else null
            return IncomingLoginPluginResponse(
                messageId = messageId,
                data = data
            )
        }
    }
}