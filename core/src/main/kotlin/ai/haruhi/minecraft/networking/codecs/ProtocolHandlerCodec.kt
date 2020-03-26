package ai.haruhi.minecraft.networking.codecs

import ai.haruhi.minecraft.networking.ProtocolState
import ai.haruhi.minecraft.networking.serialization.PacketFormat
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class ProtocolHandlerCodec : ChannelInboundHandlerAdapter() {

    private var protocolState: ProtocolState = ProtocolState.HANDSHAKING

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is ByteBuf) error("Got random message: $msg")
        if (msg.readableBytes() == 0) error("Got empty packet")

        val packet = packetFormat.load(protocolState.serializer, msg)

        println("Got packet: $packet")
    }

    companion object {
        private val packetFormat = PacketFormat()
        const val PIPELINE_NAME = "protocol-handler"
    }
}