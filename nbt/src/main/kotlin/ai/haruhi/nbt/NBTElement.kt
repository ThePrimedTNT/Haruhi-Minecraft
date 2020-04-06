package ai.haruhi.nbt

import kotlinx.serialization.Serializable

@Serializable(with = NBTElementSerializer::class)
interface NBTElement

@Serializable(with = NBTByteSerializer::class)
data class NBTByte(val value: Byte) : NBTElement {
    override fun toString(): String = "NBTByte($value)"
}

@Serializable(with = NBTShortSerializer::class)
data class NBTShort(val value: Short) : NBTElement {
    override fun toString(): String = "NBTShort($value)"
}

@Serializable(with = NBTIntSerializer::class)
data class NBTInt(val value: Int) : NBTElement {
    override fun toString(): String = "NBTInt($value)"
}

@Serializable(with = NBTLongSerializer::class)
data class NBTLong(val value: Long) : NBTElement {
    override fun toString(): String = "NBTLong($value)"
}

@Serializable(with = NBTFloatSerializer::class)
data class NBTFloat(val value: Float) : NBTElement {
    override fun toString(): String = "NBTFloat($value)"
}

@Serializable(with = NBTDoubleSerializer::class)
data class NBTDouble(val value: Double) : NBTElement {
    override fun toString(): String = "NBTDouble($value)"
}

@Serializable(with = NBTByteArraySerializer::class)
data class NBTByteArray(val value: ByteArray) : NBTElement {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NBTByteArray

        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = value.joinToString(prefix = "NBTByteArray[", separator = ", ", postfix = "]")
}

@Suppress("FunctionName")
fun NBTByteArray(vararg elements: Byte) = NBTByteArray(elements)

@Serializable(with = NBTStringSerializer::class)
data class NBTString(val value: String) : NBTElement {
    override fun toString(): String = "NBTString($value)"
}

@Serializable(with = NBTListSerializer::class)
data class NBTList<T : NBTElement>(val value: List<T> = emptyList()) : NBTElement {

    constructor(vararg elements: T) : this(listOf(*elements))

    override fun toString(): String = value.joinToString(prefix = "NBTList[", separator = ", ", postfix = "]")
}

@Serializable(with = NBTCompoundSerializer::class)
data class NBTCompound(val value: Map<String, NBTElement>) : NBTElement {

    constructor(vararg values: Pair<String, NBTElement>) : this(values.toMap())

    override fun toString(): String =
        buildString {
            append("NBTCompound{\n")
            append(buildString {
                val valueIt = value.iterator()
                while (valueIt.hasNext()) {
                    val (name, element) = valueIt.next()
                    append(name)
                    append(": ")
                    append(element.toString())
                    if (valueIt.hasNext()) append("\n")
                }
            }.prependIndent())
            append("\n}")
        }
}

@Serializable(with = NBTIntArraySerializer::class)
data class NBTIntArray(val value: IntArray) : NBTElement {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NBTIntArray

        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int = value.contentHashCode()
    override fun toString(): String = value.joinToString(prefix = "NBTIntArray[", separator = ", ", postfix = "]")
}

@Suppress("FunctionName")
fun NBTIntArray(vararg elements: Int) = NBTIntArray(elements)

@Serializable(with = NBTLongArraySerializer::class)
data class NBTLongArray(val value: LongArray) : NBTElement {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NBTLongArray

        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int = value.contentHashCode()
    override fun toString(): String = value.joinToString(prefix = "NBTLongArray[", separator = ", ", postfix = "]")
}

@Suppress("FunctionName")
fun NBTLongArray(vararg elements: Long) = NBTLongArray(elements)