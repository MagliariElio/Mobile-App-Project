package it.polito.students.showteamdetails

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import it.polito.students.showteamdetails.MainActivity.Companion.pictureProfile1
import it.polito.students.showteamdetails.MainActivity.Companion.pictureProfile2
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.MemberInfoTeam
import it.polito.students.showteamdetails.entity.Review
import it.polito.students.showteamdetails.entity.RoleEnum
import it.polito.students.showteamdetails.viewmodel.ProfileFormViewModel
import it.polito.students.showteamdetails.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.TreeSet
import kotlin.math.abs


object Utils {

    private val uploadFilePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    // This simulate the information about the member accessed in the application
    val memberAnonymus = Member(
        id = "",
        photo = null,
        name = "Anonymous user",
        surname = "",
        email = "",
        online = false,
        gender = Gender.PREFER_NOT_TO_SAY,
        bio = "",
        nickname = "",
        password = "",
        address = "",
        birthdate = LocalDate.now(),
        phoneNumber = "+",
        technicalSkills = listOf(),
        languages = listOf(),
        reviews = listOf(),
        privateEmail = true,
        privateGender = true,
        privatePhone = true,
        privateBirthdate = true,
        sound = false,
        vibration = false,
        language = Locale("en")
    )

    private val _member = Member(
        id = "tfOVVj8yLO9d8uVNhEMa",
        photo = pictureProfile1,
        name = "Margherita",
        surname = "Franceschini",
        email = "ma.fra@example.com",
        online = true,
        gender = Gender.Female,
        bio = "I'm passionate about technology and love helping others.",
        nickname = "Marghe",
        password = "Marghepassword123!",
        address = "123, Roma, Street",
        birthdate = LocalDate.of(1990, 6, 15),
        phoneNumber = "+1234567890",
        technicalSkills = listOf(
            "Data Analysis",
            "Team Coordination"
        ),
        languages = listOf(
            Pair(Locale.ITALIAN, 5),
            Pair(Locale.GERMAN, 4),
            Pair(Locale.FRENCH, 2),
            Pair(Locale.CHINESE, 4),
            Pair(Locale.KOREAN, 3),
        ),
        reviews = listOf(/*
            Review(
                message = "Excellent team player!",
                dateCreation = LocalDateTime.now(),
                createdBy = memberAnonymus,
                dateModification = LocalDateTime.now()
            ),
            Review(
                message = "Very knowledgeable and helpful.",
                dateCreation = LocalDateTime.now().minusDays(3),
                createdBy = memberAnonymus,
                dateModification = LocalDateTime.now().minusDays(2)
            ),
            Review(
                message = "Margherita has consistently demonstrated a strong ability to work as a " +
                        "team player. Her attention to detail and commitment to excellence have " +
                        "been evident in every project she has undertaken. Her analytical skills " +
                        "are exceptional, and she often provides insights that are crucial to our " +
                        "decision-making process. Her ability to communicate complex data in a " +
                        "comprehensible way is highly commendable.",
                dateCreation = LocalDateTime.now().minusDays(1),
                createdBy = memberAnonymus,
                dateModification = LocalDateTime.now().minusDays(1)
            )*/
        ),
        privateEmail = true,
        privateGender = true,
        privatePhone = true,
        privateBirthdate = true,
        sound = false,
        vibration = false,
        language = Locale("en")
    )

    private val _memberAccessed = MutableStateFlow(_member)
    val memberAccessed: StateFlow<Member> = _memberAccessed.asStateFlow()


    fun setMemberAccessed(member: Member) {
        _memberAccessed.value = member
        setProfileMemberAccessed(member)
    }

    fun getmemberAccessed(): StateFlow<Member> { //Se rinomino con la m di Member in maiuscolo da problemi!
        return _memberAccessed.asStateFlow()
    }

