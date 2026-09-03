package com.vela.detail

import com.vela.data.model.MediaStream
import org.junit.Assert.assertEquals
import org.junit.Test

class CodecCapabilityManagerHdrTest {

    @Test
    fun bt2020SdrIsNotHdr() {
        val stream = MediaStream(
            type = "Video",
            codec = "hevc",
            colorPrimaries = "bt2020",
            colorSpace = "bt2020nc",
            colorTransfer = "bt709",
            videoRange = "SDR",
            videoRangeType = "SDR",
            bitDepth = 8,
            width = 3840,
            height = 2160
        )
        assertEquals("", CodecCapabilityManager.detectHDRFormat(stream))
        assertEquals("", CodecCapabilityManager.detectBestSourceHDRFormat(listOf(stream)))
    }

    @Test
    fun pqTransferIsHdr10() {
        val stream = MediaStream(
            type = "Video",
            codec = "hevc",
            colorTransfer = "smpte2084",
            colorSpace = "bt2020nc",
            videoRange = "HDR",
            videoRangeType = "HDR10"
        )
        assertEquals("HDR10", CodecCapabilityManager.detectHDRFormat(stream))
    }

    @Test
    fun unspecifiedSdrHevcIsNotHdr() {
        val stream = MediaStream(
            type = "Video",
            codec = "hevc",
            title = "4K HEVC SDR",
            videoRange = "SDR",
            videoRangeType = "SDR",
            bitDepth = 8
        )
        assertEquals("", CodecCapabilityManager.detectHDRFormat(stream))
    }
}
