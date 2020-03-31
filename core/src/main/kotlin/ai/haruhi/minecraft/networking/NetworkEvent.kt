package ai.haruhi.minecraft.networking

import ai.haruhi.minecraft.networking.serialization.IncomingPacket

sealed class NetworkEvent {
    data class PacketReceived(val networkPacketManager: NetworkPacketManager, val packet: IncomingPacket) :
        NetworkEvent()
}