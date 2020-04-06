package ai.haruhi.nbt

import kotlinx.serialization.Serializable

@Serializable
data class ClassTest(
    val `81852b3f-3a38-4e19-a33b-e346e7779c21`: String,
    val `a7aa5100-d37a-49c5-a670-e3d553a0a0c5`: String,
    val `a506852d-6513-4807-927a-ecf9e92c5f6f`: String,
    @Suppress("NonAsciiCharacters") val `これは可能です！`: String,
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
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as NestedClassTest

            if (str != other.str) return false
            if (!intArr.contentEquals(other.intArr)) return false
            if (byte != other.byte) return false
            if (double != other.double) return false
            if (strList != other.strList) return false
            if (!byteArr.contentEquals(other.byteArr)) return false
            if (short != other.short) return false
            if (integer != other.integer) return false
            if (float != other.float) return false
            if (!longArr.contentEquals(other.longArr)) return false
            if (long != other.long) return false

            return true
        }

        override fun hashCode(): Int {
            var result = str.hashCode()
            result = 31 * result + intArr.contentHashCode()
            result = 31 * result + byte
            result = 31 * result + double.hashCode()
            result = 31 * result + strList.hashCode()
            result = 31 * result + byteArr.contentHashCode()
            result = 31 * result + short
            result = 31 * result + integer
            result = 31 * result + float.hashCode()
            result = 31 * result + longArr.contentHashCode()
            result = 31 * result + long.hashCode()
            return result
        }
    }
}

@Serializable
data class TestForcedCollectionTypeClass(
    val normalArray: ByteArray,
    @NBTListTag(NBTListType.ARRAY) val nbtArrayAsJvmList: List<Byte>,
    @NBTListTag(NBTListType.LIST) val nbtListAsJvmArray: ByteArray,
    val normalList: List<Byte>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestForcedCollectionTypeClass

        if (!normalArray.contentEquals(other.normalArray)) return false
        if (!nbtListAsJvmArray.contentEquals(other.nbtListAsJvmArray)) return false
        if (nbtArrayAsJvmList != other.nbtArrayAsJvmList) return false
        if (normalList != other.normalList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = normalArray.contentHashCode()
        result = 31 * result + nbtListAsJvmArray.contentHashCode()
        result = 31 * result + nbtArrayAsJvmList.hashCode()
        result = 31 * result + normalList.hashCode()
        return result
    }
}