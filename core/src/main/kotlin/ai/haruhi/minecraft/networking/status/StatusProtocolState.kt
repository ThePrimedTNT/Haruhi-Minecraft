package ai.haruhi.minecraft.networking.status

import ai.haruhi.minecraft.networking.PacketInboundHandler
import ai.haruhi.minecraft.networking.ProtocolState
import ai.haruhi.minecraft.networking.serialization.Packet
import ai.haruhi.minecraft.networking.serialization.PacketSerializer

class StatusProtocolState(
    private val packetHandler: PacketInboundHandler
) : ProtocolState() {
    override val packetSerializer = StatusProtocolState.packetSerializer

    override fun handlePacket(packet: Packet) {
        when (packet) {
            is IncomingRequestPacket -> {
                // Temporary
                //language=JSON
                packetHandler.sendPacket(
                    OutgoingResponsePacket(
                        """{
  "version": {
    "name": "KJP12",
    "protocol": 47
  },
  "players": {
    "max": 100,
    "online": 5,
    "sample": [
      {
        "name": "thinkofdeath",
        "id": "4566e69f-c907-48ee-8d71-d7ba5aa00d20"
      }
    ]
  },
  "description": {
    "text": "Hello world"
  },
  "favicon": "data:image/png;base64,<data>"
}"""
                    )
                )
            }
            is IncomingPingPacket -> {
                packetHandler.sendPacket(
                    OutgoingPongPacket(
                        packet.payload
                    )
                )
            }
        }
    }

    companion object {
        private val packetSerializer = PacketSerializer(
            incomingMapping = mapOf(
                0x00 to PacketSerializer.Entry(IncomingRequestPacket.serializer()),
                0x01 to PacketSerializer.Entry(IncomingPingPacket.serializer())
            ),
            outgoingMapping = mapOf(
                0x00 to PacketSerializer.Entry(OutgoingResponsePacket.serializer()),
                0x01 to PacketSerializer.Entry(OutgoingPongPacket.serializer())
            )
        )
    }
}