package ai.haruhi.minecraft.networking

import ai.haruhi.minecraft.networking.handshaking.IncomingHandshakePacket
import ai.haruhi.minecraft.networking.serialization.PacketFormat
import ai.haruhi.minecraft.networking.serialization.PacketSerializer
import ai.haruhi.minecraft.networking.serialization.VarIntSerializer
import ai.haruhi.minecraft.networking.serialization.VarLongSerializer
import org.junit.Test

class SerializationTest {

    private val packetFormat = PacketFormat()

    private val varIntTestData = mapOf(
        0 to ubyteArrayOf(0u),
        1 to ubyteArrayOf(1u),
        2 to ubyteArrayOf(2u),
        127 to ubyteArrayOf(127u),
        128 to ubyteArrayOf(128u, 1u),
        255 to ubyteArrayOf(255u, 1u),
        2147483647 to ubyteArrayOf(255u, 255u, 255u, 255u, 7u),
        -1 to ubyteArrayOf(255u, 255u, 255u, 255u, 15u),
        -2147483648 to ubyteArrayOf(128u, 128u, 128u, 128u, 8u)
    )

    private val varLongTestData = mapOf(
        0L to ubyteArrayOf(0u),
        1L to ubyteArrayOf(1u),
        2L to ubyteArrayOf(2u),
        127L to ubyteArrayOf(127u),
        128L to ubyteArrayOf(128u, 1u),
        255L to ubyteArrayOf(255u, 1u),
        2147483647L to ubyteArrayOf(255u, 255u, 255u, 255u, 7u),
        9223372036854775807L to ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 127u),
        -1L to ubyteArrayOf(255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 255u, 1u),
        -2147483648L to ubyteArrayOf(128u, 128u, 128u, 128u, 248u, 255u, 255u, 255u, 255u, 1u)
        // -9223372036854775808L to ubyteArrayOf(128u, 128u, 128u, 128u, 128u, 128u, 128u, 128u, 128u, 1u),

    )

    @Test
    fun testVarIntEncoding() {
        varIntTestData.forEach { value, expectedEncodedValue ->
            val encodedValue = packetFormat.dump(VarIntSerializer, value)

            assert(expectedEncodedValue.contentEquals(encodedValue.toUByteArray())) {
                buildString {
                    append("Failed to encode VarInt ($value)\nExpected: ")
                    expectedEncodedValue.joinTo(this)
                    append("\nGot: ")
                    encodedValue.joinTo(this) { it.toUByte().toString() }
                }
            }
        }
    }

    @Test
    fun testVarIntDecoding() {
        varIntTestData.forEach { expectedDecodedValue, encodedValue ->
            val decodedValue = packetFormat.load(VarIntSerializer, encodedValue.toByteArray())

            assert(decodedValue == expectedDecodedValue) {
                buildString {
                    append("Failed to decode VarInt (")
                    encodedValue.joinTo(this)
                    append(")\nExpected: ")
                    append(expectedDecodedValue)
                    append("\nGot: ")
                    append(decodedValue)
                }
            }
        }
    }

    @Test
    fun testVarLongEncoding() {
        varLongTestData.forEach { value, expectedEncodedValue ->
            val encodedValue = packetFormat.dump(VarLongSerializer, value)

            assert(expectedEncodedValue.contentEquals(encodedValue.toUByteArray())) {
                buildString {
                    append("Failed to encode VarLong ($value)\nExpected: ")
                    expectedEncodedValue.joinTo(this)
                    append("\nGot: ")
                    encodedValue.joinTo(this) { it.toUByte().toString() }
                }
            }
        }
    }

    @Test
    fun testVarLongDecoding() {
        varLongTestData.forEach { expectedDecodedValue, encodedValue ->
            val decodedValue = packetFormat.load(VarLongSerializer, encodedValue.toByteArray())

            assert(decodedValue == expectedDecodedValue) {
                buildString {
                    append("Failed to decode VarLong (")
                    encodedValue.joinTo(this)
                    append(")\nExpected: ")
                    append(expectedDecodedValue)
                    append("\nGot: ")
                    append(decodedValue)
                }
            }
        }
    }

    @Test
    fun test() {
        val serializer = PacketSerializer(
            packetMapping = mapOf(
                0x0 to PacketSerializer.Entry(IncomingHandshakePacket.serializer())
            )
        )

        val handshakePacket = IncomingHandshakePacket(
            protocolVersion = ProtocolVersion.CURRENT.protocolNum,
            serverAddress = "127.0.0.1",
            serverPort = 25565,
            nextState = 1
        )

        val encodedPacket = packetFormat.dump(serializer, handshakePacket)
        val decodedPacket = packetFormat.load(serializer, encodedPacket)

        assert(handshakePacket == decodedPacket) {
            buildString {
                append("Failed to serialize Handshake Packet\nInput:")
                append(handshakePacket.toString())
                append("\nDecoded: ")
                append(decodedPacket.toString())
            }
        }
    }
}

