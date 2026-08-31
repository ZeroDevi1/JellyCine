package com.vela.app.player.mpv

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DolbyVisionMpvTest {

    @Test
    fun dv7ConversionAddsDecodeFilters() {
        val options = DolbyVisionMpv.decodeOptions(
            convertDv7ToDv81 = true,
            deviceSupportsDolbyVision = true
        )
        assertEquals(DolbyVisionMpv.DV7_TO_DV81_VF, options.vf)
        assertEquals(DolbyVisionMpv.DV7_TO_DV81_VD_LAVC, options.vdLavcO)
        assertEquals("dv-native+dv7-to-dv81", options.playbackPath)
    }

    @Test
    fun unsupportedDeviceLogsFallbackPath() {
        val options = DolbyVisionMpv.decodeOptions(
            convertDv7ToDv81 = false,
            deviceSupportsDolbyVision = false
        )
        assertEquals("", options.vf)
        assertEquals("", options.vdLavcO)
        assertEquals("hdr10-or-sdr-fallback", options.playbackPath)
    }

    @Test
    fun brightnessBoostOnlyAppliesToDolbyVision() {
        val dvOn = DolbyVisionMpv.runtimeOptions(
            isDolbyVisionContent = true,
            brightnessEnhancement = true,
            dynamicPeakEnabled = false
        )
        assertEquals("yes", dvOn.hdrComputePeak)
        assertEquals(DolbyVisionMpv.BRIGHTNESS_BOOST, dvOn.toneMappingMaxBoost)

        val hdr10 = DolbyVisionMpv.runtimeOptions(
            isDolbyVisionContent = false,
            brightnessEnhancement = true,
            dynamicPeakEnabled = false
        )
        assertEquals("no", hdr10.hdrComputePeak)
        assertEquals(DolbyVisionMpv.BRIGHTNESS_NEUTRAL, hdr10.toneMappingMaxBoost)
        assertEquals("non-dv", hdr10.playbackPath)
    }

    @Test
    fun brightnessOffLeavesHdr10DynamicPeakAlone() {
        val hdr10 = DolbyVisionMpv.runtimeOptions(
            isDolbyVisionContent = false,
            brightnessEnhancement = false,
            dynamicPeakEnabled = true
        )
        assertEquals("yes", hdr10.hdrComputePeak)
        assertEquals(DolbyVisionMpv.BRIGHTNESS_NEUTRAL, hdr10.toneMappingMaxBoost)
    }

    @Test
    fun detectsDolbyVisionCodecTokens() {
        assertTrue(DolbyVisionMpv.isDolbyVisionTrack("hevc (dvhe.08.06)", null, null))
        assertTrue(DolbyVisionMpv.isDolbyVisionTrack(null, null, "yes"))
        assertFalse(DolbyVisionMpv.isDolbyVisionTrack("hevc", "yuv420p10", null))
    }
}
