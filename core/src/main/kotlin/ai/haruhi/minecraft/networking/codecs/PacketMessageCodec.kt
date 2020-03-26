package ai.haruhi.minecraft.networking.codecs

import ai.haruhi.minecraft.networking.serialization.decodeVarInt
import ai.haruhi.minecraft.networking.serialization.encodeVarInt
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import io.netty.handler.codec.CorruptedFrameException

class PacketMessageCodec : ByteToMessageCodec<ByteBuf>() {

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        val packetLength = msg.readableBytes()
        out.encodeVarInt(packetLength)
        out.writeBytes(msg, msg.readerIndex(), packetLength)
    }

    private val tempPacketLengthData = ByteArray(3)

    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        `in`.markReaderIndex()

        tempPacketLengthData.fill(0)

        for (lengthIndex in tempPacketLengthData.indices) {
            if (!`in`.isReadable) {
                // cancel operation if we reached end of buffer
                `in`.resetReaderIndex()
                return
            }
            val nextByte = `in`.readByte()
            tempPacketLengthData[lengthIndex] = nextByte

            if (nextByte >= 0) { // VarInt byte is positive when we reached the last byte
                val tempPacketLengthBuffer = Unpooled.wrappedBuffer(tempPacketLengthData)
                try {
                    val packetLength = tempPacketLengthBuffer.decodeVarInt()
                    if (`in`.readableBytes() < packetLength) {
                        // the packet isn't fully received yet, abort
                        `in`.resetReaderIndex()
                        return
                    }

                    // packet is now ready to be read
                    out += `in`.readBytes(packetLength)
                } finally {
                    tempPacketLengthBuffer.release()
                }
                return
            }
        }
        // VarInt is bigger than expected
        throw CorruptedFrameException("length wider than 21-bit")
    }

    companion object {
        const val PIPELINE_NAME = "packet-codec"
    }
}