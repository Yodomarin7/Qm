package com.example.qm.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qm.ui.currencies.CurrenciesContract
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

interface BaseEvent
interface BaseState
interface BaseNav

abstract class BaseViewModel<Event: BaseEvent, State: BaseState, Nav: BaseNav>: ViewModel() {

    abstract fun initState(): State

    private val initialState by lazy { initState() }
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state

    protected fun setState(state: State.()-> State) {
        _state.value = _state.value.state()
    }

    private val _nav: Channel<Nav> = Channel(Channel.CONFLATED)
    val nav = _nav.receiveAsFlow()

    protected fun setNav(nav: Nav) {
        viewModelScope.launch {
            _nav.send(nav)
        }

    }

    abstract fun setEvent(event: Event)
}