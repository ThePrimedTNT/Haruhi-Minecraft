package ai.haruhi.nbt

import kotlinx.io.ByteArrayInputStream
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.decode
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule
import java.io.InputStream

class NBTFormat(override val context: SerialModule = EmptyModule) : BinaryFormat {

    override fun <T> dump(serializer: SerializationStrategy<T>, value: T): ByteArray {
        TODO("not implemented")
    }

    override fun <T> load(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
        ByteArrayInputStream(bytes).use { inputStream ->
            load(deserializer, inputStream)
        }

    fun <T> load(deserializer: DeserializationStrategy<T>, inputStream: InputStream): T {
        return NBTDecoder(inputStream, DecoderMode.TOP_LEVEL, context).decode(deserializer)
    }
}