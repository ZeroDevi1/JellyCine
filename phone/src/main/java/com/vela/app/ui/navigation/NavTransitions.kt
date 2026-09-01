package com.vela.app.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset

/**
 * 全应用页面转场。共享轴水平滑动必须足够大，弹簧缩放 1.5% 在真机上等于没动画。
 */
internal object NavTransitions {
    const val SLIDE_FRACTION = 0.28f
    const val POP_SLIDE_FRACTION = 1f
    const val POP_SCALE = 0.92f
    const val TAB_ENTER_SCALE = 0.96f
    const val DURATION_MS = 320

    fun <T> spec(): FiniteAnimationSpec<T> = tween(
        durationMillis = DURATION_MS,
        easing = FastOutSlowInEasing
    )

    fun enter(): EnterTransition {
        return fadeIn(animationSpec = spec()) +
            slideInHorizontally(animationSpec = spec()) { width ->
                (width * SLIDE_FRACTION).toInt().coerceAtLeast(1)
            }
    }

    fun exit(): ExitTransition {
        return fadeOut(animationSpec = spec()) +
            slideOutHorizontally(animationSpec = spec()) { width ->
                -(width * SLIDE_FRACTION).toInt().coerceAtLeast(1)
            }
    }

    fun popEnter(): EnterTransition {
        return fadeIn(animationSpec = spec()) +
            slideInHorizontally(animationSpec = spec()) { width ->
                -(width * SLIDE_FRACTION).toInt().coerceAtLeast(1)
            }
    }

    fun popExit(): ExitTransition {
        return fadeOut(animationSpec = spec()) +
            scaleOut(
                animationSpec = spec(),
                targetScale = POP_SCALE,
                transformOrigin = TransformOrigin.Center
            ) +
            slideOutHorizontally(animationSpec = spec<IntOffset>()) { width ->
                (width * POP_SLIDE_FRACTION).toInt().coerceAtLeast(1)
            }
    }

    fun tabEnter(): EnterTransition {
        return fadeIn(animationSpec = spec()) +
            scaleIn(animationSpec = spec(), initialScale = TAB_ENTER_SCALE)
    }

    fun tabExit(): ExitTransition {
        return fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing))
    }
}
