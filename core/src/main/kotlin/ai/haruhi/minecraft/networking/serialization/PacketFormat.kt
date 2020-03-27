package ai.haruhi.minecraft.networking.serialization

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.CompositeDecoder.Companion.READ_DONE
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.AbstractDecoder
import kotlinx.serialization.builtins.AbstractEncoder
import kotlinx.serialization.decode
import kotlinx.serialization.encode
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule

class PacketFormat(
    override val context: SerialModule = EmptyModule
) : BinaryFormat {

    override fun <T> dump(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val tempBuffer = Unpooled.buffer()
        try {
            dumpTo(serializer, value, tempBuffer)
            val result = ByteArray(tempBuffer.readableBytes())
            tempBuffer.readBytes(result)
            return result
        } finally {
            tempBuffer.release()
        }
    }

    override fun <T> load(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        val tempBuffer = Unpooled.wrappedBuffer(bytes)
        try {
            return load(deserializer, tempBuffer)
        } finally {
            tempBuffer.release()
        }
    }

    fun <T> dumpTo(serializer: SerializationStrategy<T>, value: T, byteBufOut: ByteBuf) {
        PacketEncoder(byteBufOut).encode(serializer, value)
    }

    fun <T> load(deserializer: DeserializationStrategy<T>, bytes: ByteBuf): T =
        PacketDecoder(bytes).decode(deserializer)

    class PacketDecoder(private val inByteBuf: ByteBuf) : AbstractDecoder() {
        private var index: Int = -1

        override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
            index++
            return if (index < descriptor.elementsCount) index else READ_DONE
        }

        fun remainingBytes(): Int = inByteBuf.readableBytes()

        override fun decodeSequentially(): Boolean = true

        override fun decodeBoolean(): Boolean = inByteBuf.readBoolean()
        override fun decodeByte(): Byte = inByteBuf.readByte()

        fun decodeBytes(size: Int): ByteArray = ByteArray(size).also {
            inByteBuf.readBytes(it)
        }

        override fun decodeShort(): Short = inByteBuf.readShort()
        override fun decodeInt(): Int = inByteBuf.readInt()
        override fun decodeLong(): Long = inByteBuf.readLong()
        override fun decodeFloat(): Float = inByteBuf.readFloat()
        override fun decodeDouble(): Double = inByteBuf.readDouble()

        override fun decodeString(): String = inByteBuf.decodeString()

        fun decodeVarInt(): Int = inByteBuf.decodeVarInt()
        fun decodeVarLong(): Long = inByteBuf.decodeVarLong()
    }

    class PacketEncoder(private val outByteBuf: ByteBuf) : AbstractEncoder() {

        override fun encodeBoolean(value: Boolean) {
            outByteBuf.writeBoolean(value)
        }

        override fun encodeByte(value: Byte) {
            outByteBuf.writeByte(value.toInt())
        }

        fun encodeBytes(value: ByteArray) {
            outByteBuf.writeBytes(value)
        }

        override fun encodeShort(value: Short) {
            outByteBuf.writeShort(value.toInt())
        }

        override fun encodeInt(value: Int) {
            outByteBuf.writeInt(value)
        }

        override fun encodeLong(value: Long) {
            outByteBuf.writeLong(value)
        }

        override fun encodeFloat(value: Float) {
            outByteBuf.writeFloat(value)
        }

        override fun encodeDouble(value: Double) {
            outByteBuf.writeDouble(value)
        }

        override fun encodeString(value: String) {
            outByteBuf.encodeString(value)
        }

        fun encodeVarInt(value: Int) {
            outByteBuf.encodeVarInt(value)
        }

        fun encodeVarLong(value: Long) {
            outByteBuf.encodeVarLong(value)
        }

        override fun endStructure(descriptor: SerialDescriptor) {
        }
    }
}

fun ByteBuf.decodeString(): String {
    val size = decodeVarInt()
    require(size <= 32767) { "Got string bigger than max size, was $size bytes" }
    return ByteArray(size).also { byteArrayStr ->
        readBytes(byteArrayStr)
    }.toString(Charsets.UTF_8)
}

fun ByteBuf.encodeString(value: String) {
    val byteArrayStr = value.toByteArray(Charsets.UTF_8)
    require(byteArrayStr.size <= 32767) {
        "Max string size is 32767 but got string with ${byteArrayStr.size} bytes"
    }
    encodeVarInt(byteArrayStr.size)
    writeBytes(byteArrayStr)
}

fun ByteBuf.decodeVarInt(): Int {
    var numRead = 0
    var result = 0
    var readByte: Byte
    do {
        readByte = readByte()
        val value = (readByte.toInt() and 0b01111111)
        result = result or (value shl (7 * numRead))
        numRead++
        if (numRead > 5) error("VarInt is too big")
    } while ((readByte.toInt() and 0b10000000) != 0)
    return result
}

fun ByteBuf.encodeVarInt(value: Int) {
    var leftToEncode = value

    while ((leftToEncode and -128) != 0) {
        writeByte(leftToEncode and 127 or 128)
        leftToEncode = leftToEncode ushr 7
    }

    writeByte(leftToEncode)
}

fun ByteBuf.decodeVarLong(): Long {
    var numRead = 0
    var result = 0L
    var readByte: Byte
    do {
        readByte = readByte()
        val value = (readByte.toInt() and 0b01111111).toLong()
        result = result or (value shl (7 * numRead))
        numRead++
        if (numRead > 10) error("VarLong is too big")
    } while ((readByte.toInt() and 0b10000000) != 0)
    return result
}

fun ByteBuf.encodeVarLong(value: Long) {
    var leftToEncode = value

    while ((leftToEncode and -128) != 0L) {
        writeByte((leftToEncode and 127 or 128).toInt())
        leftToEncode = leftToEncode ushr 7
    }

    writeByte(leftToEncode.toInt())
}