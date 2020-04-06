package ai.haruhi.nbt

import java.io.InputStream

internal val nbtFormat = NBTFormat()

inline fun <reified CLASS, RETURN> CLASS.loadFile(name: String, crossinline block: (InputStream) -> RETURN): RETURN =
    CLASS::class.java.getResourceAsStream(name).use(block)