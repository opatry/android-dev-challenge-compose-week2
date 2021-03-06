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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.opatry.countdowntimer.R
import net.opatry.countdowntimer.Timer
import net.opatry.countdowntimer.ui.theme.FluorescentBlue
import net.opatry.countdowntimer.ui.theme.RazzleDazzleRose
import net.opatry.countdowntimer.ui.theme.RussianViolet
import net.opatry.countdowntimer.ui.theme.typography
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@ExperimentalTime
val DurationUnit.color: Color
    get() = when (this) {
        DurationUnit.HOURS -> RussianViolet
        DurationUnit.MINUTES -> RazzleDazzleRose
        DurationUnit.SECONDS -> FluorescentBlue
        else -> Color.Cyan
    }

@Composable
@ExperimentalTime
@ExperimentalFoundationApi
fun TimerList(activeTimer: Timer?, timers: List<Timer>, onTimerClicked: (Timer) -> Unit) {
    val otherTimers = timers.filterNot { it == activeTimer }

    LazyColumn {
        // FIXME when header goes sticky, it's not opaque and list is displayed below
        stickyHeader {
            if (activeTimer != null) {
                ActiveTimer(activeTimer.name ?: stringResource(R.string.timer_unnamed))
            } else {
                // TODO distinguish hint from selected timer but share common "title" style
                Text(
                    stringResource(R.string.timer_select_a_timer),
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    style = typography.h5,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
        items(otherTimers) { timer ->
            TimerListItem(timer, onTimerClicked)
        }
    }
}

@Composable
@ExperimentalTime
fun ActiveTimer(timerName: String) {
    Text(
        timerName,
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        style = typography.h5,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.onBackground
    )
}

@Composable
@ExperimentalTime
fun TimerListItem(timer: Timer, onTimerClicked: (Timer) -> Unit) {
    fun Int.pad0(length: Int = 2) = toString().padStart(length, '0')

    Text(
        buildAnnotatedString {
            if (timer.name != null) {
                append(timer.name)
            } else {
                withStyle(
                    style = SpanStyle(
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colors.onBackground.copy(alpha = .5f)
                    )
                ) {
                    append(stringResource(R.string.timer_unnamed))
                }
            }
            withStyle(style = SpanStyle(fontSize = 10.sp)) {
                timer.duration.toComponents { hours, minutes, seconds, _ ->
                    append(" (${hours.pad0()}:${minutes.pad0()}:${seconds.pad0()})")
                }
            }
        },
        Modifier
            .fillMaxWidth()
            .clickable { onTimerClicked(timer) }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        style = typography.body1,
        color = MaterialTheme.colors.onBackground
    )
}
