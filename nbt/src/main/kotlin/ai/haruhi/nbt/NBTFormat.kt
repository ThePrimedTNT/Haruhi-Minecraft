package ai.haruhi.nbt

import kotlinx.io.ByteArrayInputStream
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.decode
import kotlinx.serialization.encode
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.plus
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class NBTFormat(context: SerialModule = EmptyModule) : BinaryFormat {
    override val context: SerialModule = context + defaultNBTFormatModule

    override fun <T> dump(serializer: SerializationStrategy<T>, value: T): ByteArray =
        ByteArrayOutputStream().use { outputStream ->
            dump(serializer, value, outputStream)
            outputStream.toByteArray()
        }

    fun <T> dump(serializer: SerializationStrategy<T>, value: T, outputStream: OutputStream) {
        NBTTopLevelEncoder(outputStream, context).encode(serializer, value)
    }

    override fun <T> load(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
        ByteArrayInputStream(bytes).use { inputStream ->
            load(deserializer, inputStream)
        }

    fun <T> load(deserializer: DeserializationStrategy<T>, inputStream: InputStream): T {
        return NBTTopLevelDecoder(inputStream, context).decode(deserializer)
    }
}