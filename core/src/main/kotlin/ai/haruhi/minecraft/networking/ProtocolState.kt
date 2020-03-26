package ai.haruhi.minecraft.networking

import ai.haruhi.minecraft.networking.handshaking.IncomingHandshakePacket
import ai.haruhi.minecraft.networking.serialization.PacketSerializer

enum class ProtocolState(val serializer: PacketSerializer) {
    HANDSHAKING(PacketSerializer(
        0x0 to PacketSerializer.Entry(IncomingHandshakePacket.serializer())
    )),
    STATUS(PacketSerializer()),
    LOGIN(PacketSerializer()),
    PLAY(PacketSerializer())
}