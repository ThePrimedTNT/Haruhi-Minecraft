package ai.haruhi.nbt

import kotlinx.serialization.Serializable

@Serializable(with = NBTElementSerializer::class)
interface NBTElement

@Serializable(with = NBTByteSerializer::class)
class NBTByte(val value: Byte) : NBTElement {
    override fun toString(): String = "NBTByte($value)"
}

@Serializable(with = NBTShortSerializer::class)
class NBTShort(val value: Short) : NBTElement {
    override fun toString(): String = "NBTShort($value)"
}

@Serializable(with = NBTIntSerializer::class)
class NBTInt(val value: Int) : NBTElement {
    override fun toString(): String = "NBTInt($value)"
}

@Serializable(with = NBTLongSerializer::class)
class NBTLong(val value: Long) : NBTElement {
    override fun toString(): String = "NBTLong($value)"
}

@Serializable(with = NBTFloatSerializer::class)
class NBTFloat(val value: Float) : NBTElement {
    override fun toString(): String = "NBTFloat($value)"
}

@Serializable(with = NBTDoubleSerializer::class)
class NBTDouble(val value: Double) : NBTElement {
    override fun toString(): String = "NBTDouble($value)"
}

@Serializable(with = NBTByteArraySerializer::class)
class NBTByteArray(val value: ByteArray) : NBTElement {
    override fun toString(): String = value.joinToString(prefix = "NBTByteArray[", separator = ", ", postfix = "]")
}

@Serializable(with = NBTStringSerializer::class)
class NBTString(val value: String) : NBTElement {
    override fun toString(): String = "NBTString($value)"
}

@Serializable(with = NBTListSerializer::class)
class NBTList<T : NBTElement>(val value: List<T>) : NBTElement {
    override fun toString(): String = value.joinToString(prefix = "NBTList[", separator = ", ", postfix = "]")
}

@Serializable(with = NBTCompoundSerializer::class)
class NBTCompound(val value: Map<String, NBTElement>) : NBTElement {

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
class NBTIntArray(val value: IntArray) : NBTElement {
    override fun toString(): String = value.joinToString(prefix = "NBTIntArray[", separator = ", ", postfix = "]")
}

@Serializable(with = NBTLongArraySerializer::class)
class NBTLongArray(val value: LongArray) : NBTElement {
    override fun toString(): String = value.joinToString(prefix = "NBTLongArray[", separator = ", ", postfix = "]")
}