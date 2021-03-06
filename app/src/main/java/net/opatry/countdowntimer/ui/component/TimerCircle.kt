/*
 * Copyright (c) 2021 Olivier Patry
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.opatry.countdowntimer.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.material.icons.twotone.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.opatry.countdowntimer.R
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@Composable
@ExperimentalTime
fun TimerCircle(
    hoursProgress: Float,
    minutesProgress: Float,
    secondsProgress: Float,
    tickInterval: Duration,
    onFABClicked: () -> Unit
) {
    Card(
        Modifier
            .padding(16.dp)
            .fillMaxWidth(.8f)
            .aspectRatio(1f),
        shape = CircleShape,
        elevation = 16.dp
    ) {
        if (secondsProgress > 0 || minutesProgress > 0 || hoursProgress > 0) {
            CountDownTimerProgressIndicator(
                progress = secondsProgress,
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                color = DurationUnit.SECONDS.color,
                strokeWidth = 8.dp,
                animationDuration = tickInterval
            )
        }
        if (minutesProgress > 0 || hoursProgress > 0) {
            CountDownTimerProgressIndicator(
                progress = minutesProgress,
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                color = DurationUnit.MINUTES.color,
                strokeWidth = 8.dp,
                animationDuration = tickInterval
            )
        }
        if (hoursProgress > 0) {
            CountDownTimerProgressIndicator(
                progress = hoursProgress,
                Modifier
                    .fillMaxSize()
                    .padding(56.dp),
                color = DurationUnit.HOURS.color,
                strokeWidth = 8.dp,
                animationDuration = tickInterval
            )
        }
        FloatingActionButton(
            onClick = onFABClicked,
            Modifier.wrapContentSize(Alignment.Center)
        ) {
            val (icon, labelRes) = when {
                secondsProgress > 0 || secondsProgress > 0 || secondsProgress > 0 -> Icons.TwoTone.Stop to R.string.timer_stop
                else -> Icons.TwoTone.PlayArrow to R.string.timer_start
            }
            Icon(
                icon,
                stringResource(labelRes)
            )
        }
    }
}

@Composable
@ExperimentalTime
fun CountDownTimerProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
    animationDuration: Duration,
    backgroundColor: Color = MaterialTheme.colors.onSurface.copy(alpha = .05f)
) {
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = animationDuration.toLongMilliseconds().toInt(),
            easing = LinearEasing
        )
    ).value
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
    }
    Canvas(modifier) {
        val innerRadius = (size.minDimension - stroke.width) / 2
        drawArc(
            color = backgroundColor,
            startAngle = 0f,
            sweepAngle = 360f,
            topLeft = Offset(
                (size / 2.0f).width - innerRadius,
                (size / 2.0f).height - innerRadius
            ),
            size = Size(innerRadius * 2, innerRadius * 2),
            useCenter = false,
            style = stroke
        )
    }
    // // TODO how to make stroke cap rounded?
    // CircularProgressIndicator(
    //     animatedProgress,
    //     modifier,
    //     color = color,
    //     strokeWidth = strokeWidth
    // )

    Canvas(modifier) {
        val innerRadius = (size.minDimension - stroke.width) / 2
        drawArc(
            color = color,
            // Start at 12 O'clock
            startAngle = 270f,
            sweepAngle = animatedProgress * 360f,
            topLeft = Offset(
                (size / 2.0f).width - innerRadius,
                (size / 2.0f).height - innerRadius
            ),
            size = Size(innerRadius * 2, innerRadius * 2),
            useCenter = false,
            style = stroke
        )
    }
}
