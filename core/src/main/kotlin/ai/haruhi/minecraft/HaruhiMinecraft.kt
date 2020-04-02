package ai.haruhi.minecraft

import ai.haruhi.minecraft.networking.NettyChannelInitializer
import ai.haruhi.minecraft.networking.NetworkEvent
import ai.haruhi.minecraft.networking.ProtocolVersion
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.BroadcastChannel
import kotlin.coroutines.CoroutineContext

class HaruhiMinecraft(
    val port: Int
) : CoroutineScope {

    val version = ProtocolVersion.v1_15_2

    private val serverBootstrap = ServerBootstrap()
    override val coroutineContext: CoroutineContext
    val eventBus = BroadcastChannel<NetworkEvent>(capacity = 5)

    init {
        val eventLoopGroup: EventLoopGroup
        val channelClass: Class<out ServerChannel>

        when {
            // MacOS/BSD native transport
            KQueue.isAvailable() -> {
                eventLoopGroup = KQueueEventLoopGroup()
                channelClass = KQueueServerSocketChannel::class.java
            }
            // Linux native transport
            Epoll.isAvailable() -> {
                eventLoopGroup = EpollEventLoopGroup()
                channelClass = EpollServerSocketChannel::class.java
            }
            // Generic
            else -> {
                eventLoopGroup = NioEventLoopGroup()
                channelClass = NioServerSocketChannel::class.java
            }
        }

        coroutineContext = eventLoopGroup.asCoroutineDispatcher() + Job()

        serverBootstrap
            .group(eventLoopGroup, eventLoopGroup)
            .channel(channelClass)
            .childHandler(NettyChannelInitializer(eventBus))
    }

    fun start() {
        serverBootstrap.bind(port)
    }
}