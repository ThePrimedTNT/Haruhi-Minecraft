package ai.haruhi.nbt

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.StructureKind
import kotlinx.serialization.UpdateMode
import kotlinx.serialization.builtins.AbstractDecoder
import kotlinx.serialization.modules.SerialModule
import java.io.IOException
import java.io.InputStream

enum class DecoderMode {
    TOP_LEVEL,
    MAP,
    LIST,
    ARRAY,
    CLASS
}

internal class NBTDecoder(
    private val inputStream: InputStream,
    private val decoderMode: DecoderMode,
    override val context: SerialModule
) : AbstractDecoder() {
    override val updateMode: UpdateMode = UpdateMode.UPDATE

    private var currentIndex = -1

    var tagType: Byte = -1
        private set

    private var collectionSize = -1

    init {
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (decoderMode) {
            DecoderMode.TOP_LEVEL -> {
                tagType = decodeByte()
                if (tagType != TAG_END) {
                    // Ignore tag name
                    decodeString()
                }
            }
            DecoderMode.LIST -> {
                tagType = decodeByte()
                collectionSize = decodeInt()
            }
            DecoderMode.ARRAY -> {
                collectionSize = decodeInt()
            }
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeDecoder =
        when (val kind = descriptor.kind) {
            StructureKind.MAP -> NBTDecoder(inputStream, DecoderMode.MAP, context)
            StructureKind.LIST -> {
                val decoderMode = when (tagType) {
                    TAG_BYTE_ARRAY, TAG_INT_ARRAY, TAG_LONG_ARRAY -> DecoderMode.ARRAY
                    TAG_LIST -> DecoderMode.LIST
                    else -> error("Invalid tag type for list: $tagType")
                }
                NBTDecoder(inputStream, decoderMode, context)
            }
            StructureKind.CLASS -> NBTDecoder(inputStream, DecoderMode.CLASS, context)
            else -> error("Unsupported kind: $kind")
        }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        when (decoderMode) {
            DecoderMode.MAP -> {
                // Maps use even indexes as key and odd as value
                currentIndex++
                if (currentIndex.rem(2) == 0) {
                    tagType = decodeByte()
                    if (tagType == TAG_END) {
                        return CompositeDecoder.READ_DONE
                    }
                }
            }
            DecoderMode.CLASS -> {
                tagType = decodeByte()
                if (tagType == TAG_END) {
                    return CompositeDecoder.READ_DONE
                } else {
                    currentIndex = descriptor.getElementIndex(decodeString())
                }
            }
            DecoderMode.LIST, DecoderMode.ARRAY -> currentIndex++
            else -> error("Cannot get element index in mode: $decoderMode")
        }
        return currentIndex
    }

    // We can decode sequentially because in lists the size is saved up front
    override fun decodeSequentially(): Boolean = decoderMode == DecoderMode.LIST || decoderMode == DecoderMode.ARRAY
    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = collectionSize

    override fun decodeByte(): Byte {
        val i = inputStream.read()
        if (i == -1) throw IOException("Unexpected EOF")
        return (i and 0xFF).toByte()
    }

    override fun decodeFloat(): Float = Float.fromBits(decodeInt())
    override fun decodeDouble(): Double = Double.fromBits(decodeLong())
    override fun decodeInt(): Int = combineInt(inputStream.readNBytes(4))
    override fun decodeLong(): Long = combine(inputStream.readNBytes(8), 8)
    override fun decodeShort(): Short = combine(inputStream.readNBytes(2), 2).toShort()
    override fun decodeString(): String = String(inputStream.readNBytes(decodeShort().toInt()))

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = error("not implemented")

    override fun decodeChar(): Char = error("NBT does not support char types.")
    override fun decodeBoolean(): Boolean = error("NBT does not support boolean types.")
    override fun decodeNotNullMark(): Boolean = error("NBT does not support null types.")
    override fun decodeNull(): Nothing? = error("NBT does not support null types.")
    override fun decodeUnit() = error("NBT does not support unit types.")

    private fun combine(byteArray: ByteArray, length: Int): Long {
        if (byteArray.size != length) throw IOException("Unexpected EOF")
        var combinedLong: Long = 0
        for (i in 0 until length) {
            combinedLong = combinedLong or (byteArray[i].toLong() and 0xFF shl ((length - i - 1) * 8L).toInt())
        }
        return combinedLong
    }

    private fun combineInt(byteArray: ByteArray): Int {
        var combinedInt = 0
        for (i in 0..3) {
            combinedInt = combinedInt or (byteArray[i].toInt() and 0xFF shl ((3 - i) * 8L).toInt())
        }
        return combinedInt
    }
}