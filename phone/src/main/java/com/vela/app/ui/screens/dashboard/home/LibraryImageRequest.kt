package com.vela.app.ui.screens.dashboard.home

/**
 * 媒体库向 Jellyfin/Emby 请求封面时的像素尺寸。
 * 按 xxxhdpi 三列海报格（约 440px 宽）预留 1.2 倍采样，避免把缩略图放大当原图用。
 */
internal object LibraryImageRequest {
    const val POSTER_WIDTH = 540
    const val POSTER_HEIGHT = 810
    const val POSTER_QUALITY = 92

    const val LANDSCAPE_WIDTH = 800
    const val LANDSCAPE_HEIGHT = 450
    const val LANDSCAPE_QUALITY = 90

    const val BANNER_WIDTH = 1200
    const val BANNER_HEIGHT = 222
    const val BANNER_QUALITY = 90

    fun dimensions(
        currentImageType: String,
        requestedImageType: String
    ): Triple<Int, Int, Int> {
        return when (currentImageType) {
            "Thumb", "Backdrop" -> landscape()
            "Banner" -> banner()
            "Primary" -> when (requestedImageType) {
                "Thumb", "Backdrop" -> landscape()
                "Banner" -> banner()
                else -> poster()
            }
            else -> poster()
        }
    }

    private fun poster() = Triple(POSTER_WIDTH, POSTER_HEIGHT, POSTER_QUALITY)

    private fun landscape() = Triple(LANDSCAPE_WIDTH, LANDSCAPE_HEIGHT, LANDSCAPE_QUALITY)

    private fun banner() = Triple(BANNER_WIDTH, BANNER_HEIGHT, BANNER_QUALITY)
}
