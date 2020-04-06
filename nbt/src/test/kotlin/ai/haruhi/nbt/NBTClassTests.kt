package ai.haruhi.nbt

import org.junit.Test
import java.util.zip.GZIPInputStream

class NBTClassTests {

    private val expectedDecodedClass = ClassTest(
        `81852b3f-3a38-4e19-a33b-e346e7779c21` = "This",
        `a7aa5100-d37a-49c5-a670-e3d553a0a0c5` = "is",
        `a506852d-6513-4807-927a-ecf9e92c5f6f` = "a",
        `これは可能です！` = "test",
        `Anything is possible!` = ClassTest.NestedClassTest(
            str = "This is a lengthy test string. ¿Todavía está disponible? まだ利用可能です？",
            intArr = intArrayOf(6942, 4096),
            byte = 123,
            double = 2.2,
            strList = listOf("This", "is", "a", "test.", "Now for ÅÄÖ!"),
            byteArr = byteArrayOf(13, 37),
            short = 6942,
            integer = 2147483647,
            float = 420.1337f,
            longArr = longArrayOf(1337, 1234567890),
            long = 4294967295
        )
    )

    private val expectedDecodedClassWithForcedType = TestForcedCollectionTypeClass(
        normalArray = byteArrayOf(53, 65, 34, 4, -34),
        nbtListAsJvmArray = byteArrayOf(34, 65, 34, 92, -32),
        nbtArrayAsJvmList = listOf(38, 94, 32, -45),
        normalList = listOf(34, 92, 72, 3)
    )

    @Test
    fun decodeToClassTest() {
        val decodedClass = loadFile("/classTest.nbt") {
            nbtFormat.load(ClassTest.serializer(), GZIPInputStream(it))
        }

        assert(decodedClass == expectedDecodedClass) {
            "Decode to class test failed\n" +
                "Got: $decodedClass\n" +
                "Expected: $expectedDecodedClass"
        }
    }

    @Test
    fun encodeFromClassTest() {
        val encodedClass = nbtFormat.dump(ClassTest.serializer(), expectedDecodedClass)

        loadFile("/classTest.nbt") {
            assert(encodedClass.contentEquals(GZIPInputStream(it).readAllBytes())) {
                "Encode Class Test Failed:\n" +
                    "Input: $expectedDecodedClass"
            }
        }
    }

    @Test
    fun decodeClassWithForcedCollectionTypeTest() {
        val decodedClass = loadFile("/classForceCollectionTypeTest.nbt") {
            nbtFormat.load(TestForcedCollectionTypeClass.serializer(), GZIPInputStream(it))
        }

        assert(decodedClass == expectedDecodedClassWithForcedType) {
            "Decode to class with forced collection type test failed\n" +
                "Got: $decodedClass\n" +
                "Expected: $expectedDecodedClass"
        }
    }

    @Test
    fun encodeClassWithForcedCollectionTypeTest() {
        val encodedClass =
            nbtFormat.dump(TestForcedCollectionTypeClass.serializer(), expectedDecodedClassWithForcedType)

        loadFile("/classForceCollectionTypeTest.nbt") {
            val contents = GZIPInputStream(it).readAllBytes()
            assert(encodedClass.contentEquals(contents)) {
                "Encode Class With Forced Collection Type Test Failed:\n" +
                    "Input: $expectedDecodedClassWithForcedType\n" +
                    "Encoded: ${encodedClass.joinToString { it.toUByte().toString() }}\n" +
                    "Expected: ${contents.joinToString { it.toUByte().toString() }}"
            }
        }
    }
}

