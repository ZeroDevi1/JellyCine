package com.vela.app.player.vr

/**
 * Strict VR layout detection from filename / path / tags.
 *
 * A projection token is required. Bare "180" in titles like "180 Days" is ignored.
 * [video3DFormat] only fills stereo packing after a projection is already known.
 */
object VrLayoutParser {
    private val FILE_EXTENSION = Regex("""\.[a-z0-9]{2,4}$""", RegexOption.IGNORE_CASE)

    private val FISHEYE_220 = Regex("""(?:^|[_\-. ])(?:mkx220|fisheye220|f220)(?:$|[_\-. ])""")
    private val FISHEYE_200 = Regex("""(?:^|[_\-. ])(?:mkx200|fisheye200|f200|rf70)(?:$|[_\-. ])""")
    private val FISHEYE_190 = Regex("""(?:^|[_\-. ])(?:fisheye190|f190|fisheye|rf52)(?:$|[_\-. ])""")
    private val HALF_EQUIRECT = Regex(
        """(?:^|[_\-. ])(?:vr180|180x180|hequirect)(?:$|[_\-. ])|[_\-.]180[_\-. ]"""
    )
    private val EQUIRECT = Regex(
        """(?:^|[_\-. ])(?:vr360|360x180|360x360|equirect)(?:$|[_\-. ])|[_\-.]360[_\-. ]"""
    )

    private val STEREO_SBS = Regex("""(?:^|[_\-. ])(?:sbs|3dh|lr)(?:$|[_\-. ])""")
    private val STEREO_TB = Regex("""(?:^|[_\-. ])(?:tb|ou|3dv)(?:$|[_\-. ])""")
    private val STEREO_MONO = Regex("""(?:^|[_\-. ])(?:mono|2d)(?:$|[_\-. ])""")

    fun parse(
        mediaSourcePath: String? = null,
        itemPath: String? = null,
        itemName: String? = null,
        mediaSourceName: String? = null,
        tags: List<String>? = null,
        video3DFormat: String? = null
    ): VrLayout? {
        var projection: VrProjection? = null
        var inputFov: Int? = null
        var stereo: VrStereo? = null

        val texts = listOf(
            fileName(mediaSourcePath),
            fileName(itemPath),
            itemName,
            mediaSourceName
        )
        for (text in texts) {
            val scan = scanText(text)
            if (projection == null && scan.projection != null) {
                projection = scan.projection
                inputFov = scan.inputFov
            }
            if (stereo == null && scan.stereo != null) {
                stereo = scan.stereo
            }
            if (projection != null && stereo != null) break
        }

        if (projection == null || stereo == null) {
            for (tag in tags.orEmpty()) {
                val scan = scanTag(tag) ?: continue
                if (projection == null && scan.projection != null) {
                    projection = scan.projection
                    inputFov = scan.inputFov
                }
                if (stereo == null && scan.stereo != null) {
                    stereo = scan.stereo
                }
                if (projection != null && stereo != null) break
            }
        }

        val resolvedProjection = projection ?: return null
        val resolvedStereo = stereo
            ?: stereoFromVideo3DFormat(video3DFormat)
            ?: VrStereo.SideBySide
        return VrLayout(
            projection = resolvedProjection,
            stereo = resolvedStereo,
            inputFov = inputFov ?: defaultFov(resolvedProjection)
        )
    }

    fun layoutForId(id: String): VrLayout? {
        val parts = id.split(':')
        if (parts.size == 3) {
            val projection = runCatching { VrProjection.valueOf(parts[0]) }.getOrNull()
            val stereo = runCatching { VrStereo.valueOf(parts[1]) }.getOrNull()
            val fov = parts[2].toIntOrNull()
            if (projection != null && stereo != null && fov != null) {
                return VrLayout(projection, stereo, fov)
            }
        }
        return manualOptions().firstOrNull { it.id == id }
    }

