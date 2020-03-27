package ai.haruhi.minecraft.networking.login

import ai.haruhi.minecraft.networking.serialization.OutgoingPacket
import ai.haruhi.minecraft.networking.serialization.PacketFormat
import ai.haruhi.minecraft.networking.serialization.VarIntSerializer
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class OutgoingDisconnectPacket(
    val reason: String
) : OutgoingPacket

@Serializable
data class OutgoingEncryptionRequest(
    val serverId: String,
    val publicKey: ByteArray,
    val verifyToken: ByteArray
) : OutgoingPacket {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OutgoingEncryptionRequest

        if (serverId != other.serverId) return false
        if (!publicKey.contentEquals(other.publicKey)) return false
        if (!verifyToken.contentEquals(other.verifyToken)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serverId.hashCode()
        result = 31 * result + publicKey.contentHashCode()
        result = 31 * result + verifyToken.contentHashCode()
        return result
    }

    @Serializer(forClass = OutgoingEncryptionRequest::class)
    companion object PacketSerializer : KSerializer<OutgoingEncryptionRequest> {
        override fun serialize(encoder: Encoder, value: OutgoingEncryptionRequest) {
            @Suppress("NAME_SHADOWING")
            val encoder = encoder as PacketFormat.PacketEncoder
            encoder.encodeString(value.serverId)
            encoder.encodeVarInt(value.publicKey.size)
            encoder.encodeBytes(value.publicKey)
            encoder.encodeVarInt(value.verifyToken.size)
            encoder.encodeBytes(value.verifyToken)
        }

        override fun deserialize(decoder: Decoder): OutgoingEncryptionRequest {
            @Suppress("NAME_SHADOWING")
            val decoder = decoder as PacketFormat.PacketDecoder
            val serverId = decoder.decodeString()
            val publicKey = decoder.decodeBytes(decoder.decodeVarInt())
            val verifyToken = decoder.decodeBytes(decoder.decodeVarInt())
            return OutgoingEncryptionRequest(
                serverId = serverId,
                publicKey = publicKey,
                verifyToken = verifyToken
            )
        }
    }
}

@Serializable
data class OutgoingLoginSuccessPacket(
    val uuid: String,
    val username: String
) : OutgoingPacket

@Serializable
data class OutgoingSetCompressionPacket(
    @Serializable(with = VarIntSerializer::class) val threshold: Int
) : OutgoingPacket

@Serializable
data class OutgoingLoginPluginRequestPacket(
    val messageId: Int,
    val channel: String,
    val data: ByteArray
) : OutgoingPacket {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OutgoingLoginPluginRequestPacket

        if (messageId != other.messageId) return false
        if (channel != other.channel) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = messageId
        result = 31 * result + channel.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }

    @Serializer(forClass = OutgoingLoginPluginRequestPacket::class)
    companion object PacketSerializer : KSerializer<OutgoingLoginPluginRequestPacket> {
        override fun serialize(encoder: Encoder, value: OutgoingLoginPluginRequestPacket) {
            @Suppress("NAME_SHADOWING")
            val encoder = encoder as PacketFormat.PacketEncoder
            encoder.encodeVarInt(value.messageId)
            encoder.encodeString(value.channel)
            encoder.encodeBytes(value.data)
        }

        override fun deserialize(decoder: Decoder): OutgoingLoginPluginRequestPacket {
            @Suppress("NAME_SHADOWING")
            val decoder = decoder as PacketFormat.PacketDecoder
            val messageId = decoder.decodeVarInt()
            val channel = decoder.decodeString()
            val data = decoder.decodeBytes(decoder.remainingBytes())
            return OutgoingLoginPluginRequestPacket(
                messageId = messageId,
                channel = channel,
                data = data
            )
        }
    }
}