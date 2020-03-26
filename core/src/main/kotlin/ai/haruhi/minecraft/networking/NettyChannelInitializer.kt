package ai.haruhi.minecraft.networking

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer

internal class NettyChannelInitializer : ChannelInitializer<Channel>() {
    override fun initChannel(ch: Channel) {
        println("Incoming connection from ${ch.remoteAddress()}")
    }
}