    fun manualOptions(): List<VrLayout> {
        return listOf(
            VrLayout(VrProjection.HalfEquirect, VrStereo.SideBySide, 180),
            VrLayout(VrProjection.HalfEquirect, VrStereo.TopBottom, 180),
            VrLayout(VrProjection.HalfEquirect, VrStereo.Mono, 180),
            VrLayout(VrProjection.Equirect, VrStereo.SideBySide, 360),
            VrLayout(VrProjection.Equirect, VrStereo.TopBottom, 360),
            VrLayout(VrProjection.Equirect, VrStereo.Mono, 360),
            VrLayout(VrProjection.Fisheye, VrStereo.SideBySide, 190),
            VrLayout(VrProjection.Fisheye, VrStereo.SideBySide, 200),
            VrLayout(VrProjection.Fisheye, VrStereo.SideBySide, 220)
        )
    }

    private fun scanText(raw: String?): Scan {
        val normalized = normalize(raw) ?: return Scan()
        return Scan(
            projection = scanProjection(normalized)?.first,
            inputFov = scanProjection(normalized)?.second,
            stereo = scanStereo(normalized)
        )
    }

    private fun scanTag(raw: String): Scan? {
        val tag = raw.trim().lowercase()
        if (tag.isBlank()) return null
        val padded = " $tag "
        when (tag) {
            "vr", "vr180", "180x180", "hequirect" ->
                return Scan(VrProjection.HalfEquirect, 180, scanStereo(padded))
            "vr360", "360", "equirect" ->
                return Scan(VrProjection.Equirect, 360, scanStereo(padded))
            "mkx220", "fisheye220", "f220" ->
                return Scan(VrProjection.Fisheye, 220, scanStereo(padded))
            "mkx200", "fisheye200", "f200", "rf70" ->
                return Scan(VrProjection.Fisheye, 200, scanStereo(padded))
            "fisheye190", "f190", "fisheye", "rf52" ->
                return Scan(VrProjection.Fisheye, 190, scanStereo(padded))
        }
        return scanText(tag).takeIf { it.projection != null || it.stereo != null }
    }

    private fun scanProjection(text: String): Pair<VrProjection, Int>? {
        return when {
            FISHEYE_220.containsMatchIn(text) -> VrProjection.Fisheye to 220
            FISHEYE_200.containsMatchIn(text) -> VrProjection.Fisheye to 200
            FISHEYE_190.containsMatchIn(text) -> VrProjection.Fisheye to 190
            HALF_EQUIRECT.containsMatchIn(text) -> VrProjection.HalfEquirect to 180
            EQUIRECT.containsMatchIn(text) -> VrProjection.Equirect to 360
            else -> null
        }
    }

    private fun scanStereo(text: String): VrStereo? {
        return when {
            STEREO_MONO.containsMatchIn(text) -> VrStereo.Mono
            STEREO_TB.containsMatchIn(text) -> VrStereo.TopBottom
            STEREO_SBS.containsMatchIn(text) -> VrStereo.SideBySide
            else -> null
        }
    }

    private fun stereoFromVideo3DFormat(format: String?): VrStereo? {
        return when (format?.trim()?.lowercase()) {
            "halfsidebyside", "fullsidebyside" -> VrStereo.SideBySide
            "halftopandbottom", "fulltopandbottom" -> VrStereo.TopBottom
            else -> null
        }
    }

    private fun defaultFov(projection: VrProjection): Int {
        return when (projection) {
            VrProjection.HalfEquirect -> 180
            VrProjection.Equirect -> 360
            VrProjection.Fisheye -> 190
        }
    }

    private fun fileName(path: String?): String? {
        if (path.isNullOrBlank()) return null
        val withoutQuery = path.substringBefore('?')
        val name = withoutQuery.substringAfterLast('/').substringAfterLast('\\')
        return name.takeIf { it.isNotBlank() }
    }

    private fun normalize(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val name = fileName(raw) ?: raw
        return " ${FILE_EXTENSION.replace(name.lowercase(), " ")} "
    }

    private data class Scan(
        val projection: VrProjection? = null,
        val inputFov: Int? = null,
        val stereo: VrStereo? = null
    )
}
