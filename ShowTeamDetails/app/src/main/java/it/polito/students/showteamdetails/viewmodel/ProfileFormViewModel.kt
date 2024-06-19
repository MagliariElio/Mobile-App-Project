package it.polito.students.showteamdetails.viewmodel

import android.annotation.SuppressLint
import android.telephony.PhoneNumberUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.students.showteamdetails.ErrorsPage
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.Review
import it.polito.students.showteamdetails.model.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

class ProfileFormViewModel(member: Member) : ViewModel() {

    fun cancelButtonClicked() {  //Come back to the view page
        nameField.editValue = nameField.value
        surnameField.editValue = surnameField.value
        phoneNumberField.editValue = phoneNumberField.value
        phoneNumberField.editPrivate = phoneNumberField.privateValueField
        birthdateField.editPrivate = birthdateField.privateValueField
        genderField.editPrivate = genderField.privateValueField
        addressField.editValue = addressField.value
        bioField.editValue = bioField.value
        technicalSkillsField.editValue = technicalSkillsField.value
        languageSkillsField.editValue = languageSkillsField.value
        emailField.editValue = emailField.value
        emailField.editPrivate = emailField.privateValueField
        oldPasswordField.editValue = ""
        newPasswordField.editValue = ""
        repeatPasswordField.editValue = ""
        onlineSwitchField.editValue = onlineSwitchField.value
        imageField.bitmapEdit.value = imageField.bitmapView.value
    }

    //Validate for registration page
    fun validateFormRegister() {
        nameField.validate()
        surnameField.validate()
        nicknameField.validate()
        addressField.validate()
        phoneNumberField.validate()
        birthdateField.validateBirthday()

        //If at least one of the errors is not empty, the registration cannot be done
        val willEdit = listOf(
            nameField.error,
            surnameField.error,
            nicknameField.error,
            addressField.error,
            phoneNumberField.error,
            birthdateField.errorDate
        ).none { it != -1 }

        if (willEdit) {
            nameField.setVal(nameField.editValue)
            surnameField.setVal(surnameField.editValue)
            nicknameField.setVal(nicknameField.editValue)
            addressField.setVal(addressField.editValue)
            phoneNumberField.setVal(phoneNumberField.editValue)
            birthdateField.updateDate(birthdateField.editDate)
        }
    }

