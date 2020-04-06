package ai.haruhi.nbt

import org.junit.Test
import java.io.InputStream
import java.util.zip.GZIPInputStream

class NBTElementTests {

    private val testData: Map<String, NBTCompound> = mapOf(
        "singleByteTest" to NBTCompound("singleByte" to NBTByte(1)),
        "singleShortTest" to NBTCompound("singleShort" to NBTShort(2)),
        "singleIntTest" to NBTCompound("singleInt" to NBTInt(3)),
        "singleLongTest" to NBTCompound("singleLong" to NBTLong(4)),
        "singleFloatTest" to NBTCompound("singleFloat" to NBTFloat(5.2f)),
        "singleDoubleTest" to NBTCompound("singleDouble" to NBTDouble(6.5)),
        "emptyByteArrayTest" to NBTCompound("empty" to NBTByteArray()),
        "filledByteArrayTest" to NBTCompound("filledByteArray" to NBTByteArray(2, 7, 8, 3, 4, 6)),
        "singleStringTest" to NBTCompound("singleString" to NBTString("Hello World!")),
        "emptyListTest" to NBTCompound("empty" to NBTList<NBTElement>()),
        "filledListTest" to NBTCompound(
            "filledList" to NBTList(NBTInt(5), NBTInt(2), NBTInt(6), NBTInt(7), NBTInt(2))
        ),
        "emptyCompoundTest" to NBTCompound(),
        "emptyIntArrayTest" to NBTCompound("empty" to NBTIntArray()),
        "filledIntArrayTest" to NBTCompound("filledIntArray" to NBTIntArray(8, 5, 3, 1, 67, 4, 2)),
        "emptyLongArrayTest" to NBTCompound("empty" to NBTLongArray()),
        "filledLongArrayTest" to NBTCompound("filledLongArray" to NBTLongArray(5, 3, 7, 6, 34, 4, 7456, 54, 34, 56))
    )

    @Test
    fun testDecode() {
        testData.forEach { (file, expected) ->
            loadFile("/$file.nbt") { inputStream ->
                testDecode(file, GZIPInputStream(inputStream), expected)
            }
        }
    }

    private fun testDecode(name: String, inputStream: InputStream, expected: NBTCompound) {
        val decodedNBT = nbtFormat.load(NBTElementSerializer, inputStream)

        assert(decodedNBT == expected) {
            "Decode Test: $name\n" +
                "Expected: $expected\n" +
                "Got: $decodedNBT"
        }
    }

    @Test
    fun testEncode() {
        testData.forEach { (file, input) ->
            loadFile("/$file.nbt") { inputStream ->
                testEncode(file, input, GZIPInputStream(inputStream))
            }
        }
    }

    private fun testEncode(name: String, input: NBTCompound, expectedStream: InputStream) {
        val decodedNBT = nbtFormat.dump(NBTElementSerializer, input)

        assert(decodedNBT.contentEquals(expectedStream.readAllBytes())) {
            "Encode Test Failed: $name\n" +
                "Input: $input"
        }
    }
}