package com.vela.app.player.vr

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VrFlattenFilterTest {

    private val layout = VrLayout(
        projection = VrProjection.HalfEquirect,
        stereo = VrStereo.SideBySide,
        inputFov = 180
    )

    @Test
    fun buildsGlslShaderOpts() {
        val opts = VrFlattenFilter.shaderOpts(layout, yaw = 12.5f, pitch = -4f, outputFov = 80f)
        assertTrue(opts.contains("yaw=12.50"))
        assertTrue(opts.contains("pitch=-4.00"))
        assertTrue(opts.contains("d_fov=80.00"))
        assertTrue(opts.contains("id_fov=180"))
        assertTrue(opts.contains("proj_mode=0"))
        assertTrue(opts.contains("stereo_mode=1"))
    }

    @Test
    fun clampsOutputFov() {
        val low = VrFlattenFilter.shaderOpts(layout, outputFov = 10f)
        val high = VrFlattenFilter.shaderOpts(layout, outputFov = 200f)
        assertTrue(low.contains("d_fov=40.00"))
        assertTrue(high.contains("d_fov=120.00"))
    }

    @Test
    fun mapsEquirectAndFisheyeModes() {
        val equirect = VrFlattenFilter.shaderOpts(
            VrLayout(VrProjection.Equirect, VrStereo.TopBottom, 360)
        )
        val fisheye = VrFlattenFilter.shaderOpts(
            VrLayout(VrProjection.Fisheye, VrStereo.Mono, 200)
        )
        assertTrue(equirect.contains("proj_mode=1"))
        assertTrue(equirect.contains("stereo_mode=2"))
        assertTrue(fisheye.contains("proj_mode=2"))
        assertTrue(fisheye.contains("stereo_mode=0"))
        assertFalse(equirect.contains("v360"))
    }
}
