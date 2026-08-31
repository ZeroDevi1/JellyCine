package com.vela.app.playback

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 全屏 / 小窗播放是否仍占用解码器。首页预热 MPV 或自动预告片必须先看这个状态。
 */
object ActivePlayback {
    private val active = MutableStateFlow(false)

    val isActive: StateFlow<Boolean> = active.asStateFlow()

    fun setActive(value: Boolean) {
        active.value = value
    }
}
