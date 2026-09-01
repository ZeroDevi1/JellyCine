package com.vela.app.player.vr

/**
 * GPU flatten options for mpv GLSL / libplacebo hooks.
 */
object VrFlattenFilter {
    const val SHADER_ASSET = "shaders/vr_flatten.glsl"
    const val SHADER_FILE_NAME = "vr_flatten.glsl"
    const val DEFAULT_OUTPUT_FOV = 90f
    const val MIN_OUTPUT_FOV = 40f
    const val MAX_OUTPUT_FOV = 120f

    fun shaderOpts(
        layout: VrLayout,
        yaw: Float = 0f,
        pitch: Float = 0f,
        outputFov: Float = DEFAULT_OUTPUT_FOV
    ): String {
        val fov = outputFov.coerceIn(MIN_OUTPUT_FOV, MAX_OUTPUT_FOV)
        return "yaw=${format(yaw)},pitch=${format(pitch)},d_fov=${format(fov)}," +
            "id_fov=${layout.inputFov},proj_mode=${layout.projMode},stereo_mode=${layout.stereoMode}"
    }

    private fun format(value: Float): String {
        return "%.2f".format(java.util.Locale.US, value)
    }
}

internal val VrLayout.projMode: Int
    get() = when (projection) {
        VrProjection.HalfEquirect -> 0
        VrProjection.Equirect -> 1
        VrProjection.Fisheye -> 2
    }

internal val VrLayout.stereoMode: Int
    get() = when (stereo) {
        VrStereo.Mono -> 0
        VrStereo.SideBySide -> 1
        VrStereo.TopBottom -> 2
    }
