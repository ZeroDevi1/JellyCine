package com.vela.app.player.vr

/**
 * Parsed VR layout used to flatten a 180/360/fisheye stream into a rectilinear 2D window.
 */
data class VrLayout(
    val projection: VrProjection,
    val stereo: VrStereo,
    val inputFov: Int
) {
    val id: String
        get() = "${projection.name}:${stereo.name}:$inputFov"

    val yawLimit: Float
        get() = if (projection == VrProjection.Equirect) 180f else 90f
}

enum class VrProjection {
    HalfEquirect,
    Equirect,
    Fisheye
}

enum class VrStereo {
    SideBySide,
    TopBottom,
    Mono
}