    val member1 = Member(
        id = "B6VEtRlj8PfXP0pZXggD",
        photo = pictureProfile2,
        name = "Mario",
        surname = "Rossi",
        email = "mario.rossi@example.com",
        online = true,
        gender = Gender.Male,
        bio = "Experienced in business management with a passion for sports.",
        nickname = "SuperMario",
        password = "Mariopassword456!",
        address = "456, Milano, Street",
        birthdate = LocalDate.of(1985, 10, 25),
        phoneNumber = "+987654321",
        technicalSkills = listOf(
            "Task Management",
            "Communication Proficiency"
        ),
        languages = listOf(
            Pair(Locale.ITALIAN, 4),
            Pair(Locale.FRENCH, 3),
            Pair(Locale.CHINESE, 5),
        ),
        reviews = listOf(
            Review(
                message = "Great leader and motivator!",
                dateCreation = LocalDateTime.now().minusDays(10),
                createdBy = memberAccessed.value,
                dateModification = LocalDateTime.now().minusDays(10)
            ),
            Review(
                message = "Mario is an outstanding leader and motivator. His ability to manage " +
                        "tasks efficiently while keeping the team motivated is remarkable. " +
                        "He possesses excellent communication skills, which he uses to clearly " +
                        "convey his vision and objectives. Mario's strategic thinking and " +
                        "problem-solving abilities have significantly contributed to our " +
                        "team's success. He is always approachable and provides constructive " +
                        "feedback that helps everyone improve.",
                dateCreation = LocalDateTime.now(),
                createdBy = memberAccessed.value,
                dateModification = LocalDateTime.now()
            )
        ),
        privateEmail = true,
        privateGender = true,
        privatePhone = true,
        privateBirthdate = true,
        sound = false,
        vibration = false,
        language = Locale("en")
    )

    val member2 = Member(
        id = "tfOVVj8yLO9d8uVNhEMa",
        photo = null,
        name = "Paolo",
        surname = "Bianchi",
        email = "paolo.bianchi@example.com",
        online = false,
        gender = Gender.Male,
        bio = "Passionate about photography and technology.",
        nickname = "Pablo",
        password = "Paolopassword789!",
        address = "789, Napoli, Street",
        birthdate = LocalDate.of(1993, 3, 8),
        phoneNumber = "+1122334455",
        technicalSkills = listOf(
            "Data Analysis",
            "User Support",
            "Task Management",
            "Communication Proficiency"
        ),
        languages = listOf(
            Pair(Locale.GERMAN, 1),
            Pair(Locale.CHINESE, 4),
            Pair(Locale.KOREAN, 5),
        ),
        reviews = listOf(
            Review(
                message = "Always ready to help!",
                dateCreation = LocalDateTime.now().minusDays(30),
                createdBy = memberAnonymus,
                dateModification = LocalDateTime.now().minusDays(30)
            ),
            Review(
                message = "Very organized and efficient.",
                dateCreation = LocalDateTime.now().minusDays(4),
                createdBy = memberAnonymus,
                dateModification = LocalDateTime.now().minusDays(4)
            )
        ),
        privateEmail = true,
        privateGender = true,
        privatePhone = true,
        privateBirthdate = true,
        sound = false,
        vibration = false,
        language = Locale("en")
    )

    val member3 = Member(
        id = "sW2j92ykSZKn2Y6B7tDE",
        photo = null,
        name = "Federico",
        surname = "Neri",
        email = "federico.neri@example.com",
        online = false,
        gender = Gender.Male,
        bio = "Seeking new professional challenges in administration.",
        nickname = "Fed",
        password = "federicopassword321",
        address = "135, Firenze, Street",
        birthdate = LocalDate.of(1988, 7, 12),
        phoneNumber = "+3344556677",
        technicalSkills = listOf(
            "Collaborative Decision-Making",
            "Time Management"
        ),
        languages = listOf(
            Pair(Locale.ITALIAN, 2),
            Pair(Locale.GERMAN, 3),
            Pair(Locale.FRENCH, 4),
        ),
        reviews = listOf(
            Review(
                message = "Very organized and efficient.",
                dateCreation = LocalDateTime.now(),
                createdBy = memberAnonymus,
                dateModification = LocalDateTime.now()
            )
        ),
        privateEmail = true,
        privateGender = true,
        privatePhone = true,
        privateBirthdate = true,
        sound = false,
        vibration = false,
        language = Locale("en")
    )