    fun validateForm() {
        if (oldPasswordField.editValue.isBlank()
            && newPasswordField.editValue.isBlank()
            && repeatPasswordField.editValue.isBlank()
        ) { //Editing profile info

            nameField.validate()
            surnameField.validate()
            phoneNumberField.validate()
            addressField.validate()
            bioField.validate()
            technicalSkillsField.validate()
            languageSkillsField.validate()
            emailField.validate()

            val willEdit = listOf(
                //If at least one of the errors is not empty, the editing mode cannot be exited
                imageField.error,
                nameField.error,
                surnameField.error,
                phoneNumberField.error,
                addressField.error,
                bioField.error,
                technicalSkillsField.error,
                languageSkillsField.error,
                emailField.error
            ).none { it != -1 }

            if (willEdit) {
                nameField.setVal(nameField.editValue)
                surnameField.setVal(surnameField.editValue)
                phoneNumberField.setVal(phoneNumberField.editValue)
                phoneNumberField.setPrivateFieldValue(phoneNumberField.editPrivate)
                birthdateField.setPrivateFieldValue(birthdateField.editPrivate)
                genderField.setPrivateFieldValue(genderField.editPrivate)
                addressField.setVal(addressField.editValue)
                bioField.setVal(bioField.editValue)
                technicalSkillsField.setVal(technicalSkillsField.editValue)
                languageSkillsField.setVal(languageSkillsField.editValue)
                emailField.setVal(emailField.editValue)
                emailField.setPrivateFieldValue(emailField.editPrivate)
                onlineSwitchField.setVal(onlineSwitchField.editValue)
                imageField.bitmapView.value = imageField.bitmapEdit.value
            }
        } else { //Editing profile info and password
            imageField.validate()
            nameField.validate()
            surnameField.validate()
            phoneNumberField.validate()
            addressField.validate()
            bioField.validate()
            technicalSkillsField.validate()
            languageSkillsField.validate()
            emailField.validate()
            oldPasswordField.validate()
            newPasswordField.validate()
            repeatPasswordField.validate(newPasswordField.editValue)

            val willEdit =
                listOf(            //If at least one of the errors is not empty, the editing mode cannot be exited
                    imageField.error,
                    nameField.error,
                    surnameField.error,
                    phoneNumberField.error,
                    addressField.error,
                    bioField.error,
                    technicalSkillsField.error,
                    languageSkillsField.error,
                    emailField.error,
                    newPasswordField.error,
                    oldPasswordField.error,
                    repeatPasswordField.error
                ).none { it != -1 }

            if (willEdit) {
                nameField.setVal(nameField.editValue)
                surnameField.setVal(surnameField.editValue)
                phoneNumberField.setVal(phoneNumberField.editValue)
                phoneNumberField.setPrivateFieldValue(phoneNumberField.editPrivate)
                birthdateField.setPrivateFieldValue(birthdateField.editPrivate)
                genderField.setPrivateFieldValue(genderField.editPrivate)
                addressField.setVal(addressField.editValue)
                bioField.setVal(bioField.editValue)
                technicalSkillsField.setVal(technicalSkillsField.editValue)
                languageSkillsField.setVal(languageSkillsField.editValue)
                emailField.setVal(emailField.editValue)
                emailField.setPrivateFieldValue(emailField.editPrivate)
                onlineSwitchField.setVal(onlineSwitchField.editValue)
                oldPasswordField.setVal(newPasswordField.editValue)
                oldPasswordField.editValue = ""
                newPasswordField.editValue = ""
                repeatPasswordField.editValue = ""
                imageField.bitmapView.value = imageField.bitmapEdit.value
            }
        }
    }

    fun areThereUnsavedChanges(): Boolean =
        if (oldPasswordField.editValue.isBlank()) {
            !(
                    imageField.bitmapView.value == imageField.bitmapEdit.value &&
                            nameField.value == nameField.editValue &&
                            surnameField.value == surnameField.editValue &&
                            phoneNumberField.value == phoneNumberField.editValue &&
                            phoneNumberField.privateValueField == phoneNumberField.editPrivate &&
                            birthdateField.privateValueField == birthdateField.editPrivate &&
                            genderField.privateValueField == genderField.editPrivate &&
                            addressField.value == addressField.editValue &&
                            bioField.value == bioField.editValue &&
                            technicalSkillsField.value == technicalSkillsField.editValue &&
                            languageSkillsField.value == languageSkillsField.editValue &&
                            emailField.value == emailField.editValue &&
                            emailField.privateValueField == emailField.editPrivate &&
                            onlineSwitchField.value == onlineSwitchField.editValue
                    )
        } else {
            !(
                    imageField.bitmapView.value == imageField.bitmapEdit.value &&
                            nameField.value == nameField.editValue &&
                            surnameField.value == surnameField.editValue &&
                            phoneNumberField.value == phoneNumberField.editValue &&
                            phoneNumberField.privateValueField == phoneNumberField.editPrivate &&
                            birthdateField.privateValueField == birthdateField.editPrivate &&
                            genderField.privateValueField == genderField.editPrivate &&
                            addressField.value == addressField.editValue &&
                            bioField.value == bioField.editValue &&
                            technicalSkillsField.value == technicalSkillsField.editValue &&
                            languageSkillsField.value == languageSkillsField.editValue &&
                            emailField.value == emailField.editValue &&
                            emailField.privateValueField == emailField.editPrivate &&
                            onlineSwitchField.value == onlineSwitchField.editValue &&
                            newPasswordField.editValue != oldPasswordField.value
                    )
        }

