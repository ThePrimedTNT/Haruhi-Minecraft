package ai.haruhi.nbt

import org.junit.Test
import java.util.zip.GZIPInputStream

class MainTest {

    private val nbtFormat = NBTFormat()

    @Test
    fun testLoad() {
        val nbt = javaClass.classLoader.getResourceAsStream("test.nbt")!!.use { fileStream ->
            nbtFormat.load(NBTCompoundSerializer, GZIPInputStream(fileStream))
        }

        println(nbt)
    }
}