    val member4 = Member(
        id = "",
        photo = pictureProfile1,
        name = "Maria",
        surname = "Verdi",
        email = "maria.verdi@example.com",
        online = true,
        gender = Gender.Female,
        bio = "I love the world of marketing and am always looking for new strategies.",
        nickname = "MarketingQueen",
        password = "Mariapassword135!",
        address = "246, Venezia, Street",
        birthdate = LocalDate.of(1980, 5, 20),
        phoneNumber = "+5566778899",
        technicalSkills = listOf(
            "Collaborative Decision-Making",
            "Time Management"
        ),
        languages = listOf(
            Pair(Locale.FRENCH, 2),
            Pair(Locale.CHINESE, 5),
            Pair(Locale.KOREAN, 4),
        ),
        reviews = listOf(
            Review(
                message = "Creative and strategic thinker.",
                dateCreation = LocalDateTime.now().minusDays(5),
                createdBy = memberAccessed.value,
                dateModification = LocalDateTime.now().minusDays(5)
            )
        ),
        privateEmail = true,
        privateGender = true,
        privatePhone = true,
        privateBirthdate = true,
        sound = false,
        vibration = false,
        language = Locale("en")
    )

    val member5 = Member(
        id = "tfOVVj8yLO9d8uVNhEMa",
        photo = pictureProfile1,
        name = "Laura",
        surname = "Bianco",
        email = "laura.bianco@example.com",
        online = false,
        gender = Gender.Female,
        bio = "Passionate about travel and cooking.",
        nickname = "Lau",
        password = "Laurapassword246!",
        address = "357, Genova, Street",
        birthdate = LocalDate.of(1995, 12, 3),
        phoneNumber = "+9988776655",
        technicalSkills = listOf(
            "Collaborative Decision-Making",
            "Time Management"
        ),
        languages = listOf(
            Pair(Locale.ITALIAN, 4),
            Pair(Locale.FRENCH, 3),
            Pair(Locale.KOREAN, 5),
        ),
        reviews = listOf(
            Review(
                message = "Detail-oriented and reliable.",
                dateCreation = LocalDateTime.now(),
                createdBy = memberAnonymus,
                dateModification = LocalDateTime.now()
            )
        ),
        privateEmail = true,
        privateGender = true,
        privatePhone = true,
        privateBirthdate = true,
        sound = false,
        vibration = false,
        language = Locale("en")
    )

    val member6 = Member(
        id = "tfOVVj8yLO9d8uVNhEMa",
        photo = null,
        name = "Giuseppe",
        surname = "Gialli",
        email = "giuseppe.gialli@example.com",
        online = true,
        gender = Gender.Male,
        bio = "Sales expert with a passion for soccer.",
        nickname = "Peppe",
        password = "Giuseppecalcio10!",
        address = "468, Torino, Street",
        birthdate = LocalDate.of(1982, 9, 18),
        phoneNumber = "+1122334455",
        technicalSkills = listOf(
            "Collaborative Decision-Making",
            "Time Management"
        ),
        languages = listOf(
            Pair(Locale.FRENCH, 2),
            Pair(Locale.CHINESE, 2),
            Pair(Locale.KOREAN, 5),
        ),
        reviews = listOf(
            Review(
                message = "Excellent in closing deals.",
                dateCreation = LocalDateTime.now(),
                createdBy = memberAnonymus,
                dateModification = LocalDateTime.now()
            )
        ),
        privateEmail = true,
        privateGender = true,
        privatePhone = true,
        privateBirthdate = true,
        sound = false,
        vibration = false,
        language = Locale("en")
    )

    val member7 = Member(
        id = "",
        photo = null,
        name = "Alessia",
        surname = "Rosa",
        email = "alessia.rosa@example.com",
        online = true,
        gender = Gender.Female,
        bio = "Passionate about programming and music.",
        nickname = "TechGirl",
        password = "Alessiapassword789!",
        address = "579, Bologna, Street",
        birthdate = LocalDate.of(1998, 2, 28),
        phoneNumber = "+3344556677",
        technicalSkills = listOf(
            "Collaborative Decision-Making",
            "User Support"
        ),
        languages = listOf(
            Pair(Locale.CHINESE, 5),
            Pair(Locale.KOREAN, 5),
        ),
        reviews = listOf(
            Review(
                message = "Innovative and tech-savvy.",
                dateCreation = LocalDateTime.now(),
                createdBy = memberAnonymus,
                dateModification = LocalDateTime.now()
            )
        ),
        privateEmail = true,
        privateGender = true,
        privatePhone = true,
        privateBirthdate = true,
        sound = false,
        vibration = false,
        language = Locale("en")
    )

