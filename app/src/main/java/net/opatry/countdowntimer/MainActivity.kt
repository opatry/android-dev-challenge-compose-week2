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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import net.opatry.countdowntimer.ui.component.TimerCircle
import net.opatry.countdowntimer.ui.component.TimerControls
import net.opatry.countdowntimer.ui.component.TimerLabel
import net.opatry.countdowntimer.ui.component.TimerList
import net.opatry.countdowntimer.ui.theme.MyTheme
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

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
    val state by viewModel.state.observeAsState(null)
    val tickInterval by viewModel.tickInterval.observeAsState(0.seconds)
    val coroutineScope = rememberCoroutineScope()

    val scaffoldState = rememberBackdropScaffoldState(if (state == null) BackdropValue.Revealed else BackdropValue.Concealed)

    BackdropScaffold(
        appBar = { },
        scaffoldState = scaffoldState,
        backLayerContent = {
            Column(
                Modifier.fillMaxWidth()
            ) {
                // TODO if list is empty, allow to create one
                TimerList(state?.timer, timers) { timer ->
                    coroutineScope.launch {
                        scaffoldState.conceal()
                    }
                    viewModel.start(timer)
                }
            }
        },
        frontLayerContent = {
            state?.let { uiState ->
                CountDownTimerLayout(
                    uiState.remaining,
                    tickInterval,
                    onFABClicked = {
                        if (uiState.remaining.isPositive()) {
                            coroutineScope.launch {
                                scaffoldState.reveal()
                            }
                            viewModel.stop()
                        } else {
                            coroutineScope.launch {
                                scaffoldState.conceal()
                            }
                            viewModel.start(uiState.timer)
                        }
                    }
                )
            } ?: Box {}
        }
    )
}

@Composable
@ExperimentalTime
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun CountDownTimerLayout(
    remainingDuration: Duration,
    tickInterval: Duration,
    onFABClicked: () -> Unit
) {
    remainingDuration.coerceAtLeast(0.seconds).toComponents { hours, minutes, seconds, _ ->
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(.8f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            TimerLabel(hours, minutes, seconds)
            TimerCircle(hours / 12f, minutes / 60f, seconds / 60f, tickInterval, onFABClicked)
            TimerControls(onClose = { /* TODO */ }, onDelete = { /* TODO */ })
        }
    }
}
