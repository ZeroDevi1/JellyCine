package com.vela.app.ui.screens.player

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vela.player.core.MAX_VIDEO_WIDTH_FRACTION
import com.vela.player.core.MIN_VIDEO_WIDTH_FRACTION
import com.vela.shared.R
import kotlin.math.roundToInt

@Composable
fun VideoWidthAdjustOverlay(
    widthFraction: Float,
    onWidthFractionChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(
                    WindowInsets.displayCutout.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                    )
                )
                .padding(horizontal = 28.dp, vertical = 18.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xE6161618))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.player_adjust_video_size),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(
                    R.string.player_video_width_value,
                    (widthFraction * 100f).roundToInt()
                ),
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
            Slider(
                value = widthFraction,
                onValueChange = onWidthFractionChange,
                valueRange = MIN_VIDEO_WIDTH_FRACTION..MAX_VIDEO_WIDTH_FRACTION,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF7DD3FC),
                    activeTrackColor = Color(0xFF7DD3FC),
                    inactiveTrackColor = Color.White.copy(alpha = 0.18f)
                )
            )
        }
    }
}