    // Simulation of the members with ProfileFormViewModel
    private val _profileMemberAccessed =
        MutableStateFlow(ProfileFormViewModel(memberAccessed.value))
    val profileMemberAccessed: StateFlow<ProfileFormViewModel> =
        _profileMemberAccessed.asStateFlow()

    fun setProfileMemberAccessed(member: Member) {
        _profileMemberAccessed.value = ProfileFormViewModel(member)
    }

    val profileMember1 = ProfileFormViewModel(member1)
    val profileMember2 = ProfileFormViewModel(member2)
    val profileMember3 = ProfileFormViewModel(member3)
    val profileMember4 = ProfileFormViewModel(member4)
    val profileMember5 = ProfileFormViewModel(member5)
    val profileMember6 = ProfileFormViewModel(member6)
    val profileMember7 = ProfileFormViewModel(member7)

    val allUsers = listOf(
        profileMemberAccessed.value,
        profileMember1,
        profileMember2,
        profileMember3,
        profileMember4,
        profileMember5,
        profileMember6,
        profileMember7,
    )

    val languageList = listOf(
        Locale.ITALIAN,
        Locale.ENGLISH,
        Locale.FRENCH,
        Locale.GERMAN
    )

    fun convertCategoryToString(value: CategoryEnum?): Int {
        if (value == null) {
            return R.string.select_category
        }
        return when (value) {
            CategoryEnum.MANAGEMENT -> R.string.management_category
            CategoryEnum.ADMINISTRATIVE -> R.string.administrative_category
            CategoryEnum.FINANCE -> R.string.finance_category
            CategoryEnum.MARKETING -> R.string.marketing_category
            CategoryEnum.HUMAN_RESOURCES -> R.string.human_resource_category
            CategoryEnum.IT_SUPPORT -> R.string.it_support_category
            CategoryEnum.LOGISTICS -> R.string.logistics_category
            CategoryEnum.CUSTOMER_SERVICE -> R.string.customer_service_category
        }
    }

    fun convertRoleToString(value: RoleEnum): Int {
        return when (value) {
            RoleEnum.NO_ROLE_ASSIGNED -> R.string.no_role_assigned
            RoleEnum.EXECUTIVE_LEADER -> R.string.executive_leader_role
            RoleEnum.ADMINISTRATIVE_SUPPORTER -> R.string.administrative_supporter_role
            RoleEnum.TECHNOLOGY_AND_IT_MANAGER -> R.string.technology_and_it_manager_role
            RoleEnum.SALES_AND_MARKETING_LEADER -> R.string.sales_and_marketing_leader_role
            RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER -> R.string.operational_and_technical_staffer_role
        }
    }

    fun convertRepeatToString(value: RepeatEnum): Int {
        return when (value) {
            RepeatEnum.NO_REPEAT -> R.string.no_repeat
            RepeatEnum.DAILY -> R.string.daily_repeat
            RepeatEnum.WEEKLY -> R.string.weekly_repeat
            RepeatEnum.MONTHLY -> R.string.monthly_repeat
            RepeatEnum.YEARLY -> R.string.yearly_repeat
        }
    }

    fun convertStatusToString(value: StatusEnum): Int {
        return when (value) {
            StatusEnum.PENDING -> R.string.pending_status
            StatusEnum.IN_PROGRESS -> R.string.in_progress_status
            StatusEnum.ON_HOLD -> R.string.on_hold_status
            StatusEnum.OVERDUE -> R.string.overdue_status
            StatusEnum.DONE -> R.string.done_status
        }
    }

    fun convertGenderToString(value: Gender): Int {
        return when (value) {
            Gender.Male -> R.string.male
            Gender.Female -> R.string.female
            Gender.NON_BINARY -> R.string.not_binary
            Gender.PREFER_NOT_TO_SAY -> R.string.prefer_not_to_say
        }
    }

