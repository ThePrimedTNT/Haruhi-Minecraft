package ai.haruhi.vanilla

import ai.haruhi.minecraft.HaruhiMinecraft
import ai.haruhi.minecraft.networking.NetworkEvent
import ai.haruhi.minecraft.networking.ProtocolVersion
import ai.haruhi.minecraft.networking.status.IncomingRequestPacket
import ai.haruhi.minecraft.networking.status.OutgoingResponsePacket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.io.File

fun main(args: Array<String>) {
    VanillaMinecraft.createServer(args)
}

class VanillaMinecraft(private val settings: Settings) {

    val server = HaruhiMinecraft(settings.serverPort)

    val version = ProtocolVersion.v1_15_2

    init {
        server.start()

        server.launch {
            server.eventBus.openSubscription()
                .consumeEach { event ->
                    if (event is NetworkEvent.PacketReceived) {
                        handlePacketReceived(event)
                    }
                }
        }
    }

    private fun handlePacketReceived(event: NetworkEvent.PacketReceived) {
        when (event.packet) {
            is IncomingRequestPacket -> {
                //language=JSON
                event.networkPacketManager.sendPacket(
                    OutgoingResponsePacket(
                        """{
  "version": {
    "name": "${version.mcVersion}",
    "protocol": ${version.protocolNum}
  },
  "players": {
    "max": ${settings.maxPlayers},
    "online": 1,
    "sample": [
      {
        "name": "thinkofdeath",
        "id": "4566e69f-c907-48ee-8d71-d7ba5aa00d20"
      }
    ]
  },
  "description": {
    "text": "${settings.motd}"
  },
  "favicon": "data:image/png;base64,<data>"
}"""
                    )
                )
            }
        }
    }

    companion object {
        fun createServer(args: Array<String>): VanillaMinecraft {
            val settings = loadSettingsFile(File("./server.properties"))
            println("Loaded settings:\n$settings")
            return VanillaMinecraft(settings)
        }
    }
}