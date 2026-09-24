package com.sagar.matchmate.core.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * Base ViewModel class supporting MVI architecture with reactive State, Intent handling, and Effects.
 */

/**
 * Platform-independent BaseViewModel for MVI architecture.
 *
 * Responsibilities:
 *
 * 1. Receive UI intents
 * 2. Process intents sequentially
 * 3. Maintain immutable UI state
 * 4. Emit one-time UI effects
 * 5. Provide a coroutine scope
 *
 * This implementation does not depend on Android's ViewModel.
 */
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<
        Intent : UiIntent,
        State : UiState,
        Effect : UiEffect
        >(
    initialState: State
) : ViewModel() {

    /**
     * Coroutine scope owned by this ViewModel.
     *
     * SupervisorJob ensures that a failure in one coroutine
     * does not automatically cancel unrelated coroutines.
     */
    protected val scope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Main.immediate
    )

    /**
     * Internal mutable state.
     */
    private val _uiState = MutableStateFlow(initialState)

    /**
     * Public immutable state.
     */
    val uiState: StateFlow<State> =
        _uiState.asStateFlow()

    /**
     * One-time UI effects.
     */
    private val _uiEffect =
        Channel<Effect>(Channel.BUFFERED)

    /**
     * Public effect stream.
     */
    val uiEffect: Flow<Effect> =
        _uiEffect.receiveAsFlow()

    /**
     * Intent queue.
     *
     * Intents are processed sequentially by observeIntents().
     */
    private val _intentChannel =
        Channel<Intent>(Channel.BUFFERED)

    /**
     * Current state snapshot.
     */
    protected val currentState: State
        get() = _uiState.value

    init {
        observeIntents()
    }

    /**
     * Starts collecting intents.
     *
     * Because Channel.receiveAsFlow() is collected by one
     * coroutine, intents are processed in order.
     */
    fun observeIntents() {

        scope.launch {

            _intentChannel
                .receiveAsFlow()
                .collect { intent ->

                    try {

                        handleIntent(intent)

                    } catch (exception: CancellationException) {

                        throw exception

                    } catch (exception: Throwable) {

                        onError(exception)
                    }
                }
        }
    }

    /**
     * Entry point used by Compose UI.
     */
    fun sendIntent(intent: Intent) {

        _intentChannel.trySend(intent)
    }

    /**
     * Handles an incoming Intent.
     */
    protected abstract suspend fun handleIntent(
        intent: Intent
    )

    /**
     * Centralized error handling.
     */
    protected open fun onError(
        exception: Throwable
    ) {
        // Override in feature ViewModel.
    }

    /**
     * Updates the current state using a reducer.
     */
    protected fun setState(
        reducer: State.() -> State
    ) {

        _uiState.update { current ->
            current.reducer()
        }
    }

    /**
     * Sends a one-time UI effect.
     */
    protected fun sendEffect(
        effect: Effect
    ) {

        _uiEffect.trySend(effect)
    }

    /**
     * Cancels all ViewModel coroutines.
     */
    fun clear() {

        scope.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        clear()
    }
}