    fun areThereErrors(): Boolean = (
            imageField.error != -1 ||
                    nameField.error != -1 ||
                    surnameField.error != -1 ||
                    phoneNumberField.error != -1 ||
                    addressField.error != -1 ||
                    bioField.error != -1 ||
                    technicalSkillsField.error != -1 ||
                    languageSkillsField.error != -1 ||
                    emailField.error != -1 ||
                    oldPasswordField.error != -1 ||
                    newPasswordField.error != -1 ||
                    repeatPasswordField.error != -1
            )

    fun areThereErrorsInRegister(): Boolean = (
            nameField.error != -1 ||
                    surnameField.error != -1 ||
                    nicknameField.error != -1 ||
                    addressField.error != -1 ||
                    languageSkillsField.error != -1 ||
                    emailField.error != -1 ||
                    oldPasswordField.error != -1 ||
                    newPasswordField.error != -1 ||
                    repeatPasswordField.error != -1
            )

    fun createUser(member: Member) {
        viewModelScope.launch {
            val member = UserModel().createNewUser(member)
            if (member != null) {
                Utils.setMemberAccessed(member)
            }
        }
    }

    fun editUser(updatedUser: Member) {
        viewModelScope.launch {
            UserModel().editUser(updatedUser)
        }
    }

    val id = member.id
    val imageField = ProfileImageViewModel(member.photo)
    val nameField = NameFieldViewModel(member.name)
    val surnameField = SurnameFieldViewModel(member.surname)
    val birthdateField = BirthdateFieldViewModel(member.birthdate, member.privateBirthdate)
    val genderField = GenderFieldViewModel(member.gender, member.privateGender)
    val phoneNumberField = PhoneNumberFieldViewModel(member.phoneNumber, member.privatePhone)
    val addressField = AddressFieldViewModel(member.address)
    val bioField = BioFieldViewModel(member.bio)
    val technicalSkillsField = TechnicalSkillsFieldViewModel(member.technicalSkills)
    val languageSkillsField = LanguageSkillsFieldViewModel(member.languages)
    val nicknameField = NicknameFieldViewModel(member.nickname)
    val emailField = EmailFieldViewModel(member.email, member.privateEmail)
    val oldPasswordField = OldPasswordFieldViewModel(member.password)
    val newPasswordField = NewPasswordFieldViewModel()
    val repeatPasswordField = RepeatPasswordFieldViewModel()
    val onlineSwitchField = OnlineSwitchFieldViewModel(member.online)
    val reviewsField = ReviewsFieldViewModel(member.reviews)

    val accountSettings =
        AccountSettingsViewModel(soundActive = member.sound, vibrationActive = member.vibration)


    var reviewText by mutableStateOf(TextFieldValue())
}


class NameFieldViewModel(name: String) {
    var value by mutableStateOf(name)
        private set

    var editValue by mutableStateOf(name)

    var error by mutableIntStateOf(-1)
        private set

    fun setVal(s: String) {
        value = s
    }

    fun validate() {
        error = when {
            editValue.isBlank() -> ErrorsPage.NAME_BLANK
            editValue.length < 2 -> ErrorsPage.NAME_TOO_SHORT
            editValue.any {
                it.isWhitespace() || it.isDigit()
            } -> ErrorsPage.NAME_INVALID

            else -> -1
        }
    }
}

class SurnameFieldViewModel(surname: String) {
    var value by mutableStateOf(surname)
        private set

    var editValue by mutableStateOf(surname)

    var error by mutableIntStateOf(-1)
        private set

    fun setVal(s: String) {
        value = s
    }

    fun validate() {
        error = when {
            editValue.isBlank() -> ErrorsPage.SURNAME_BLANK
            editValue.length < 2 -> ErrorsPage.SURNAME_TOO_SHORT
            editValue.any { it.isWhitespace() || it.isDigit() } -> ErrorsPage.SURNAME_INVALID
            else -> -1
        }
    }
}

class ProfileImageViewModel(pictureParam: ImageBitmap? = null) {

    var error by mutableIntStateOf(-1)
        private set

    var bitmapView = mutableStateOf(pictureParam)
    var bitmapEdit = mutableStateOf(pictureParam)
    var showRemovePhoto by mutableStateOf(false)
    var showConfirmRemovePhoto by mutableStateOf(false)

