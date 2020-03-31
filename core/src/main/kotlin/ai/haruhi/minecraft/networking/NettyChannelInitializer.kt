package ai.haruhi.minecraft.networking

import ai.haruhi.minecraft.networking.codecs.PacketMessageCodec
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import kotlinx.coroutines.channels.BroadcastChannel

internal class NettyChannelInitializer(
    private val eventBus: BroadcastChannel<NetworkEvent>
) : ChannelInitializer<Channel>() {
    override fun initChannel(ch: Channel) {
        println("Got connection from: ${ch.remoteAddress()}")
        ch.pipeline().apply {
            addLast(PacketMessageCodec.PIPELINE_NAME, PacketMessageCodec())
            addLast(NetworkPacketManager.PIPELINE_NAME, NetworkPacketManager(eventBus, ch))
        }
    }
}