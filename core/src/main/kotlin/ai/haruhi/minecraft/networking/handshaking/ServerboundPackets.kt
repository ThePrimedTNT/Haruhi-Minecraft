package ai.haruhi.minecraft.networking.handshaking

import ai.haruhi.minecraft.networking.serialization.Packet
import ai.haruhi.minecraft.networking.serialization.VarIntSerializer
import kotlinx.serialization.Serializable

@Serializable
data class IncomingHandshakePacket(
    @Serializable(with = VarIntSerializer::class) val protocolVersion: Int,
    val serverAddress: String,
    val serverPort: Short,
    @Serializable(with = VarIntSerializer::class) val nextState: Int
): Packet {
    init {
        require(serverAddress.length <= 255) {
            "Server Address cannot be over 255 in length"
        }
    }
}
