package it.polito.students.showteamdetails

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    // Add a loading state
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _isNeededRegistration = MutableStateFlow(false)
    val isNeededRegistration: StateFlow<Boolean> = _isNeededRegistration.asStateFlow()

    fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

    fun setIsNeededRegistration(registration: Boolean) {
        _isNeededRegistration.value = registration
    }

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage,
                isNeededRegistration = result.isNeededRegistration
            )
        }
    }

    fun resetState() {
        _state.update { SignInState() }
        _loading.value = false
        _isNeededRegistration.value = false
    }
}