    fun convertStringToStatus(value: Int): StatusEnum {
        return when (value) {
            R.string.pending_status -> StatusEnum.PENDING
            R.string.in_progress_status -> StatusEnum.IN_PROGRESS
            R.string.on_hold_status -> StatusEnum.ON_HOLD
            R.string.overdue_status -> StatusEnum.OVERDUE
            R.string.done_status -> StatusEnum.DONE
            else -> throw Exception("Cannot convert the string to StatusEnum option")
        }
    }

    fun convertContentTypeToString(content: ContentTypeEnum): String? {
        return when (content) {
            ContentTypeEnum.PDF -> content.value!!
            ContentTypeEnum.IMAGE_PNG -> content.value!!
            ContentTypeEnum.IMAGE_GIF -> content.value!!
            ContentTypeEnum.IMAGE_JPEG -> content.value!!
            ContentTypeEnum.TEXT -> content.value!!
            ContentTypeEnum.OTHER -> {
                if (content.value == null) {
                    return null
                } else {
                    return content.value
                }
            }
        }
    }

    fun convertFilterTeamByToString(value: FilterTeamBy): Int {
        return when (value) {
            FilterTeamBy.CATEGORY -> R.string.category
        }
    }

    fun convertTimePartecipationToString(value: TimePartecipationTeamTypes): Int {
        return when (value) {
            TimePartecipationTeamTypes.FULL_TIME -> R.string.full_time
            TimePartecipationTeamTypes.PART_TIME -> R.string.part_time
        }
    }

    fun convertToTimestampLocalDateTime(localDateTime: LocalDateTime): Timestamp {
        val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        return Timestamp(Date.from(instant))
    }

    fun convertToTimestampLocalDate(localDate: LocalDate): Timestamp {
        val instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        return Timestamp(Date.from(instant))
    }

    fun convertToTimestampLocalDateTime(date: LocalDate, time: LocalTime): Timestamp {
        val localDateTime = LocalDateTime.of(date, time)
        val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        return Timestamp(Date.from(instant))
    }

