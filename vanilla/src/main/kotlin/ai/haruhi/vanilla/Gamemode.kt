package ai.haruhi.vanilla

enum class Gamemode(val id: Int) {
    NONE(-1),
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);

    companion object {
        fun getById(id: Int): Gamemode? = values().find { it.id == id }
    }
}