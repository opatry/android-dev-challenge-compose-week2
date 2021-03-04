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
package net.opatry.countdowntimer

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import net.opatry.countdowntimer.ui.component.TimerCircle
import net.opatry.countdowntimer.ui.component.TimerControls
import net.opatry.countdowntimer.ui.component.TimerLabel
import net.opatry.countdowntimer.ui.component.TimerList
import net.opatry.countdowntimer.ui.theme.MyTheme
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

class MainActivity : AppCompatActivity() {
    @ExperimentalTime
    @ExperimentalMaterialApi
    @ExperimentalStdlibApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
@ExperimentalTime
@ExperimentalStdlibApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        CountDownTimerDispatcher()
    }
}

@Composable
@ExperimentalTime
@ExperimentalStdlibApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun CountDownTimerDispatcher() {
    val viewModel = viewModel<CounterViewModel>()
    val timers by viewModel.timers.observeAsState(listOf())
    val state by viewModel.state.observeAsState(TimerState.Reset(null))

    state.let { uiState ->
        when (uiState) {
            is TimerState.Reset -> CountDownTimerReset {
                // TODO TODO handle no timer and ask for a duration & name
                viewModel.start(timers[0])
            }
            is TimerState.Running -> {
                val activeTimer = uiState.timer
                CountDownTimerLayout(
                    activeTimer,
                    timers,
                    onTimerClicked = {
                        viewModel.start(it)
                    },
                    onFABClicked = {
                        viewModel.reset()
                        // viewModel.pause()
                    }
                )
            }
            // is TimerState.Paused -> CountDownTimerLayout(
            //     onTimerClicked = {}
            // ) {
            //     viewModel.resume()
            // }
            // is TimerState.Done -> CountDownTimerLayout(
            //     onTimerClicked = {}
            // ) {
            //     viewModel.reset()
            // }
        }
    }
}

@Composable
fun CountDownTimerReset(onFABClicked: () -> Unit) {
    Column {
        Text("TODO CREATE NEW TIMER", Modifier.padding(48.dp))
        FloatingActionButton(onClick = onFABClicked) {
            Icon(Icons.TwoTone.PlayArrow, stringResource(R.string.timer_start))
        }
    }
}

@Composable
@ExperimentalTime
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun CountDownTimerLayout(
    activeTimer: Timer,
    timers: List<Timer>,
    onTimerClicked: (Timer) -> Unit,
    onFABClicked: () -> Unit
) {
    // FIXME there is a bug when finishing the first progress for the first time, progress sticks to max
    var remainingDuration by remember/*Saveable TODO Duration to bundle*/ { mutableStateOf(activeTimer.duration) }
    LaunchedEffect(activeTimer/*.name*/) {
        // FIXME how to smoothly animate the progress and update remaining time accordingly
        // FIXME not stopped even when key change :(
        while (!remainingDuration.isNegative() && isActive) {
            remainingDuration = remainingDuration - 16.milliseconds
            delay(16)
        }
    }

    val hours = (remainingDuration.inHours % 12).coerceAtLeast(.0)
    val minutes = (remainingDuration.inMinutes % 60).coerceAtLeast(.0)
    val seconds = (remainingDuration.inSeconds % 60).coerceAtLeast(.0)

    BackdropScaffold(
        appBar = { },
        backLayerContent = {
            Column(
                Modifier.fillMaxWidth()
            ) {
                TimerList(activeTimer, timers, onTimerClicked)
            }
        },
        frontLayerContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // FIXME values rounding is incorrect
                //  1. when switching from 35min 30sec to 35min 29sec, it displays 34min 29sec
                //  2. when seconds is almost 0 but not yet, digit changes to 0 before the progress reach 12O'clock
                //      (especially visible for last second)
                val hoursI = hours.roundToInt()
                val minutesI = minutes.roundToInt()
                val secondsI = seconds.roundToInt()
                TimerLabel(hoursI, minutesI, secondsI)
                val hoursP = if (hoursI == 0) 0f else (hours / 12).toFloat()
                val minutesP = if (minutesI == 0) 0f else (minutes / 60).toFloat()
                val secondsP = if (secondsI == 0) 0f else (seconds / 60).toFloat()
                TimerCircle(hoursP, minutesP, secondsP, onFABClicked)
                TimerControls(onClose = {}, onDelete = {})
            }
        }
    )
}
