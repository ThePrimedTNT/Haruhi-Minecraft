package ai.haruhi.minecraft.networking.serialization

import ai.haruhi.minecraft.networking.handshaking.IncomingHandshakePacket
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlin.reflect.KClass

interface Packet

class PacketSerializer(
    private val mapping: Map<Int, Entry<*>>
) : KSerializer<Packet> {

    constructor(vararg mapping: Pair<Int, Entry<*>>) : this(mapping.toMap())

    override val descriptor = SerialDescriptor("PacketSerializer")

    override fun deserialize(decoder: Decoder): Packet {
        val packetDecoder = decoder as PacketFormat.PacketDecoder
        val packetId = packetDecoder.decodeVarInt()
        val entry = mapping[packetId] ?: error("No mapping available for packet Id: $packetId")
        return packetDecoder.decodeSerializableValue(entry.serializer)
    }

    override fun serialize(encoder: Encoder, value: Packet) {
        val packetEncoder = encoder as PacketFormat.PacketEncoder
        val packetClass = value::class
        val entry = mapping.entries.find { it.value.packetClass == packetClass }
            ?: error("No mapping available for packet: ${packetClass::class.simpleName}")
        packetEncoder.encodeVarInt(entry.key)
        @Suppress("UNCHECKED_CAST")
        packetEncoder.encodeSerializableValue(entry.value.serializer as KSerializer<Packet>, value)
    }

    data class Entry<T : Packet>(
        val packetClass: KClass<T>,
        val serializer: KSerializer<T>
    )

    companion object {
        @Suppress("FunctionName")
        inline fun <reified PACKET : Packet> Entry(serializer: KSerializer<PACKET>) =
            Entry(PACKET::class, serializer)

        val HANDSHAKING = PacketSerializer(
            mapOf(
                0x0 to Entry(IncomingHandshakePacket.serializer())
            )
        )
    }
}

