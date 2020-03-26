package ai.haruhi.minecraft.networking.serialization

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlin.reflect.KClass

interface Packet
interface IncomingPacket : Packet
interface OutgoingPacket : Packet

class PacketSerializer(
    private val incomingMapping: Map<Int, Entry<out IncomingPacket>> = emptyMap(),
    private val outgoingMapping: Map<Int, Entry<out OutgoingPacket>> = emptyMap()
) : KSerializer<Packet> {

    override val descriptor = SerialDescriptor("PacketSerializer")

    override fun deserialize(decoder: Decoder): Packet {
        val packetDecoder = decoder as PacketFormat.PacketDecoder
        val packetId = packetDecoder.decodeVarInt()
        val entry = incomingMapping[packetId] ?: error("No incoming mapping available for packet Id: $packetId")
        return packetDecoder.decodeSerializableValue(entry.serializer)
    }

    override fun serialize(encoder: Encoder, value: Packet) {
        val packetEncoder = encoder as PacketFormat.PacketEncoder
        val packetClass = value::class
        val entry = outgoingMapping.entries.find { it.value.packetClass == packetClass }
            ?: error("No outgoing mapping available for packet: ${packetClass::class.simpleName}")
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
    }
}

