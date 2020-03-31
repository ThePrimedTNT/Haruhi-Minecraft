package ai.haruhi.minecraft.networking.handshaking

import ai.haruhi.minecraft.networking.NetworkPacketManager
import ai.haruhi.minecraft.networking.ProtocolState
import ai.haruhi.minecraft.networking.serialization.IncomingPacket
import ai.haruhi.minecraft.networking.serialization.OutgoingPacket
import ai.haruhi.minecraft.networking.serialization.PacketSerializer
import ai.haruhi.minecraft.networking.status.StatusProtocolState

class HandshakeProtocolState(
    private val networkPacketManager: NetworkPacketManager
) : ProtocolState() {
    override val incomingPacketSerializer = HandshakeProtocolState.incomingPacketSerializer
    override val outgoingPacketSerializer = HandshakeProtocolState.outgoingPacketSerializer

    override fun handlePacket(packet: IncomingPacket) {
        when (packet) {
            is IncomingHandshakePacket -> {
                when (packet.nextState) {
                    1 -> {
                        println("Changing to status")
                        networkPacketManager.protocolState = StatusProtocolState(networkPacketManager)
                    }
                    2 -> {
                        println("Changing to login")
                    }
                }
            }
        }
    }

    companion object {
        private val incomingPacketSerializer = PacketSerializer<IncomingPacket>(
            mapOf(
                0x0 to PacketSerializer.Entry(IncomingHandshakePacket.serializer())
            )
        )
        private val outgoingPacketSerializer = PacketSerializer<OutgoingPacket>()
    }
}