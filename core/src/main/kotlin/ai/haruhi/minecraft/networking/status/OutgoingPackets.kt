package ai.haruhi.minecraft.networking.status

import ai.haruhi.minecraft.networking.serialization.OutgoingPacket
import kotlinx.serialization.Serializable

@Serializable
data class OutgoingResponsePacket(
    val response: String
) : OutgoingPacket

@Serializable
data class OutgoingPongPacket(
    val payload: Long
) : OutgoingPacket