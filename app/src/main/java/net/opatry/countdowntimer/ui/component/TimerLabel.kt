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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import net.opatry.countdowntimer.ui.theme.ReemKufi
import net.opatry.countdowntimer.ui.theme.typography
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@Composable
@ExperimentalTime
fun TimerLabel(hours: Int, minutes: Int, seconds: Int) {
    fun Int.pad0(length: Int = 2) = toString().padStart(length, '0')

    // TODO if 0 reach, display something different or make it blink?
    Text(
        // FIXME text "moves" because it's not a monospaced font, how could we make each "bucket" of 2-digits stable in size?
        buildAnnotatedString {
            // TODO padded string 2 digits, pad number with alpha
            // TODO transition anim alpha of pad number when changing from 0 to N or N to 0
            withStyle(style = SpanStyle(color = DurationUnit.HOURS.color)) {
                append(hours.pad0())
            }
            withStyle(style = SpanStyle(color = MaterialTheme.colors.onSurface.copy(alpha = .4f))) {
                append(" : ")
            }
            withStyle(style = SpanStyle(color = DurationUnit.MINUTES.color)) {
                append(minutes.pad0())
            }
            withStyle(style = SpanStyle(color = MaterialTheme.colors.onSurface.copy(alpha = .4f))) {
                append(" : ")
            }
            withStyle(style = SpanStyle(color = DurationUnit.SECONDS.color)) {
                append(seconds.pad0())
            }
        },
        Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = typography.h3
    )
}
