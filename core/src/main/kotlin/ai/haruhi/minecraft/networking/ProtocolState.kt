package ai.haruhi.minecraft.networking

import ai.haruhi.minecraft.networking.serialization.Packet
import ai.haruhi.minecraft.networking.serialization.PacketSerializer

abstract class ProtocolState {
    abstract val packetSerializer: PacketSerializer

    abstract fun handlePacket(packet: Packet)
}