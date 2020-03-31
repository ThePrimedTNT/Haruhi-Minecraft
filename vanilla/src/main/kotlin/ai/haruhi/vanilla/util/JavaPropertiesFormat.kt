package ai.haruhi.vanilla.util

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.decode
import kotlinx.serialization.encode
import kotlinx.serialization.internal.NamedValueDecoder
import kotlinx.serialization.internal.NamedValueEncoder
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer
import java.util.Properties

class JavaPropertiesFormat(override val context: SerialModule = EmptyModule) : SerialFormat {

    fun <T> load(deserializer: DeserializationStrategy<T>, file: File): T =
        file.reader().use { reader -> load(deserializer, reader) }

    fun <T> load(deserializer: DeserializationStrategy<T>, reader: Reader): T =
        load(deserializer, Properties().apply { load(reader) })

    fun <T> load(deserializer: DeserializationStrategy<T>, stream: InputStream): T =
        load(deserializer, Properties().apply { load(stream) })

    fun <T> load(deserializer: DeserializationStrategy<T>, properties: Properties): T =
        JavaPropertiesDecoder(properties).decode(deserializer)

    fun <T> dumpTo(serializer: SerializationStrategy<T>, value: T, file: File, comments: String = "") {
        file.writer().use { writer -> dumpTo(serializer, value, writer, comments) }
    }

    fun <T> dumpTo(serializer: SerializationStrategy<T>, value: T, writer: Writer, comments: String = "") {
        val properties = Properties()
        dumpTo(serializer, value, properties)
        properties.store(writer, comments)
    }

    fun <T> dumpTo(serializer: SerializationStrategy<T>, value: T, stream: OutputStream, comments: String = "") {
        val properties = Properties()
        dumpTo(serializer, value, properties)
        properties.store(stream, comments)
    }

    fun <T> dumpTo(serializer: SerializationStrategy<T>, value: T, properties: Properties) {
        JavaPropertiesEncoder(properties).encode(serializer, value)
    }

    @OptIn(InternalSerializationApi::class)
    inner class JavaPropertiesDecoder(private val properties: Properties) : NamedValueDecoder() {
        override val context: SerialModule get() = this@JavaPropertiesFormat.context

        private var position = 0

        override fun decodeTaggedValue(tag: String): Any = properties.getValue(tag)

        override fun decodeTaggedBoolean(tag: String): Boolean = decodeTaggedString(tag).toBoolean()
        override fun decodeTaggedByte(tag: String): Byte = decodeTaggedString(tag).toByte()
        override fun decodeTaggedShort(tag: String): Short = decodeTaggedString(tag).toShort()
        override fun decodeTaggedInt(tag: String): Int = decodeTaggedString(tag).toInt()
        override fun decodeTaggedLong(tag: String): Long = decodeTaggedString(tag).toLong()
        override fun decodeTaggedFloat(tag: String): Float = decodeTaggedString(tag).toFloat()
        override fun decodeTaggedDouble(tag: String): Double = decodeTaggedString(tag).toDouble()
        override fun decodeTaggedChar(tag: String): Char = decodeTaggedString(tag)[0]
        override fun decodeTaggedEnum(tag: String, enumDescription: SerialDescriptor): Int =
            decodeTaggedString(tag).toInt()

        override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
            while (position < descriptor.elementsCount) {
                val name = descriptor.getTag(position++)
                if (properties.containsKey(name)) {
                    return position - 1
                }
            }
            return CompositeDecoder.READ_DONE
        }
    }

    @OptIn(InternalSerializationApi::class)
    inner class JavaPropertiesEncoder(private val properties: Properties) : NamedValueEncoder() {
        override val context: SerialModule get() = this@JavaPropertiesFormat.context

        override fun encodeTaggedValue(tag: String, value: Any) {
            properties[tag] = value.toString()
        }
    }
}