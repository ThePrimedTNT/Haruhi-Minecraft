package ai.haruhi.minecraft.networking

import ai.haruhi.minecraft.networking.handshaking.HandshakeProtocolState
import ai.haruhi.minecraft.networking.serialization.IncomingPacket
import ai.haruhi.minecraft.networking.serialization.OutgoingPacket
import ai.haruhi.minecraft.networking.serialization.PacketFormat
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.channels.BroadcastChannel

class NetworkPacketManager(
    val eventBus: BroadcastChannel<NetworkEvent>,
    private val channel: Channel
) : SimpleChannelInboundHandler<ByteBuf>() {

    var protocolState: ProtocolState = HandshakeProtocolState(this)

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
        if (msg.readableBytes() == 0) error("Got empty packet")

        val packet = packetFormat.load(protocolState.incomingPacketSerializer, msg) as IncomingPacket

        println("Got packet: $packet")
        protocolState.handlePacket(packet)
    }

    fun sendPacket(packet: OutgoingPacket) {
        println("Sending packet: $packet")
        val packetBuffer = channel.alloc().ioBuffer()
        packetFormat.dumpTo(protocolState.outgoingPacketSerializer, packet, packetBuffer)
        channel.writeAndFlush(packetBuffer)
    }

    companion object {
        private val packetFormat = PacketFormat()
        const val PIPELINE_NAME = "package-manager"
    }
}