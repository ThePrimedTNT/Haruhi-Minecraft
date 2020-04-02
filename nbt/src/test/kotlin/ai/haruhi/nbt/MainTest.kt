package ai.haruhi.nbt

import kotlinx.serialization.Serializable
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

    @Serializable
    data class ClassTest(
        val `81852b3f-3a38-4e19-a33b-e346e7779c21`: String,
        val `a7aa5100-d37a-49c5-a670-e3d553a0a0c5`: String,
        val `a506852d-6513-4807-927a-ecf9e92c5f6f`: String,
        val `これは可能です！`: String,
        val `Anything is possible!`: NestedClassTest
    ) {
        @Serializable
        data class NestedClassTest(
            val str: String,
            val intArr: IntArray,
            val byte: Byte,
            val double: Double,
            val strList: List<String>,
            val byteArr: ByteArray,
            val short: Short,
            val integer: Int,
            val float: Float,
            val longArr: LongArray,
            val long: Long
        )
    }

    @Test
    fun testClassDecoding() {
        val nbt = javaClass.classLoader.getResourceAsStream("test.nbt")!!.use { fileStream ->
            nbtFormat.load(ClassTest.serializer(), GZIPInputStream(fileStream))
        }

        println(nbt)
    }
}