package ai.haruhi.minecraft.networking

import ai.haruhi.minecraft.networking.handshaking.HandshakeProtocolState
import ai.haruhi.minecraft.networking.serialization.OutgoingPacket
import ai.haruhi.minecraft.networking.serialization.PacketFormat
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class PacketInboundHandler(
    private val channel: Channel
) : ChannelInboundHandlerAdapter() {

    var protocolState: ProtocolState = HandshakeProtocolState(this)

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is ByteBuf) error("Got random message: $msg")
        if (msg.readableBytes() == 0) {
            msg.release()
            error("Got empty packet")
        }

        val packet = try {
            packetFormat.load(protocolState.packetSerializer, msg)
        } finally {
            msg.release()
        }

        println("Got packet: $packet")
        protocolState.handlePacket(packet)
    }

    fun sendPacket(packet: OutgoingPacket) {
        println("Sending packet: $packet")
        val packetBuffer = channel.alloc().ioBuffer()
        packetFormat.dumpTo(protocolState.packetSerializer, packet, packetBuffer)
        channel.writeAndFlush(packetBuffer)
    }

    companion object {
        private val packetFormat = PacketFormat()
        const val PIPELINE_NAME = "protocol-handler"
    }
}