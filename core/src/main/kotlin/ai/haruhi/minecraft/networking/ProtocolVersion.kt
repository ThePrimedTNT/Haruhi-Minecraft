package ai.haruhi.minecraft.networking

@Suppress("EnumEntryName")
enum class ProtocolVersion(val mcVersion: String, val protocolNum: Int) {
    v1_15_2_578("1.15.2", 578);

    companion object {
        val CURRENT = v1_15_2_578
    }
}