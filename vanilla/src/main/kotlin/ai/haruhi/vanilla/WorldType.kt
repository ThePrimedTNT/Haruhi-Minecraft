package ai.haruhi.vanilla

enum class WorldType(val id: Int, val internalName: String) {
    NORMAL(0, "default"),
    FLAT(1, "flat"),
    LARGE_BIOMES(2, "largeBiomes"),
    AMPLIFIED(3, "amplified"),
    CUSTOMIZED(4, "customized"),
    BUFFET(5, "buffet"),
    DEBUG_ALL_BLOCK_STATES(7, "debug_all_block_states"),
    NORMAL_1_1(8, "default_1_1");

    companion object {
        fun getById(id: Int): WorldType? = values().find { it.id == id }
        fun getByName(name: String): WorldType? = values().find { it.internalName.equals(name, ignoreCase = true) }
    }
}