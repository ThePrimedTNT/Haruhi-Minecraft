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

    override fun <T> dump(serializer: SerializationStrategy<T>, value: T): ByteArray =
        Unpooled.buffer().also { bytes ->
            dumpTo(serializer, value, bytes)
        }.array()

    override fun <T> load(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
        load(deserializer, Unpooled.wrappedBuffer(bytes))

    fun <T> dumpTo(serializer: SerializationStrategy<T>, value: T, byteBufOut: ByteBuf) {
        PacketEncoder(byteBufOut).encode(serializer, value)
    }

    fun <T> load(deserializer: DeserializationStrategy<T>, bytes: ByteBuf): T =
        PacketDecoder(bytes).decode(deserializer)

    internal open class PacketDecoder(private val bytes: ByteBuf) : AbstractDecoder() {
        private var index: Int = -1

        override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
            index++
            return if (index < descriptor.elementsCount) index else READ_DONE
        }

        override fun decodeBoolean(): Boolean = bytes.readBoolean()
        override fun decodeByte(): Byte = bytes.readByte()
        override fun decodeShort(): Short = bytes.readShort()
        override fun decodeInt(): Int = bytes.readInt()
        override fun decodeLong(): Long = bytes.readLong()
        override fun decodeFloat(): Float = bytes.readFloat()
        override fun decodeDouble(): Double = bytes.readDouble()

        override fun decodeString(): String {
            val size = decodeVarInt()
            require(size <= 32767) { "Got string bigger than max size, was $size bytes" }
            return ByteArray(size).also { byteArrayStr ->
                bytes.readBytes(byteArrayStr)
            }.toString(Charsets.UTF_8)
        }

        fun decodeVarInt(): Int {
            var numRead = 0
            var result = 0
            var readByte: Byte = 0
            do {
                readByte = bytes.readByte()
                val value = (readByte.toInt() and 0b01111111)
                result = result or (value shl (7 * numRead))
                numRead++
                if (numRead > 5) error("VarInt is too big")
            } while ((readByte.toInt() and 0b10000000) != 0)
            return result
        }
    }

    internal open class PacketEncoder(private val bytes: ByteBuf) : AbstractEncoder() {

        override fun encodeBoolean(value: Boolean) {
            bytes.writeBoolean(value)
        }

        override fun encodeByte(value: Byte) {
            bytes.writeByte(value.toInt())
        }

        override fun encodeShort(value: Short) {
            bytes.writeShort(value.toInt())
        }

        override fun encodeInt(value: Int) {
            bytes.writeInt(value)
        }

        override fun encodeLong(value: Long) {
            bytes.writeLong(value)
        }

        override fun encodeFloat(value: Float) {
            bytes.writeFloat(value)
        }

        override fun encodeDouble(value: Double) {
            bytes.writeDouble(value)
        }

        override fun encodeString(value: String) {
            val byteArrayStr = value.toByteArray(Charsets.UTF_8)
            require(byteArrayStr.size <= 32767) {
                "Max string size is 32767 but got string with ${byteArrayStr.size} bytes"
            }
            encodeVarInt(byteArrayStr.size)
            bytes.writeBytes(byteArrayStr)
        }

        fun encodeVarInt(value: Int) {
            var leftToEncode = value

            while ((leftToEncode and -128) != 0) {
                bytes.writeByte(leftToEncode and 127 or 128)
                leftToEncode = leftToEncode ushr 7
            }

            bytes.writeByte(leftToEncode)
        }

        override fun endStructure(descriptor: SerialDescriptor) {
        }
    }
}