    fun convertTimestampToLocalDate(timestamp: Timestamp): LocalDate {
        val instant = timestamp.toDate().toInstant()
        return instant.atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun convertTimestampToLocalTime(timestamp: Timestamp): LocalTime {
        val instant = timestamp.toDate().toInstant()
        return instant.atZone(ZoneId.systemDefault()).toLocalTime()
    }

    fun convertTimestampToLocalDateTime(timestamp: Timestamp): LocalDateTime {
        val instant = timestamp.toDate().toInstant()
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun categoryTeamList(): List<Int> {
        return listOf(
            R.string.category_work,
            R.string.category_project,
            R.string.category_study,
            R.string.category_research,
            R.string.category_development,
            R.string.category_innovation,
            R.string.category_marketing,
            R.string.category_sales,
            R.string.category_support,
            R.string.category_training,
            R.string.category_creativity,
            R.string.category_analysis,
            R.string.category_events,
            R.string.category_administration,
            R.string.category_technology,
            R.string.category_production,
            R.string.category_quality,
            R.string.category_sustainability,
            R.string.category_leisure
        )
    }

    fun calculateRanking(
        members: List<MemberInfoTeam>,
        tasksParam: List<TaskViewModel>
    ): List<Pair<MemberInfoTeam, Int>> {
        val rankingList = mutableMapOf<MemberInfoTeam, Int>()
        val tasks = tasksParam.filter { it.statusField.status == StatusEnum.DONE }

        members.forEach { member ->
            tasks.forEach { task ->
                if (task.creationField.createdBy.nickname == member.profile.nickname ||
                    task.delegateTasksField.members.contains(member)
                ) {
                    rankingList[member] = (rankingList[member] ?: 0) + 1
                }
            }
        }

        return rankingList.toList().sortedByDescending { it.second }
            .map { Pair(it.first, it.second) }
    }

    fun loadMediaPlayer(context: Context, raw: Int): MediaPlayer? {
        val mediaPlayerDialog: MediaPlayer? = MediaPlayer.create(context, raw)
        mediaPlayerDialog?.setOnCompletionListener {
            it.reset()
            it.release()
        }

        return mediaPlayerDialog
    }

    fun changeLanguage(context: Context, locale: Locale) {
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun getFlagResourceId(locale: Locale): Int {
        return when (locale) {
            Locale.ENGLISH -> R.drawable.ic_great_britain_flag
            Locale.ITALIAN -> R.drawable.ic_italy_flag
            Locale.FRENCH -> R.drawable.ic_french_flag
            Locale.GERMAN -> R.drawable.ic_german_flag
            else -> R.drawable.on_hold
        }
    }

    private val tagColors = listOf(
        R.color.cyan,
        R.color.light_green,
        R.color.light_yellow,
        R.color.orange,
        R.color.light_red,
        R.color.purple
    )

    fun getTagColor(index: Int): Int {
        // Returns the color corresponding to the provided index
        return tagColors[index % tagColors.size]
    }

    fun hasRequiredPermissions(applicationContext: Context): Boolean {
        return uploadFilePermissions.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun getContentTypeFromString(contentType: String?): ContentTypeEnum {
        if (contentType == null) {
            return ContentTypeEnum.OTHER
        }

        for (content in ContentTypeEnum.entries) {
            if (content.value == contentType) {
                return content
            }
        }

        return ContentTypeEnum.OTHER.apply { this.value = contentType }
    }

    fun getUniqueLocales(vm: ProfileFormViewModel): List<Locale> {
        val selectedLocales =
            vm.languageSkillsField.editValue.map { it.first.displayLanguage }.toList()
        val uniqueLocales =
            TreeSet<Locale> { o1, o2 -> o1.displayLanguage.compareTo(o2.displayLanguage) }
        uniqueLocales.addAll(Locale.getAvailableLocales()
            .filter { !selectedLocales.contains(it.displayLanguage) }
            .sortedBy { it.displayLanguage })
        return uniqueLocales.toList()
    }

    fun formatFileSize(bytes: Double): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> String.format("%.2f bytes", bytes)
        }
    }

    fun formatLocalDateTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm")
        return dateTime.format(formatter)
    }

    fun formatLocalDate(dateTime: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return dateTime.format(formatter)
    }

    val orderingMap: Map<String, Comparator<TaskViewModel>> = mapOf(
        "Select task ordering" to Comparator { _, _ -> 0 },
        "Ascending task name" to Comparator { t1, t2 -> t1.titleField.value.compareTo(t2.titleField.value) },
        "Descending task name" to Comparator { t1, t2 -> t2.titleField.value.compareTo(t1.titleField.value) },
        "Ascending creation date" to Comparator { t1, t2 ->
            t1.creationField.dateCreation.compareTo(
                t2.creationField.dateCreation
            )
        },
        "Descending creation date" to Comparator { t1, t2 ->
            t2.creationField.dateCreation.compareTo(
                t1.creationField.dateCreation
            )
        },
        "Ascending due date" to Comparator { t1, t2 ->
            val compareDate = t1.dueDateField.date.compareTo(t2.dueDateField.date)
            if (compareDate == 0) {
                t1.dueDateField.time.compareTo(t2.dueDateField.time)
            } else {
                compareDate
            }
        },
        "Descending due date" to Comparator { t1, t2 ->
            val compareDate = t2.dueDateField.date.compareTo(t1.dueDateField.date)
            if (compareDate == 0) {
                t2.dueDateField.time.compareTo(t1.dueDateField.time)
            } else {
                compareDate
            }
        },
        "Ascending delegates number" to Comparator { t1, t2 ->
            t1.delegateTasksField.members.size.compareTo(
                t2.delegateTasksField.members.size
            )
        },
        "Descending delegates number" to Comparator { t1, t2 ->
            t2.delegateTasksField.members.size.compareTo(
                t1.delegateTasksField.members.size
            )
        }
    )

    val filteringMap: Map<String, (TaskViewModel) -> Boolean> = mapOf(
        "Select task filter" to { true },
        "Late tasks" to { t ->
            (t.dueDateField.date.atTime(t.dueDateField.time)).isBefore(
                LocalDateTime.now()
            ) && t.statusField.status !== StatusEnum.DONE
        },
        "My assigned tasks" to { t ->
            t.delegateTasksField.members.any { (m, _) -> m.id == memberAccessed.value.id }
        },
        "My created tasks" to { t -> t.creationField.createdBy.id == memberAccessed.value.id },
        "Start date" to { true },
        "Due date" to { true },
        "${member1.name} ${member1.surname}" to { t ->
            t.delegateTasksField.members.any { (m, _) -> m.name == member1.name && m.surname == member1.surname }
        },
        "${member2.name} ${member2.surname}" to { t ->
            t.delegateTasksField.members.any { (m, _) -> m.name == member2.name && m.surname == member2.surname }
        },
        "${member3.name} ${member3.surname}" to { t ->
            t.delegateTasksField.members.any { (m, _) -> m.name == member3.name && m.surname == member3.surname }
        },
        "${member4.name} ${member4.surname}" to { t ->
            t.delegateTasksField.members.any { (m, _) -> m.name == member4.name && m.surname == member4.surname }
        },
        "${member5.name} ${member5.surname}" to { t ->
            t.delegateTasksField.members.any { (m, _) -> m.name == member5.name && m.surname == member5.surname }
        },
        "${member6.name} ${member6.surname}" to { t ->
            t.delegateTasksField.members.any { (m, _) -> m.name == member6.name && m.surname == member6.surname }
        },
        "${member7.name} ${member7.surname}" to { t ->
            t.delegateTasksField.members.any { (m, _) -> m.name == member7.name && m.surname == member7.surname }
        },
        "${memberAccessed.value.name} ${memberAccessed.value.surname}" to { t ->
            t.delegateTasksField.members.any { (m, _) -> m.name == memberAccessed.value.name && m.surname == memberAccessed.value.surname }
        }
    )

    fun generateUniqueId(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSnnnnnnnnn")
        val formattedDateTime = currentDateTime.format(formatter)

        // Combinazione della data e dell'ora in una stringa
        val combinedString = formattedDateTime

        // Calcola l'hash della stringa combinata
        val hash = abs(combinedString.hashCode())

        // Seleziona le ultime 8 cifre dell'hash come ID univoco numerico
        val numericId = hash % 100000000 // 100000000 è 10^8
        return numericId.toString()
    }

    fun getTaskOrderStringId(orderKey: String): Int? {
        return when (orderKey) {
            "Select task ordering" -> R.string.select_task_ordering
            "Ascending task name" -> R.string.ascending_task_name
            "Descending task name" -> R.string.descending_task_name
            "Ascending creation date" -> R.string.ascending_creation_date
            "Descending creation date" -> R.string.descending_creation_date
            "Ascending due date" -> R.string.ascending_due_date
            "Descending due date" -> R.string.descending_due_date
            "Ascending delegates number" -> R.string.ascending_delegates_number
            "Descending delegates number" -> R.string.descending_delegates_number
            else -> null
        }
    }

    fun getTaskFilterStringId(filterKey: String): Int? {
        return when (filterKey) {
            "Select task filter" -> R.string.select_task_filter
            "Late tasks" -> R.string.late_tasks
            "My assigned tasks" -> R.string.my_assigned_tasks
            "My created tasks" -> R.string.my_created_tasks
            "Start date" -> R.string.start_date
            "Due date" -> R.string.due_date
            else -> null
        }
    }

    fun useSafeMediaPlayer(mediaPlayer: MediaPlayer?) {
        try {
            if (profileMemberAccessed.value.accountSettings.sound) {
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            // nothing
        }
    }

    fun playSoundEffectWithSoundCheck(view: View, click: Int) {
        try {
            if (profileMemberAccessed.value.accountSettings.sound) {
                view.playSoundEffect(click)
            }
        } catch (e: Exception) {
            // nothing
        }
    }

    fun vibrate(context: Context, milliseconds: Long = 500) {
        if (profileMemberAccessed.value.accountSettings.vibration) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    milliseconds,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
    }

    fun vibrateTickEffect(context: Context, milliseconds: Long = 100, times: Int = 2) {
        if (profileMemberAccessed.value.accountSettings.vibration) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val amplitudes = mutableListOf<Int>()
            for (i in 0 until times * 2) {
                amplitudes.add(if (i % 2 == 0) 1 else 0) // Alternating between 1 and 0
            }
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    LongArray(amplitudes.size) { milliseconds },
                    amplitudes.toIntArray(),
                    -1 // Play once
                )
            )
        }
    }


    fun copyToClipboard(teamId: String): String {
        return "https://www.synchrotask.com/join/team/${teamId}"
    }

    /*fun generateQrCode(teamId: String): Bitmap? {
        return try {
            val url = copyToClipboard(teamId)
            val bitMatrix: BitMatrix =
                BarcodeEncoder().encode(url, BarcodeFormat.QR_CODE, 400, 400)
            BarcodeEncoder().createBitmap(bitMatrix)
        } catch (e: WriterException) {
            Log.e("QrCode", "Error generating QR code", e)
            null
        }
    }*/

    fun generateQrCode(teamId: String, expiryDate: Date): Bitmap? {
        return try {
            val urlLink = copyToClipboard(teamId)
            val url = "${urlLink}?expiry=${formatIsoStringDate(expiryDate)}"
            val bitMatrix: BitMatrix =
                BarcodeEncoder().encode(url, BarcodeFormat.QR_CODE, 400, 400)
            BarcodeEncoder().createBitmap(bitMatrix)
        } catch (e: WriterException) {
            Log.e("QrCode", "Error generating QR code", e)
            null
        }
    }

    private fun formatIsoStringDate(date: Date): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.time = date
        return String.format(
            "%04d-%02d-%02dT%02d:%02d:%02dZ",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND)
        )
    }

    fun parseIsoStringDate(dateString: String): Date? {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.parse(dateString)
        } catch (e: ParseException) {
            Log.e("DateParsing", "Error parsing date", e)
            null
        }
    }

    fun isExpired(expiryDate: Date): Boolean {
        return Date().after(expiryDate)
    }


    enum class CategoryEnum {
        MANAGEMENT,
        ADMINISTRATIVE,
        FINANCE,
        MARKETING,
        HUMAN_RESOURCES,
        IT_SUPPORT,
        LOGISTICS,
        CUSTOMER_SERVICE
    }

    enum class RepeatEnum {
        NO_REPEAT,
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
    }

    enum class StatusEnum {
        PENDING,
        IN_PROGRESS,
        ON_HOLD,
        OVERDUE,
        DONE
    }

    enum class ContentTypeEnum(var value: String?) {
        PDF("application/pdf"),
        IMAGE_PNG("image/png"),
        IMAGE_GIF("image/gif"),
        IMAGE_JPEG("image/jpeg"),
        TEXT("text/plain"),
        OTHER("")
    }

    enum class Gender {
        Male,
        Female,
        NON_BINARY,
        PREFER_NOT_TO_SAY
    }


    enum class FilterTeamBy {
        CATEGORY,

    }

    enum class TimePartecipationTeamTypes {
        FULL_TIME,
        PART_TIME
    }

    enum class TypeOfPage {
        TEAMS,
        CHAT
    }

    enum class RoutesEnum {
        HOME_TEAM,
        ADD_TEAM,
        TEAM_PAGE,
        MODIFY_TEAM,
        TEAM_QRCODE_SHARE_PAGE,
        TEAM_CONFIRM_JOIN,
        TEAM_REQUESTS,
        ROLE_ASSIGNATION_REQUEST_MEMBER,
        ROLE_ASSIGNATION_REQUEST_TEAM,
        TEAM_STATISTICS,
        LEAVE_TEAM,
        DELETE_TEAM,
        DELETE_TEAM_MEMBER,

        HOME_CHAT,
        CHAT_PAGE,
        NEW_CHAT_PAGE,

        VIEW_TASK,
        MODIFY_TASK,
        CREATE_TASK,

        PROFILE,
        MEMBER_PROFILE,
        ACCOUNT_SETTINGS,

        ABOUTUS,

        LOGIN,
        LOGOUT,
        REGISTER
    }

    enum class CollectionsEnum {
        tasks,
        teams,
        users,
        memberInfoTeam,
        comments
    }
}