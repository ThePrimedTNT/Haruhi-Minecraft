package ai.haruhi.minecraft.networking.status

import ai.haruhi.minecraft.networking.NetworkEvent
import ai.haruhi.minecraft.networking.NetworkPacketManager
import ai.haruhi.minecraft.networking.ProtocolState
import ai.haruhi.minecraft.networking.serialization.IncomingPacket
import ai.haruhi.minecraft.networking.serialization.OutgoingPacket
import ai.haruhi.minecraft.networking.serialization.PacketSerializer
import kotlinx.coroutines.channels.sendBlocking

class StatusProtocolState(
    private val networkPacketManager: NetworkPacketManager
) : ProtocolState() {
    override val incomingPacketSerializer = StatusProtocolState.incomingPacketSerializer
    override val outgoingPacketSerializer = StatusProtocolState.outgoingPacketSerializer

    override fun handlePacket(packet: IncomingPacket) {
        when (packet) {
            is IncomingRequestPacket -> {
                // Temporary
                networkPacketManager.eventBus.sendBlocking(
                    NetworkEvent.PacketReceived(
                        networkPacketManager,
                        packet
                    )
                )
            }
            is IncomingPingPacket -> {
                networkPacketManager.sendPacket(
                    OutgoingPongPacket(
                        packet.payload
                    )
                )
            }
        }
    }

    companion object {
        private val incomingPacketSerializer = PacketSerializer<IncomingPacket>(
            mapOf(
                0x00 to PacketSerializer.Entry(IncomingRequestPacket.serializer()),
                0x01 to PacketSerializer.Entry(IncomingPingPacket.serializer())
            )
        )
        private val outgoingPacketSerializer = PacketSerializer<OutgoingPacket>(
            mapOf(
                0x00 to PacketSerializer.Entry(OutgoingResponsePacket.serializer()),
                0x01 to PacketSerializer.Entry(OutgoingPongPacket.serializer())
            )
        )
    }
}