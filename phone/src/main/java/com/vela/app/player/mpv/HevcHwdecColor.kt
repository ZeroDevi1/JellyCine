package com.vela.app.player.mpv

import com.vela.data.model.MediaStream
import com.vela.player.preferences.PlayerPreferences

/**
 * 未标注色域的 HEVC（尤其 4K / hev1）在 MediaCodec 零拷贝上会按错误矩阵上色。
 * 拷回 CPU 后由 mpv 按 BT.709 转换；BT.2020 SDR 只切 copy，不强制 709。
 */
internal object HevcHwdecColor {
    const val BT709_FORMAT_VF =
        "format:colormatrix=bt.709:primaries=bt.709:gamma=bt.709:colorlevels=limited"

    fun hardwareDecoding(
        userPreference: String,
        mediaStreams: List<MediaStream>?
    ): String {
        if (userPreference != PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC) {
            return userPreference
        }
        return if (needsCopyPath(mediaStreams)) {
            PlayerPreferences.MPV_HARDWARE_DECODING_MEDIACODEC_COPY
        } else {
            userPreference
        }
    }

    fun formatVf(mediaStreams: List<MediaStream>?): String {
        return if (needsBt709Override(mediaStreams)) BT709_FORMAT_VF else ""
    }

    fun composedVf(dolbyVf: String, mediaStreams: List<MediaStream>?): String {
        return listOf(formatVf(mediaStreams), dolbyVf)
            .filter { it.isNotBlank() }
            .joinToString(",")
    }

    fun needsCopyPath(mediaStreams: List<MediaStream>?): Boolean {
        val video = videoStream(mediaStreams) ?: return false
        if (!isHevc(video) || isHdr(video) || !isHdOrUnknown(video)) return false
        return hasUnspecifiedColor(video) || isBt2020Sdr(video)
    }

    private fun needsBt709Override(mediaStreams: List<MediaStream>?): Boolean {
        val video = videoStream(mediaStreams) ?: return false
        return isHevc(video) &&
            !isHdr(video) &&
            isHdOrUnknown(video) &&
            hasUnspecifiedColor(video)
    }

    private fun videoStream(mediaStreams: List<MediaStream>?): MediaStream? {
        return mediaStreams.orEmpty().firstOrNull { stream ->
            stream.type.equals("Video", ignoreCase = true)
        }
    }

    private fun isHevc(video: MediaStream): Boolean {
        val codec = video.codec?.lowercase().orEmpty()
        val tag = video.codecTag?.lowercase().orEmpty()
        return codec.contains("hevc") ||
            codec.contains("h265") ||
            codec.contains("hev1") ||
            codec.contains("hvc1") ||
            tag.contains("hev1") ||
            tag.contains("hvc1")
    }

    private fun isHdOrUnknown(video: MediaStream): Boolean {
        val width = video.width ?: 0
        val height = video.height ?: 0
        if (width <= 0 && height <= 0) return true
        return width >= 1280 || height >= 720
    }

    private fun hasUnspecifiedColor(video: MediaStream): Boolean {
        return isBlankOrUnspecified(video.colorPrimaries) &&
            isBlankOrUnspecified(video.colorSpace) &&
            isBlankOrUnspecified(video.colorTransfer)
    }

    private fun isBt2020Sdr(video: MediaStream): Boolean {
        if (isHdr(video)) return false
        return containsBt2020(video.colorSpace) || containsBt2020(video.colorPrimaries)
    }

    private fun isHdr(video: MediaStream): Boolean {
        val transfer = video.colorTransfer?.lowercase().orEmpty()
        val range = video.videoRange?.lowercase().orEmpty()
        val rangeType = video.videoRangeType?.lowercase().orEmpty()
        if (
            transfer.contains("2084") ||
            transfer.contains("smpte2084") ||
            transfer.contains("pq") ||
            transfer.contains("hlg") ||
            transfer.contains("arib-std-b67")
        ) {
            return true
        }
        if (rangeType.contains("dovi") || range.contains("dovi")) return true
        if (rangeType.contains("hdr") || range == "hdr") return true
        return video.dvProfile != null || video.rpuPresentFlag == 1
    }

    private fun containsBt2020(value: String?): Boolean {
        val token = value?.lowercase().orEmpty()
        return token.contains("bt2020") || token.contains("bt.2020")
    }

    private fun isBlankOrUnspecified(value: String?): Boolean {
        val token = value?.trim()?.lowercase().orEmpty()
        return token.isEmpty() ||
            token == "unspecified" ||
            token == "unknown" ||
            token == "na"
    }
}
