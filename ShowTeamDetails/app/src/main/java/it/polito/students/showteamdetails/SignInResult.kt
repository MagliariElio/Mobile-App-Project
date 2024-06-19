package it.polito.students.showteamdetails

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?,
    val isNeededRegistration: Boolean
)

data class UserData(
    val userId: String,
    val username: String?,
    val email: String?,
    val phoneNumber: String?,
    val profilePictureUrl: String?
)