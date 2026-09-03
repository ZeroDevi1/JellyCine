package com.vela.app.player.mpv

import com.vela.data.model.MediaStream
import com.vela.player.preferences.PlayerPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HevcHwdecColorTest {

    @Test
    fun unspecified4kHevcUsesCopyAndBt709Filter() {
        val streams = listOf(unspecifiedHevc(width = 3840, height = 2160, codecTag = "hev1"))
        assertEquals(
            PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC_COPY,
            HevcHwdecColor.hardwareDecoding(
                PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC,
                streams
            )
        )
        assertEquals(HevcHwdecColor.BT709_FORMAT_VF, HevcHwdecColor.formatVf(streams))
        assertTrue(HevcHwdecColor.needsCopyPath(streams))
    }

    @Test
    fun taggedBt709HevcKeepsZeroCopy() {
        val streams = listOf(
            MediaStream(
                type = "Video",
                codec = "hevc",
                codecTag = "hvc1",
                width = 3840,
                height = 2160,
                colorPrimaries = "bt709",
                colorSpace = "bt709",
                colorTransfer = "bt709",
                videoRange = "SDR",
                videoRangeType = "SDR",
                bitDepth = 8
            )
        )
        assertEquals(
            PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC,
            HevcHwdecColor.hardwareDecoding(
                PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC,
                streams
            )
        )
        assertEquals("", HevcHwdecColor.formatVf(streams))
        assertFalse(HevcHwdecColor.needsCopyPath(streams))
    }

    @Test
    fun hdr10HevcKeepsZeroCopy() {
        val streams = listOf(
            MediaStream(
                type = "Video",
                codec = "hevc",
                width = 3840,
                height = 2160,
                colorPrimaries = "bt2020",
                colorSpace = "bt2020nc",
                colorTransfer = "smpte2084",
                videoRange = "HDR",
                videoRangeType = "HDR10",
                bitDepth = 10
            )
        )
        assertEquals(
            PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC,
            HevcHwdecColor.hardwareDecoding(
                PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC,
                streams
            )
        )
        assertEquals("", HevcHwdecColor.formatVf(streams))
    }

    @Test
    fun bt2020SdrUsesCopyWithoutForcing709() {
        val streams = listOf(
            MediaStream(
                type = "Video",
                codec = "hevc",
                width = 3840,
                height = 2160,
                colorPrimaries = "bt2020",
                colorSpace = "bt2020nc",
                colorTransfer = "bt709",
                videoRange = "SDR",
                videoRangeType = "SDR",
                bitDepth = 8
            )
        )
        assertEquals(
            PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC_COPY,
            HevcHwdecColor.hardwareDecoding(
                PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC,
                streams
            )
        )
        assertEquals("", HevcHwdecColor.formatVf(streams))
        assertTrue(HevcHwdecColor.needsCopyPath(streams))
    }

    @Test
    fun softwarePreferenceIsUnchanged() {
        val streams = listOf(unspecifiedHevc(width = 3840, height = 2160))
        assertEquals(
            PlayerPreferences.MPV_HARDWARE_DECODING_NONE,
            HevcHwdecColor.hardwareDecoding(
                PlayerPreferences.MPV_HARDWARE_DECODING_NONE,
                streams
            )
        )
    }

    @Test
    fun alreadyCopyPreferenceIsUnchanged() {
        val streams = listOf(unspecifiedHevc(width = 3840, height = 2160))
        assertEquals(
            PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC_COPY,
            HevcHwdecColor.hardwareDecoding(
                PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC_COPY,
                streams
            )
        )
        assertEquals(HevcHwdecColor.BT709_FORMAT_VF, HevcHwdecColor.formatVf(streams))
    }

    @Test
    fun sdUnspecifiedHevcDoesNotForceCopy() {
        val streams = listOf(unspecifiedHevc(width = 720, height = 480))
        assertFalse(HevcHwdecColor.needsCopyPath(streams))
        assertEquals("", HevcHwdecColor.formatVf(streams))
    }

    @Test
    fun h264UnspecifiedDoesNotForceCopy() {
        val streams = listOf(
            MediaStream(
                type = "Video",
                codec = "h264",
                width = 1920,
                height = 1080
            )
        )
        assertFalse(HevcHwdecColor.needsCopyPath(streams))
        assertEquals("", HevcHwdecColor.formatVf(streams))
    }

    @Test
    fun mpvHardwareDecodingForUnspecified4kHevcUsesCopy() {
        val streams = listOf(unspecifiedHevc(width = 3840, height = 2160, codecTag = "hev1"))
        assertEquals(
            PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC_COPY,
            MPVPlayer.hardwareDecodingFor(
                mediaSource = null,
                userPreference = PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC,
                mediaStreams = streams
            )
        )
        assertFalse(MPVPlayer.isHdr(streams))
    }

    @Test
    fun bt2020SdrIsNotTreatedAsHdr() {
        val streams = listOf(
            MediaStream(
                type = "Video",
                codec = "hevc",
                colorSpace = "bt2020nc",
                colorTransfer = "bt709",
                videoRange = "SDR"
            )
        )
        assertFalse(MPVPlayer.isHdr(streams))
    }

    @Test
    fun composesDolbyFilterAfterColorFilter() {
        val streams = listOf(unspecifiedHevc(width = 3840, height = 2160))
        assertEquals(
            "${HevcHwdecColor.BT709_FORMAT_VF},${DolbyVisionMpv.DV7_TO_DV81_VF}",
            HevcHwdecColor.composedVf(DolbyVisionMpv.DV7_TO_DV81_VF, streams)
        )
    }

    private fun unspecifiedHevc(
        width: Int,
        height: Int,
        codecTag: String? = "hev1"
    ): MediaStream {
        return MediaStream(
            type = "Video",
            codec = "hevc",
            codecTag = codecTag,
            width = width,
            height = height,
            profile = "Main",
            bitDepth = 8,
            videoRange = "SDR",
            videoRangeType = "SDR"
        )
    }
}
