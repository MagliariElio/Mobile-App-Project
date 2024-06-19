package it.polito.students.showteamdetails.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.students.showteamdetails.ErrorsPage
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.Review
import it.polito.students.showteamdetails.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(val userModel: UserModel) : ViewModel() {

    private val _allUsers = MutableStateFlow<List<Member>>(emptyList())
    val allUsers: StateFlow<List<Member>> = _allUsers

    private val _neighborUsers = MutableStateFlow<List<Member>>(emptyList())
    val neighborUsers: StateFlow<List<Member>> = _neighborUsers.asStateFlow()

    val imageViewModel: ProfileImageViewModel? = null

    var editReview by mutableStateOf("")
    var errorNewReview by mutableIntStateOf(-1)


    fun addReview(
        user: Member,
        review: Review,
        resetUsers: (List<Member>) -> Unit,
        users: List<Member>
    ) {
        if (review.message.isNotBlank()) {
            addFirebaseReview(user, review)
            val rev = user.reviews.toMutableList()
            rev.add(review)
            users[users.indexOfFirst { it.id == user.id }].reviews = rev.toList()
            resetUsers(users)

            errorNewReview = -1
        } else {
            errorNewReview = ErrorsPage.REVIEW_BLANK
        }
    }

    fun deleteReview(
        user: Member,
        review: Review,
        resetUsers: (List<Member>) -> Unit,
        users: List<Member>
    ) {
        resetReviewError()
        removeReviewFromUser(user, review)
        val rev = user.reviews.toMutableList()
        rev.removeIf { rew -> rew.message == review.message }
        user.reviews = rev.toList()
        users[users.indexOfFirst { it.id == user.id }].reviews = rev.toList()
        resetUsers(users)

    }

    fun resetReviewError() {
        errorNewReview = -1
    }

    init {
        // Avvia la raccolta dei dati di neighborUsers
        viewModelScope.launch {
            userModel.getNeighborUsers().collect { users ->
                _neighborUsers.value = users
            }
        }

        // Avvia la raccolta dei dati di allUsers
        viewModelScope.launch {
            userModel.getAllUsers().collect { users ->
                _allUsers.value = users
            }
        }
    }

    fun getNeighborUsers() = userModel.getNeighborUsers()

    fun getAllUsers() = userModel.getAllUsers()

    fun removeReviewFromUser(user: Member, review: Review) {
        viewModelScope.launch {
            userModel.removeReviewFromUser(user, review)
        }
    }

    fun addFirebaseReview(user: Member, review: Review) {
        viewModelScope.launch {
            userModel.addFirebaseReview(user, review)
        }
    }
}