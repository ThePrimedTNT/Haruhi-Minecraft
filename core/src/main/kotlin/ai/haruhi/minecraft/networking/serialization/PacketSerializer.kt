package ai.haruhi.minecraft.networking.serialization

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlin.reflect.KClass

interface Packet
interface IncomingPacket : Packet
interface OutgoingPacket : Packet

class PacketSerializer<PACKET : Packet>(
    private val packetMapping: Map<Int, Entry<out PACKET>> = emptyMap()
) : KSerializer<Packet> {

    override val descriptor = SerialDescriptor("PacketSerializer")

    override fun deserialize(decoder: Decoder): Packet {
        val packetDecoder = decoder as PacketFormat.PacketDecoder
        val packetId = packetDecoder.decodeVarInt()
        val entry = packetMapping[packetId] ?: error("No mapping available for packet Id: $packetId")
        return packetDecoder.decodeSerializableValue(entry.serializer)
    }

    override fun serialize(encoder: Encoder, value: Packet) {
        val packetEncoder = encoder as PacketFormat.PacketEncoder
        val packetClass = value::class
        val entry = packetMapping.entries.find { it.value.packetClass == packetClass }
            ?: error("No mapping available for packet: ${packetClass::class.simpleName}")
        packetEncoder.encodeVarInt(entry.key)
        @Suppress("UNCHECKED_CAST")
        packetEncoder.encodeSerializableValue(entry.value.serializer as KSerializer<Packet>, value)
    }

    data class Entry<PACKET : Packet>(
        val packetClass: KClass<PACKET>,
        val serializer: KSerializer<PACKET>
    )

    companion object {
        @Suppress("FunctionName")
        inline fun <reified PACKET : Packet> Entry(serializer: KSerializer<PACKET>) =
            Entry(PACKET::class, serializer)
    }
}

