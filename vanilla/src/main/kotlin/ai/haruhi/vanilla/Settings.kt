package ai.haruhi.vanilla

import ai.haruhi.vanilla.util.JavaPropertiesFormat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class Settings(
    val debug: Boolean = false,
    @SerialName("online-mode") val onlineMode: Boolean = true,
    @SerialName("prevent-proxy-connections") val preventProxyConnections: Boolean = false,
    @SerialName("server-ip") val serverIp: String = "",
    @SerialName("spawn-animals") val spawnAnimals: Boolean = true,
    @SerialName("spawn-npcs") val spawnNpcs: Boolean = true,
    @SerialName("pvp") val pvp: Boolean = true,
    @SerialName("allow-flight") val allowFlight: Boolean = false,
    @SerialName("resource-pack") val resourcePack: String = "",
    @SerialName("motd") val motd: String = "A Minecraft Server",
    @SerialName("force-gamemode") val forceGamemode: Boolean = false,
    @SerialName("enforce-whitelist") val enforceWhitelist: Boolean = false,
    @SerialName("generate-structures") val generateStructures: Boolean = true,
    @SerialName("difficulty") val difficulty: Difficulty = Difficulty.EASY,
    @SerialName("gamemode") val gamemode: Gamemode = Gamemode.SURVIVAL,
    @SerialName("level-name") val levelName: String = "world",
    @SerialName("level-seed") val levelSeed: String = "",
    @SerialName("level-type") val levelType: WorldType = WorldType.NORMAL,
    @SerialName("generator-settings") val generatorSettings: String = "",
    @SerialName("server-port") val serverPort: Int = 25565,
    @SerialName("max-build-height") val maxBuildHeight: Int = 256,
    @SerialName("announce-player-achievements") val announcePlayerAchievements: Boolean = true,
    @SerialName("enable-query") val enableQuery: Boolean = false,
    @SerialName("query.port") val queryPort: Int = 25565,
    @SerialName("enable-rcon") val enableRcon: Boolean = false,
    @SerialName("rcon.port") val rconPort: Int = 25585,
    @SerialName("rcon.password") val rconPassword: String = "",
    @SerialName("resource-pack-hash") val resourcePackHash: String = "",
    @SerialName("resource-pack-sha1") val resourcePackSha1: String = "",
    @SerialName("hardcore") val hardcore: Boolean = false,
    @SerialName("allow-nether") val allowNether: Boolean = true,
    @SerialName("spawn-monsters") val spawnMonsters: Boolean = true,
    @SerialName("snooper-enabled") val snooperEnabled: Boolean = false,
    @SerialName("use-native-transport") val useNativeTransport: Boolean = true,
    @SerialName("enable-command-block") val enableCommandBlock: Boolean = false,
    @SerialName("spawn-protection") val spawnProtection: Int = 16,
    @SerialName("op-permission-level") val opPermissionLevel: Int = 4,
    @SerialName("function-permission-level") val functionPermissionLevel: Int = 2,
    @SerialName("max-tick-time") val maxTickTime: Long = 1L * 60L * 1000L,
    @SerialName("view-distance") val viewDistance: Int = 10,
    @SerialName("max-players") val maxPlayers: Int = 20,
    @SerialName("network-compression-threshold") val networkCompressionThreshold: Int = 256,
    @SerialName("broadcast-rcon-to-ops") val broadcastRconToOps: Boolean = true,
    @SerialName("broadcast-console-to-ops") val broadcastConsoleToOps: Boolean = true,
    @SerialName("max-world-size") val maxWorldSize: Int = 29999984,
    @SerialName("player-idle-timeout") val playerIdleTimeout: Int = 0,
    @SerialName("white-list") val whiteList: Boolean = false
)

internal fun loadSettingsFile(file: File): Settings {
    val format = JavaPropertiesFormat()
    val loadedSettings = if (file.exists()) {
        format.load(Settings.serializer(), file)
    } else Settings()

    format.dumpTo(Settings.serializer(), loadedSettings, file, "Minecraft server properties")

    return loadedSettings
}