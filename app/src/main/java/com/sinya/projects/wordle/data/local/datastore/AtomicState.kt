package com.sinya.projects.wordle.data.local.datastore

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Stable
class AtomicState<T>(initial: T) {

    private val flow = MutableStateFlow(initial)

    val value: T get() = flow.value
    val state: StateFlow<T> = flow

    fun update(block: (T) -> T) {
        flow.update(block)
    }

    fun set(new: T) {
        flow.value = new
    }
}