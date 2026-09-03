package com.vela.app.player.mpv

import com.vela.data.model.MediaStream
import com.vela.player.preferences.PlayerPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HevcHwdecColorTest {

    @Test
    fun unspecified4kHevcPrefersSoftwareAndBt709Filter() {
        val streams = listOf(unspecifiedHevc(width = 3840, height = 2160, codecTag = "hev1"))
        assertEquals(
            PlayerPreferences.MPV_HARDWARE_DECODING_NONE,
            HevcHwdecColor.hardwareDecoding(
                PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC,
                streams
            )
        )
        assertEquals(HevcHwdecColor.BT709_FORMAT_VF, HevcHwdecColor.formatVf(streams))
        assertTrue(HevcHwdecColor.needsSoftwareColorPath(streams))
        assertTrue(HevcHwdecColor.needsBt709InputOverride(streams))
    }

    @Test
    fun tagged4kBt709HevcPrefersSoftware() {
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
            PlayerPreferences.MPV_HARDWARE_DECODING_NONE,
            HevcHwdecColor.hardwareDecoding(
                PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC,
                streams
            )
        )
        assertTrue(HevcHwdecColor.needsSoftwareColorPath(streams))
        assertEquals(HevcHwdecColor.BT709_FORMAT_VF, HevcHwdecColor.formatVf(streams))
    }

    @Test
    fun tagged1080pBt709HevcKeepsHardware() {
        val streams = listOf(
            MediaStream(
                type = "Video",
                codec = "hevc",
                codecTag = "hvc1",
                width = 1920,
                height = 1080,
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
        assertFalse(HevcHwdecColor.needsSoftwareColorPath(streams))
        assertEquals("", HevcHwdecColor.formatVf(streams))
    }

    @Test
    fun hdr10HevcKeepsHardware() {
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
    fun bt2020SdrPrefersSoftwareWithoutTreatingAsHdr() {
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
            PlayerPreferences.MPV_HARDWARE_DECODING_NONE,
            HevcHwdecColor.hardwareDecoding(
                PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC,
                streams
            )
        )
        assertTrue(HevcHwdecColor.needsSoftwareColorPath(streams))
        assertFalse(HevcHwdecColor.needsBt709InputOverride(streams))
        assertEquals("", HevcHwdecColor.formatVf(streams))
        assertFalse(MPVPlayer.isHdr(streams))
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
    fun copyPreferenceIsOverriddenToSoftware() {
        val streams = listOf(unspecifiedHevc(width = 3840, height = 2160))
        assertEquals(
            PlayerPreferences.MPV_HARDWARE_DECODING_NONE,
            HevcHwdecColor.hardwareDecoding(
                PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC_COPY,
                streams
            )
        )
    }

    @Test
    fun sdUnspecifiedHevcDoesNotForceSoftware() {
        val streams = listOf(unspecifiedHevc(width = 720, height = 480, codecTag = "hvc1"))
        assertFalse(HevcHwdecColor.needsSoftwareColorPath(streams))
        assertEquals("", HevcHwdecColor.formatVf(streams))
    }

    @Test
    fun h264UnspecifiedDoesNotForceSoftware() {
        val streams = listOf(
            MediaStream(
                type = "Video",
                codec = "h264",
                width = 1920,
                height = 1080
            )
        )
        assertFalse(HevcHwdecColor.needsSoftwareColorPath(streams))
        assertEquals("", HevcHwdecColor.formatVf(streams))
    }

    @Test
    fun mpvHardwareDecodingForUnspecified4kHevcPrefersSoftware() {
        val streams = listOf(unspecifiedHevc(width = 3840, height = 2160, codecTag = "hev1"))
        assertEquals(
            PlayerPreferences.MPV_HARDWARE_DECODING_NONE,
            MPVPlayer.hardwareDecodingFor(
                mediaSource = null,
                userPreference = PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC,
                mediaStreams = streams
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