    fun onTakePhoto(bitmap: ImageBitmap) {
        bitmapEdit.value = bitmap

        exitCameraMode()

        showRemovePhoto = true
    }

    var isOpenedCamera by mutableStateOf(false)
        private set

    fun enterCameraMode() {
        isOpenedCamera = true
    }

    fun exitCameraMode() {
        isOpenedCamera = false
    }

    fun validate() {

    }

    fun removePhoto() {
        bitmapEdit.value = null
        bitmapView.value = null

        showRemovePhoto = false


        fun validate() {
            error = -1

        }
    }
}

class BirthdateFieldViewModel(birthdate: LocalDate, privateBirthday: Boolean) {
    var value by mutableStateOf(birthdate)
        private set

    var errorDate by mutableIntStateOf(-1)

    var editDate by mutableStateOf(birthdate)

    fun updateDate(birthdate: LocalDate) {
        editDate = birthdate
    }

    var privateValueField by mutableStateOf(privateBirthday)
        private set

    var editPrivate by mutableStateOf(privateBirthday)

    fun togglePrivate() {
        editPrivate = !editPrivate
    }

    fun setPrivateFieldValue(p: Boolean) {
        privateValueField = p
    }

    override fun toString(): String {
        return this.value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    fun dateToString(): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return editDate.format(dateFormatter)
    }

    fun validateBirthday() {
        val currentDate = LocalDate.now()
        val age = Period.between(editDate, currentDate).years

        errorDate = when {
            editDate.isAfter(currentDate) -> ErrorsPage.BIRTHDAY_DATE
            age < 18 -> ErrorsPage.BIRTHDAY_TOO_YOUNG // error message for under 18
            else -> -1
        }
    }

    fun resetErrorBirthdayDate() {
        val currentDate = LocalDate.now()
        if (editDate.isBefore(currentDate) && Period.between(editDate, currentDate).years >= 18) {
            errorDate = -1
        }
    }
}

class GenderFieldViewModel(gender: Utils.Gender, privateGender: Boolean) {
    var value by mutableStateOf(gender)
        private set

    var editGender by mutableStateOf(gender)

    var privateValueField by mutableStateOf(privateGender)
        private set

    fun setVal(gender: Utils.Gender) {
        value = gender
    }

    var editPrivate by mutableStateOf(privateGender)

    var error by mutableIntStateOf(-1)

    fun togglePrivate() {
        editPrivate = !editPrivate
    }

    fun setPrivateFieldValue(p: Boolean) {
        privateValueField = p
    }
}

class PhoneNumberFieldViewModel(phoneNumber: String, privatePhone: Boolean) {
    var value by mutableStateOf(phoneNumber)
        private set

    var editValue by mutableStateOf(phoneNumber)

    var error by mutableIntStateOf(-1)
        private set

    var privateValueField by mutableStateOf(privatePhone)
        private set

    var editPrivate by mutableStateOf(privatePhone)

    fun togglePrivate() {
        editPrivate = !editPrivate
    }

    fun setPrivateFieldValue(p: Boolean) {
        privateValueField = p
    }

    fun setVal(s: String) {
        value = s
    }

    fun validate() {
        if (editValue.isBlank()) {
            error = ErrorsPage.PHONE_NUMBER_BLANK
            return
        }

        error =
                //in order to accept international numbers also
            if (!PhoneNumberUtils.isGlobalPhoneNumber(editValue) ||
                editValue.length < 8 || editValue.length > 20
            ) {
                ErrorsPage.PHONE_NUMBER_INVALID
            } else {
                -1
            }
    }
}

class AddressFieldViewModel(address: String) {
    var value by mutableStateOf(address)
        private set

    var editValue by mutableStateOf(address)

    var error by mutableIntStateOf(-1)
        private set

    fun setVal(s: String) {
        value = s
    }

    fun validate() {
        error = when {
            editValue.isBlank() -> ErrorsPage.ADDRESS_BLANK
            !editValue.contains(",") -> ErrorsPage.ADDRESS_COMMAS
            editValue.split(",").size < 3 -> ErrorsPage.ADDRESS_COMPLETE
            else -> -1
        }
    }
}

