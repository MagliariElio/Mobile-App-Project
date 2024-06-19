package it.polito.students.showteamdetails

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val isNeededRegistration: Boolean = false
)