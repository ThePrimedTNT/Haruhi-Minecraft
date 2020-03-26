package ai.haruhi.minecraft.networking

import ai.haruhi.minecraft.networking.serialization.IncomingPacket
import ai.haruhi.minecraft.networking.serialization.OutgoingPacket
import ai.haruhi.minecraft.networking.serialization.Packet
import ai.haruhi.minecraft.networking.serialization.PacketSerializer

abstract class ProtocolState {
    abstract val outgoingPacketSerializer: PacketSerializer<OutgoingPacket>
    abstract val incomingPacketSerializer: PacketSerializer<IncomingPacket>

    abstract fun handlePacket(packet: Packet)
}