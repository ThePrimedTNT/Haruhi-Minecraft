package ai.haruhi.minecraft.networking

import ai.haruhi.minecraft.networking.handshaking.ServerBoundHandshakePacket
import ai.haruhi.minecraft.networking.serialization.PacketFormat
import ai.haruhi.minecraft.networking.serialization.VarIntSerializer
import io.netty.buffer.Unpooled
import org.junit.Test

class SerializationTest {

    private val packetFormat = PacketFormat()

    @Test
    fun testVarIntSerialization() {
        val value = 2147483647

        val encodedValue = ByteArray(5)
        packetFormat.dumpTo(VarIntSerializer, value, Unpooled.wrappedBuffer(encodedValue).apply { writerIndex(0) })

        val decodedValue = packetFormat.load(VarIntSerializer, encodedValue)

        println(encodedValue.joinToString(separator = " ") { it.toUByte().toString() })
        println(decodedValue)
    }

    @Test
    fun test() {
        val handshakePacket = ServerBoundHandshakePacket(
            protocolVersion = ProtocolVersion.CURRENT.protocolNum,
            serverAddress = "127.0.0.1",
            serverPort = 25565,
            nextState = 1
        )

        val encodedPacket = packetFormat.dump(ServerBoundHandshakePacket.serializer(), handshakePacket)

        println(encodedPacket.toString(Charsets.UTF_8))

        val decodedPacket = packetFormat.load(ServerBoundHandshakePacket.serializer(), encodedPacket)

        println(decodedPacket)
    }
}