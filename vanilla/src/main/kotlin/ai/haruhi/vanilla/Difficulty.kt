package ai.haruhi.vanilla

enum class Difficulty {
    PEACEFUL,
    EASY,
    NORMAL,
    HARD;

    companion object {
        fun getById(id: Int) = values()[id]
    }
}