package ai.haruhi.minecraft.networking

import ai.haruhi.minecraft.networking.codecs.PacketMessageCodec
import ai.haruhi.minecraft.networking.codecs.ProtocolHandlerCodec
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer

internal class NettyChannelInitializer : ChannelInitializer<Channel>() {
    override fun initChannel(ch: Channel) {
        println("Got connection from: ${ch.remoteAddress()}")
        ch.pipeline().apply {
            addLast(PacketMessageCodec.PIPELINE_NAME, PacketMessageCodec())
            addLast(ProtocolHandlerCodec.PIPELINE_NAME, ProtocolHandlerCodec())
        }
    }
}