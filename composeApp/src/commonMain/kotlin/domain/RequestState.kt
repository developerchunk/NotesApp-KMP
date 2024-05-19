package domain

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable

sealed class RequestState<out T> {

    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()

    fun isLoading() = this is Loading
    fun isSuccessful() = this is Success
    fun isError() = this is Error

    /**
     * Returns data if request is success from [Success].
     * @throws ClassCastException if current state or request is not [Success].
     * */
    fun getSuccessData() = (this as Success).data
    fun getSuccessDataOrNull(): T? {
        return try {
            (this as Success).data
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Returns an error message if request is error from an [Error].
     * @throws ClassCastException if current state or request is not [Error].
     * */
    fun getErrorMessage() = (this as Error).message
    fun getErrorMessageOrError(): String? {
        return try {
            (this as Error).message
        } catch (e: Exception) {
            null
        }
    }

    /**
     *  Compose function to display the result of the request.
     *  This Displays Function helps manage the different states of the request */
    @Composable
    fun DisplayResult(
        onIdle: (@Composable () -> Unit)? = null,
        onLoading: @Composable () -> Unit = {},
        onSuccess: @Composable (T) -> Unit,
        onError: @Composable (String) -> Unit = {},
        transitionSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
            fadeIn(tween(durationMillis = 300)) togetherWith
                    fadeOut(tween(durationMillis = 300))
        }
    ) {

        AnimatedContent(
            targetState = this,
            transitionSpec = transitionSpec,
            label = "Animated State",
        ) { state ->

            when (state) {
                is Idle -> {
                    onIdle?.invoke()
                }
                is Loading -> {
                    onLoading()
                }
                is Success -> {
                    onSuccess(state.getSuccessData())
                }
                is Error -> {
                    onError(state.getErrorMessage())
                }
            }

        }

    }

}