class BioFieldViewModel(bio: String) {
    var value by mutableStateOf(bio)
        private set

    var editValue by mutableStateOf(bio)

    var error by mutableIntStateOf(-1)
        private set

    fun setVal(s: String) {
        value = s
    }

    fun validate() {
        error = when {
            editValue.length > 500 -> ErrorsPage.BIO_CHARACTERS
            else -> -1
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
class TechnicalSkillsFieldViewModel(skills: List<String>) {
    var value = mutableStateListOf<String>().apply { addAll(skills) }
        private set

    var editValue = mutableStateListOf<String>().apply { addAll(skills) }

    fun setVal(list: MutableList<String>) {
        value.clear()
        value.addAll(list)
    }

    fun removeEditSkill(value: String) {
        editValue.remove(value)
    }

    var error by mutableIntStateOf(-1)
        private set

    fun validate() {
        error = -1

    }
}

@SuppressLint("MutableCollectionMutableState")
class LanguageSkillsFieldViewModel(languages: List<Pair<Locale, Int>>) {
    var value = mutableStateListOf<Pair<Locale, Int>>().apply { addAll(languages) }
        private set

    var editValue = mutableStateListOf<Pair<Locale, Int>>().apply { addAll(languages) }

    var error by mutableIntStateOf(-1)
        private set

    fun setVal(list: List<Pair<Locale, Int>>) {
        value.clear()
        value.addAll(list)
    }

    fun addEditVal(language: Locale, level: Int) {
        var found = false
        for (i in editValue.indices) {
            if (editValue[i].first == language) {
                editValue[i] = editValue[i].copy(second = level)
                found = true
                break
            }
        }
        if (!found) {
            editValue.add(Pair(language, level))
        }
        error = -1
    }

    fun removeEditVal(language: Locale, level: Int) {
        editValue.remove(Pair(language, level))
        error = -1
    }

    fun validate() {
        error = -1
    }
}

class NicknameFieldViewModel(nickname: String) {
    var value by mutableStateOf(nickname)
        private set

    var editValue by mutableStateOf(nickname)

    var error by mutableIntStateOf(-1)
        private set

    fun setVal(s: String) {
        value = s
    }

    fun validate() {
        error = when {
            editValue.isBlank() -> ErrorsPage.SURNAME_BLANK
            editValue.length < 2 -> ErrorsPage.SURNAME_TOO_SHORT
            editValue.any { it.isWhitespace() || it.isDigit() } -> ErrorsPage.SURNAME_INVALID
            else -> -1
        }
    }

}

class EmailFieldViewModel(email: String, privateEmail: Boolean) {
    var value by mutableStateOf(email)
        private set

    var editValue by mutableStateOf(email)

    var error by mutableIntStateOf(-1)
        private set

    var privateValueField by mutableStateOf(privateEmail)
        private set

    var editPrivate by mutableStateOf(privateEmail)

    fun togglePrivate() {
        editPrivate = !editPrivate
    }

    fun setPrivateFieldValue(p: Boolean) {
        privateValueField = p
    }

    fun setVal(s: String) {
        value = s
    }

    fun validate() {
        error = if (editValue.isBlank()) {
            ErrorsPage.MAIL_BLANK
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editValue).matches()) {
            ErrorsPage.MAIL_INVALID
        } else {
            -1
        }
    }
}

class OldPasswordFieldViewModel(password: String) {

    var value by mutableStateOf(password)
        private set

    var editValue by mutableStateOf(password)

    var error by mutableIntStateOf(-1)
        private set

    fun setVal(s: String) {
        value = s
    }

    fun validate() {
        val lowercaseRegex = Regex("[a-z]")
        val uppercaseRegex = Regex("[A-Z]")
        val digitRegex = Regex("[0-9]")
        val specialCharRegex = Regex("[^A-Za-z0-9]")

        error = when {
            editValue.isBlank() -> ErrorsPage.OLD_PASSWORD_BLANK
            editValue != value -> ErrorsPage.OLD_PASSWORD_INCORRECT
            !lowercaseRegex.containsMatchIn(editValue) -> ErrorsPage.PASSWORD_LOWERCASE
            !uppercaseRegex.containsMatchIn(editValue) -> ErrorsPage.PASSWORD_UPPERCASE
            !digitRegex.containsMatchIn(editValue) -> ErrorsPage.PASSWORD_DIGITS
            !specialCharRegex.containsMatchIn(editValue) -> ErrorsPage.PASSWORD_SPECIAL_CHAR
            else -> -1
        }
    }
}

class NewPasswordFieldViewModel() {
    var editValue by mutableStateOf("")

    var error by mutableIntStateOf(-1)
        private set

    fun validate() {
        if (editValue.isNotBlank()) {
            val lowercaseRegex = Regex("[a-z]")
            val uppercaseRegex = Regex("[A-Z]")
            val digitRegex = Regex("[0-9]")
            val specialCharRegex = Regex("[^A-Za-z0-9]")

            error = when {
                editValue.length < 8 -> ErrorsPage.NEW_PASSWORD_LEN
                !lowercaseRegex.containsMatchIn(editValue) -> ErrorsPage.PASSWORD_LOWERCASE
                !uppercaseRegex.containsMatchIn(editValue) -> ErrorsPage.PASSWORD_UPPERCASE
                !digitRegex.containsMatchIn(editValue) -> ErrorsPage.PASSWORD_DIGITS
                !specialCharRegex.containsMatchIn(editValue) -> ErrorsPage.PASSWORD_SPECIAL_CHAR
                else -> -1
            }
        }
    }
}

class RepeatPasswordFieldViewModel() {
    var editValue by mutableStateOf("")

    var error by mutableIntStateOf(-1)
        private set

    fun setVal(s: String) {
        editValue = s
    }

    fun validate(newPassword: String) {
        if (editValue.isNotBlank()) {
            error = if (editValue != newPassword) {
                ErrorsPage.REPEAT_PASSWORD_NOT_MATCH
            } else {
                -1
            }
        }
    }
}

class OnlineSwitchFieldViewModel(online: Boolean) {
    var value by mutableStateOf(online)
        private set

    var editValue by mutableStateOf(online)

    fun setVal(b: Boolean) {
        value = b
    }

    fun toggleVal() {
        editValue = !editValue
    }
}

class ReviewsFieldViewModel(reviewsList: List<Review>) {
    var reviews = mutableStateListOf<Review>().apply { addAll(reviewsList) }
        private set

    var editReviews = mutableStateListOf<Review>().apply { addAll(reviewsList) }
        private set

    var editReview = mutableStateOf("")

    var errorNewReview by mutableIntStateOf(-1)

    fun setVal(reviewsList: List<Review>) {
        reviews.clear()
        reviews.addAll(reviewsList)
    }

    fun setEditVal(reviewsList: List<Review>) {
        this.errorNewReview = -1
        editReviews.clear()
        editReviews.addAll(reviewsList)
    }

    fun addReview(review: Review) {
        if (review.message.isNotBlank()) {
            editReviews.add(review)
            errorNewReview = -1
        } else {
            errorNewReview = ErrorsPage.REVIEW_BLANK
        }
    }

    fun deleteReview(review: Review) {
        resetReviewError()
        editReviews.remove(review)
    }

    fun resetReviewError() {
        errorNewReview = -1
    }
}

class AccountSettingsViewModel(
    vibrationActive: Boolean = false,
    soundActive: Boolean = false
) { // default are set to mute

    var vibration by mutableStateOf(vibrationActive)
        private set

    var sound by mutableStateOf(soundActive)
        private set

    fun toggleVibration() {
        vibration = !vibration
        CoroutineScope(Dispatchers.IO).launch {
            UserModel().toggleAccountSettingOption("vibration", vibration)
        }
    }

    fun toggleSound() {
        sound = !sound
        CoroutineScope(Dispatchers.IO).launch {
            UserModel().toggleAccountSettingOption("sound", sound)
        }
    }
}