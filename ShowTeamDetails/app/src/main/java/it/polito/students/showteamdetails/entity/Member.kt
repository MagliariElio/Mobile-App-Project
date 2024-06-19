package it.polito.students.showteamdetails.entity

import androidx.compose.ui.graphics.ImageBitmap
import com.google.firebase.Timestamp
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.Utils.convertToTimestampLocalDate
import java.time.LocalDate
import java.util.Locale

data class Member(
    val id: String,
    var photo: ImageBitmap?,
    val name: String,
    val surname: String,
    val email: String,
    val online: Boolean,
    val password: String,
    val gender: Utils.Gender,
    val birthdate: LocalDate,
    val nickname: String,
    val bio: String,
    val address: String,
    val phoneNumber: String,
    val technicalSkills: List<String>,
    val languages: List<Pair<Locale, Int>>,
    var reviews: List<Review>,
    val privateEmail: Boolean,
    val privateBirthdate: Boolean,
    val privateGender: Boolean,
    val privatePhone: Boolean,
    val sound: Boolean,
    val vibration: Boolean,
    val language: Locale
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Member

        if (id != other.id) return false
        if (photo != other.photo) return false
        if (name != other.name) return false
        if (surname != other.surname) return false
        if (email != other.email) return false
        if (online != other.online) return false
        if (password != other.password) return false
        if (gender != other.gender) return false
        if (birthdate != other.birthdate) return false
        if (nickname != other.nickname) return false
        if (bio != other.bio) return false
        if (address != other.address) return false
        if (phoneNumber != other.phoneNumber) return false
        if (technicalSkills != other.technicalSkills) return false
        if (languages != other.languages) return false
        if (privateBirthdate != other.privateBirthdate) return false
        if (privateEmail != other.privateEmail) return false
        if (privatePhone != other.privatePhone) return false
        if (privateGender != other.privateGender) return false

        return true
    }

    override fun hashCode(): Int {
        var result = photo?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + surname.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + online.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + birthdate.hashCode()
        result = 31 * result + nickname.hashCode()
        result = 31 * result + bio.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + technicalSkills.hashCode()
        result = 31 * result + languages.hashCode()
        return result
    }
}

enum class RoleEnum {
    NO_ROLE_ASSIGNED,
    EXECUTIVE_LEADER,
    ADMINISTRATIVE_SUPPORTER,
    TECHNOLOGY_AND_IT_MANAGER,
    SALES_AND_MARKETING_LEADER,
    OPERATIONAL_AND_TECHNICAL_STAFFER
}


class FirebaseMember {
    lateinit var name: String
    lateinit var surname: String
    var imageUrl: String = ""
    lateinit var email: String
    var online: Boolean = false
    lateinit var password: String
    lateinit var gender: String
    lateinit var birthdate: Timestamp
    lateinit var nickname: String
    lateinit var bio: String
    lateinit var address: String
    lateinit var phoneNumber: String
    lateinit var technicalSkills: List<String>
    lateinit var languages: Map<String, Int>
    lateinit var reviews: List<FirebaseReview>
    var privateEmail: Boolean = false
    var privateBirthdate: Boolean = false
    var privateGender: Boolean = false
    var privatePhone: Boolean = false
    var sound: Boolean = false
    var vibration: Boolean = false
    lateinit var language: String
}

fun FirebaseMember.createFirebaseMember(member: Member): FirebaseMember {
    val languages = mutableMapOf<String, Int>()
    member.languages.forEach { (locale, level) -> languages[locale.language.toString()] = level }

    val firebaseMember = FirebaseMember()
    firebaseMember.name = member.name
    firebaseMember.surname = member.surname
    firebaseMember.email = member.email
    firebaseMember.online = member.online
    firebaseMember.password = member.password
    firebaseMember.gender = member.gender.name
    firebaseMember.birthdate = convertToTimestampLocalDate(member.birthdate)
    firebaseMember.nickname = member.nickname
    firebaseMember.bio = member.bio
    firebaseMember.address = member.address
    firebaseMember.phoneNumber = member.phoneNumber
    firebaseMember.technicalSkills = member.technicalSkills
    firebaseMember.languages = languages
    firebaseMember.reviews = convertReviewListToFirebaseList(member.reviews)
    firebaseMember.privateEmail = member.privateEmail
    firebaseMember.privatePhone = member.privatePhone
    firebaseMember.privateGender = member.privateGender
    firebaseMember.privateBirthdate = member.privateBirthdate
    firebaseMember.sound = member.sound
    firebaseMember.vibration = member.vibration
    firebaseMember.language = member.language.language
    return firebaseMember
}