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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.minutes
import kotlin.time.seconds

@ExperimentalTime
data class Timer(val duration: Duration, val name: String? = null)

@ExperimentalTime
sealed class TimerState {
    data class Reset(val duration: Duration?) : TimerState()
    data class Running(val remaining: Duration, val timer: Timer) : TimerState()
    data class Paused(val remaining: Duration, val timer: Timer) : TimerState() // TOOO __start__ blinking
    data class Done(val overdue: Duration, val timer: Timer) : TimerState() // TODO __start__ sound
}

@ExperimentalStdlibApi
@ExperimentalTime
class CounterViewModel(private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main) : ViewModel() {
    private val _state = MutableLiveData<TimerState>(TimerState.Reset(null))
    val state: LiveData<TimerState>
        get() = _state

    private val _timers = MutableLiveData(
        listOf(
            Timer(3.minutes, "🥚 Eggs — Boiled"),
            Timer(3.seconds, "Hurry up!!!"),
            Timer(3.hours + 35.minutes + 12.seconds, "⏳ Take your time"),
            Timer(40.seconds, "🏋️‍♂️Workout — Squats"),
            Timer(37.seconds),
            Timer(45.seconds, "🏋️‍♂️Workout — Plank"),
            Timer(25.minutes, "🍅 Pomodoro"),
            Timer(5.minutes, "⏸ Break"),
            Timer(15.minutes, "😴 Long break"),
        )
    )
    val timers: LiveData<List<Timer>>
        get() = _timers

    fun pause() {
        val state = _state.value as? TimerState.Running ?: return
        // TODO pause countdown scheduler
        viewModelScope.launch(mainDispatcher) {
            _state.value = TimerState.Paused(state.remaining, state.timer)
        }
    }

    fun resume() {
        val state = _state.value as? TimerState.Paused ?: return
        // TODO restart countdown scheduler
        viewModelScope.launch(mainDispatcher) {
            _state.value = TimerState.Running(state.remaining, state.timer)
        }
    }

    fun reset() {
        if (_state.value !is TimerState.Reset) {
            // TODO stop countdown scheduler
        }
        viewModelScope.launch(mainDispatcher) {
            _state.value = TimerState.Reset(null)
        }
    }

    fun start(timer: Timer) {
        viewModelScope.launch(mainDispatcher) {
            // TODO review poor state & timers modeling

            // keep list of timers up to date keeping last used first
            _timers.value = buildList {
                add(timer)
                val timers = _timers.value?.filterNot { it == timer }
                if (!timers.isNullOrEmpty()) {
                    addAll(timers)
                }
            }
            _state.value = TimerState.Running(timer.duration, timer)
        }
    }
}
