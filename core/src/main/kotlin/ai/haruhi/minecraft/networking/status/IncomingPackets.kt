package ai.haruhi.minecraft.networking.status

import ai.haruhi.minecraft.networking.serialization.IncomingPacket
import kotlinx.serialization.Serializable

@Serializable
class IncomingRequestPacket : IncomingPacket {
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = other is IncomingRequestPacket
    override fun toString(): String = "IncomingRequestPacket()"
}

@Serializable
data class IncomingPingPacket(
    val payload: Long
) : IncomingPacket