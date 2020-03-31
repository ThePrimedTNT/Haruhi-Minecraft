package ai.haruhi.vanilla

import kotlinx.serialization.Properties
import java.io.File

object VanillaMinecraft {

    @JvmStatic
    fun main(args: Array<String>) {
        val settings = loadSettingsFile(File("./server.properties"))
        println("Loaded settings:\n$settings")
    }

}