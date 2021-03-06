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

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.milliseconds
import kotlin.time.minutes
import kotlin.time.seconds

@ExperimentalTime
data class Timer(val duration: Duration, val name: String? = null)

@ExperimentalTime
data class TimerState(val remaining: Duration, val timer: Timer)

@ExperimentalStdlibApi
@ExperimentalTime
class CounterViewModel(private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main) : ViewModel() {
    private val _state = MutableLiveData<TimerState?>()
    val state: LiveData<TimerState?>
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

    private var countDownTimer: CountDownTimer? = null

    private val tickIntervalMs
        get() = _tickInterval.value?.toLongMilliseconds() ?: 0L
    private val _tickInterval = MutableLiveData(1.seconds)
    val tickInterval: LiveData<Duration>
        get() = _tickInterval

    fun stop() {
        viewModelScope.launch(mainDispatcher) {
            _state.value = null
            countDownTimer?.cancel()
            countDownTimer = null
        }
    }

    fun start(timer: Timer) {
        viewModelScope.launch(mainDispatcher) {
            _timers.value = buildList {
                add(timer)
                val timers = _timers.value?.filterNot { it == timer }
                if (!timers.isNullOrEmpty()) {
                    addAll(timers)
                }
            }.distinct()

            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(timer.duration.inMilliseconds.roundToLong(), tickIntervalMs) {
                override fun onTick(millisUntilFinished: Long) {
                    val state = _state.value ?: return
                    _state.postValue(state.copy(remaining = millisUntilFinished.milliseconds))
                }

                override fun onFinish() {
                    _state.postValue(null)
                }
            }.also {
                _state.value = TimerState(timer.duration, timer)
                delay(300)
                it.start()
            }
        }
    }

    override fun onCleared() {
        countDownTimer?.cancel()
        countDownTimer = null
        super.onCleared()
    }
}
