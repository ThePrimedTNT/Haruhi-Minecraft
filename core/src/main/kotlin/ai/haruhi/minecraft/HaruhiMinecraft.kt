package ai.haruhi.minecraft

import ai.haruhi.minecraft.networking.NettyChannelInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.EventLoopGroup
import io.netty.channel.ServerChannel
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.kqueue.KQueue
import io.netty.channel.kqueue.KQueueEventLoopGroup
import io.netty.channel.kqueue.KQueueServerSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel

class HaruhiMinecraft(
    private val port: Int
) {

    private val serverBootstrap = ServerBootstrap().apply {
        val parentGroup: EventLoopGroup
        val childGroup: EventLoopGroup
        val channelClass: Class<out ServerChannel>

        when {
            // MacOS/BSD native transport
            KQueue.isAvailable() -> {
                parentGroup = KQueueEventLoopGroup()
                childGroup = KQueueEventLoopGroup()
                channelClass = KQueueServerSocketChannel::class.java
            }
            // Linux native transport
            Epoll.isAvailable() -> {
                parentGroup = EpollEventLoopGroup()
                childGroup = EpollEventLoopGroup()
                channelClass = EpollServerSocketChannel::class.java
            }
            // Generic
            else -> {
                parentGroup = NioEventLoopGroup()
                childGroup = NioEventLoopGroup()
                channelClass = NioServerSocketChannel::class.java
            }
        }

        group(parentGroup, childGroup)
        channel(channelClass)

        childHandler(NettyChannelInitializer())
    }

    fun start() {
        serverBootstrap.bind(port)
    }
}