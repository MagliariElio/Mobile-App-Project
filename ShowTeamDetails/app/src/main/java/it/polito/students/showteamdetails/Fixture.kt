package it.polito.students.showteamdetails

import android.net.Uri
import it.polito.students.showteamdetails.Utils.generateUniqueId
import it.polito.students.showteamdetails.Utils.member1
import it.polito.students.showteamdetails.Utils.member3
import it.polito.students.showteamdetails.Utils.member4
import it.polito.students.showteamdetails.Utils.member5
import it.polito.students.showteamdetails.Utils.member6
import it.polito.students.showteamdetails.entity.Comment
import it.polito.students.showteamdetails.entity.CreatedInfo
import it.polito.students.showteamdetails.entity.File
import it.polito.students.showteamdetails.entity.History
import it.polito.students.showteamdetails.entity.Link
import it.polito.students.showteamdetails.entity.MemberInfoTeam
import it.polito.students.showteamdetails.entity.RoleEnum
import it.polito.students.showteamdetails.entity.Task
import it.polito.students.showteamdetails.entity.Team
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Fixture {

    object RouterActionsProvider {
        lateinit var routerActions: RouterActions

        fun initialize(routerActions: RouterActions) {
            this.routerActions = routerActions
        }

        fun provideRouterActions(): RouterActions {
            if (!this::routerActions.isInitialized) {
                throw IllegalStateException("RouterActions is not initialized")
            }
            return routerActions
        }
    }

    companion object {
        /** FAKE DATA **/
        /* Version number and year for app copyright */
        const val NUMBER_VERSION = "2.1.2"
        val year = LocalDate.now().year


        /* Fake files */
        val file1 = File(
            name = "Document.pdf",
            size = "1 KB",
            contentType = Utils.ContentTypeEnum.PDF,
            uploadedBy = member1,
            dateUpload = LocalDateTime.now().minusDays(1),
            uri = Uri.EMPTY
        )
        private val file2 = File(
            name = "Image.jpg",
            size = "512.0 bytes",
            contentType = Utils.ContentTypeEnum.IMAGE_JPEG,
            uploadedBy = member3,
            dateUpload = LocalDateTime.now().minusDays(1),
            uri = Uri.EMPTY
        )
        private val file3 = File(
            name = "Code.java",
            size = "1 GB",
            contentType = Utils.ContentTypeEnum.TEXT,
            uploadedBy = member5,
            dateUpload = LocalDateTime.now().minusDays(3),
            uri = Uri.EMPTY
        )
        val file4 = File(
            name = "Image gif",
            size = "304.0 bytes",
            contentType = Utils.ContentTypeEnum.IMAGE_GIF,
            uploadedBy = Utils.member2,
            dateUpload = LocalDateTime.now().minusDays(4),
            uri = Uri.EMPTY
        )

        private val file5 = File(
            name = "Image jpeg",
            size = "729.0 bytes",
            contentType = Utils.ContentTypeEnum.IMAGE_JPEG,
            uploadedBy = member5,
            dateUpload = LocalDateTime.now().minusDays(4),
            uri = Uri.EMPTY
        )

        private val file6 = File(
            name = "Image gif",
            size = "328.0 bytes",
            contentType = Utils.ContentTypeEnum.IMAGE_GIF,
            uploadedBy = member6,
            dateUpload = LocalDateTime.now().minusDays(4),
            uri = Uri.EMPTY
        )

        /* Fake links */
        private val link1 = Link(
            link = "www.link1.com",
            uploadedBy = member1,
            dateUpload = LocalDateTime.now().minusDays(1),
        )
        private val link2 = Link(
            link = "www.link2.com",
            uploadedBy = Utils.member2,
            dateUpload = LocalDateTime.now().minusDays(2),
        )
        val link3 = Link(
            link = "www.link3.com",
            uploadedBy = member3,
            dateUpload = LocalDateTime.now().minusDays(1),
        )
        private val link4 = Link(
            link = "www.link4.com",
            uploadedBy = member4,
            dateUpload = LocalDateTime.now().minusDays(1),
        )
        private val link5 = Link(
            link = "www.link5.com",
            uploadedBy = member5,
            dateUpload = LocalDateTime.now().minusDays(1),
        )
        private val link6 = Link(
            link = "www.link6.com",
            uploadedBy = member6,
            dateUpload = LocalDateTime.now().minusDays(1),
        )

        /* Fake comments */
        val comment1 = Comment(
            comment = "Developed robust communication strategies.",
            createdBy = member1,
            dateCreation = LocalDateTime.now().minusDays(1),
            dateModification = LocalDateTime.now().plusDays(1)
        )
        val comment2 = Comment(
            comment = "Successfully allocated responsibilities among team members and established " +
                    "realistic timelines and milestones.",
            createdBy = Utils.member2,
            dateCreation = LocalDateTime.now().minusDays(7),
            dateModification = LocalDateTime.now().minusDays(6),
        )
        private val comment3 = Comment(
            comment = "Developed comprehensive communication strategies to facilitate effective collaboration.",
            createdBy = member3,
            dateCreation = LocalDateTime.now().minusDays(3),
            dateModification = LocalDateTime.now().minusDays(2)
        )
        private val comment4 = Comment(
            comment = "Assigned responsibilities among team members and established clear timelines " +
                    "and milestones. Reviewed budgetary considerations and finalized project scope.",
            createdBy = member4,
            dateCreation = LocalDateTime.now().minusDays(3),
            dateModification = LocalDateTime.now().minusDays(3)
        )
        private val comment5 = Comment(
            comment = "I have outlined communication strategies to ensure effective " +
                    "coordination among team members. Overall, the task is now well-prepared" +
                    " for execution",
            createdBy = member5,
            dateCreation = LocalDateTime.now().minusDays(3),
            dateModification = LocalDateTime.now().minusDays(2)
        )
        private val comment6 = Comment(
            comment = "Finalized budget considerations and defined project scope to ensure project success.",
            createdBy = member6,
            dateCreation = LocalDateTime.now().minusDays(6),
            dateModification = LocalDateTime.now().minusDays(5)
        )
        private val comment7 = Comment(
            comment = "Completed the review of the project launch meeting description.",
            createdBy = Utils.memberAccessed.value,
            dateCreation = LocalDateTime.now().minusDays(6),
            dateModification = LocalDateTime.now().minusDays(5)
        )

        /* Fake history regarded fake data inserted */
        private val historyCA = History(
            member = Utils.memberAccessed.value,
            date = LocalDateTime.now().minusDays(10),
            value = MessagesPage.CREATED_TASK
        )
        private val historyC1 = History(
            member = member1,
            date = LocalDateTime.now().minusDays(11),
            value = MessagesPage.CREATED_TASK
        )
        private val historyC2 = History(
            member = Utils.member2,
            date = LocalDateTime.now().minusDays(12),
            value = MessagesPage.CREATED_TASK
        )
        private val historyC3 = History(
            member = member3,
            date = LocalDateTime.now().minusDays(13),
            value = MessagesPage.CREATED_TASK
        )
        private val historyC4 = History(
            member = member4,
            date = LocalDateTime.now().minusDays(14),
            value = MessagesPage.CREATED_TASK
        )
        private val historyC5 = History(
            member = member3,
            date = LocalDateTime.now().minusDays(15),
            value = MessagesPage.CREATED_TASK
        )
        private val historyC6 = History(
            member = member6,
            date = LocalDateTime.now().minusDays(16),
            value = MessagesPage.CREATED_TASK
        )
        private val historyC7 = History(
            member = Utils.member7,
            date = LocalDateTime.now().minusDays(17),
            value = MessagesPage.CREATED_TASK
        )
        private val historyAC1 = History(
            member = member1,
            date = LocalDateTime.now().minusDays(1),
            value = MessagesPage.ADDED_COMMENT
        )
        private val historyAC2 = History(
            member = Utils.member2,
            date = LocalDateTime.now().minusDays(7),
            value = MessagesPage.ADDED_COMMENT,
            delegatedMember = MemberInfoTeam(
                id = "VSD15jFmmZ5ceF4BhAST",
                profile = Utils.member3,
                role = RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                participationType = Utils.TimePartecipationTeamTypes.FULL_TIME
            )
        )
        private val historyAC3 = History(
            member = member1,
            date = LocalDateTime.now().minusDays(3),
            value = MessagesPage.ADDED_COMMENT
        )
        private val historyAC4 = History(
            member = member3,
            date = LocalDateTime.now().minusDays(3),
            value = MessagesPage.ADDED_COMMENT
        )
        private val historyAC5 = History(
            member = Utils.member2,
            date = LocalDateTime.now().minusDays(3),
            value = MessagesPage.ADDED_COMMENT
        )
        private val historyAC6 = History(
            member = member1,
            date = LocalDateTime.now().minusDays(6),
            value = MessagesPage.ADDED_COMMENT
        )
        private val historyAC7 = History(
            member = Utils.memberAccessed.value,
            date = LocalDateTime.now().minusDays(6),
            value = MessagesPage.ADDED_COMMENT
        )
        private val historyF1 = History(
            member = member1,
            date = LocalDateTime.now().minusDays(1),
            value = MessagesPage.UPLOADED_FILE
        )
        private val historyF2 = History(
            member = Utils.member2,
            date = LocalDateTime.now().minusDays(1),
            value = MessagesPage.UPLOADED_FILE
        )
        private val historyF3 = History(
            member = member3,
            date = LocalDateTime.now().minusDays(3),
            value = MessagesPage.UPLOADED_FILE,
        )
        private val historyF4 = History(
            member = member4,
            date = LocalDateTime.now().minusDays(4),
            value = MessagesPage.UPLOADED_FILE
        )
        private val historyF5 = History(
            member = member5,
            date = LocalDateTime.now().minusDays(4),
            value = MessagesPage.UPLOADED_FILE
        )
        private val historyF6 = History(
            member = member6,
            date = LocalDateTime.now().minusDays(4),
            value = MessagesPage.UPLOADED_FILE
        )
        private val historyF7 = History(
            member = Utils.member7,
            date = LocalDateTime.now().minusDays(4),
            value = MessagesPage.UPLOADED_FILE
        )
        private val historyL1 = History(
            member = member1,
            date = LocalDateTime.now().minusDays(1),
            value = MessagesPage.UPLOADED_LINK
        )
        private val historyL2 = History(
            member = Utils.member2,
            date = LocalDateTime.now().minusDays(2),
            value = MessagesPage.UPLOADED_LINK
        )
        private val historyL3 = History(
            member = member3,
            date = LocalDateTime.now().minusDays(1),
            value = MessagesPage.UPLOADED_LINK
        )
        private val historyL4 = History(
            member = member4,
            date = LocalDateTime.now().minusDays(1),
            value = MessagesPage.UPLOADED_LINK
        )
        private val historyL5 = History(
            member = member5,
            date = LocalDateTime.now().minusDays(2),
            value = MessagesPage.UPLOADED_LINK
        )
        private val historyL6 = History(
            member = member6,
            date = LocalDateTime.now().minusDays(1),
            value = MessagesPage.UPLOADED_LINK
        )

        /* Fake tasks */
        val task1Entity = Task(
            id = generateUniqueId(),
            groupID = generateUniqueId(),
            title = "Project Kickoff Meeting",
            status = Utils.StatusEnum.PENDING,
            startDate = LocalDate.now().plusDays(2),
            startTime = LocalTime.of(8, 30),
            dueDate = LocalDate.now().plusDays(4),
            dueTime = LocalTime.of(23, 59),
            repeat = Utils.RepeatEnum.NO_REPEAT,
            repeatEndDate = LocalDate.of(2024, 9, 17),
            description = "This is a description.",
            category = Utils.CategoryEnum.ADMINISTRATIVE,
            tagList = listOf("Business", "Meetings", "Project Management"),
            fileList = listOf(file1, file2, file3),
            linkList = listOf(link1, link2),
            commentList = listOf(comment1, comment2, comment3, comment4, comment5),
            historyList = listOf(historyAC2),
            /*historyCA,
            historyF3, historyF1, historyF3,
            historyL1, historyL3,
            historyAC1, historyAC2, historyAC3, historyAC4, historyAC5, historyAC7
        ),*/
            delegateList = listOf(
                MemberInfoTeam(
                    id = "VSD15jFmmZ5ceF4BhAST",
                    profile = member3,
                    role = RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                    participationType = Utils.TimePartecipationTeamTypes.FULL_TIME
                ),
                MemberInfoTeam(
                    id = "7LV14E6KzmcaUYqxOILP",
                    profile = member3,
                    role = RoleEnum.ADMINISTRATIVE_SUPPORTER,
                    participationType = Utils.TimePartecipationTeamTypes.FULL_TIME
                ),
                MemberInfoTeam(
                    id = "pXsBq5cqlGRIvRzYDWbz",
                    profile = member3,
                    role = RoleEnum.EXECUTIVE_LEADER,
                    participationType = Utils.TimePartecipationTeamTypes.FULL_TIME
                ),
            ),
            created = CreatedInfo(Utils.memberAccessed.value, LocalDateTime.now()),
        )

        private val task1 = TaskViewModel(
            Task(
                id = generateUniqueId(),
                groupID = generateUniqueId(),
                title = "Project Kickoff Meeting",
                status = Utils.StatusEnum.PENDING,
                startDate = LocalDate.now().plusDays(2),
                startTime = LocalTime.of(8, 30),
                dueDate = LocalDate.now().plusDays(4),
                dueTime = LocalTime.of(23, 59),
                repeat = Utils.RepeatEnum.NO_REPEAT,
                repeatEndDate = LocalDate.of(2024, 9, 17),
                description = "Meeting to officially launch the project, " +
                        "discuss initial plans and objectives, allocate responsibilities " +
                        "among team members, establish timelines and milestones, " +
                        "and address any questions or concerns from stakeholders. " +
                        "Additionally, review budgetary considerations, finalize the " +
                        "project scope, and outline communication strategies.",
                category = Utils.CategoryEnum.ADMINISTRATIVE,
                tagList = listOf("Business", "Meetings", "Project Management"),
                fileList = listOf(file1, file2, file3),
                linkList = listOf(link1, link2),
                commentList = listOf(comment1, comment2, comment3, comment4, comment5),
                historyList = listOf(
                    historyCA,
                    historyF3, historyF1, historyF3,
                    historyL1, historyL3,
                    historyAC1, historyAC2, historyAC3, historyAC4, historyAC5, historyAC7
                ),
                delegateList = listOf(
                    MemberInfoTeam(
                        profile = member3,
                        role = RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                        participationType = Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
                    MemberInfoTeam(
                        member3,
                        RoleEnum.ADMINISTRATIVE_SUPPORTER,
                        Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
                    MemberInfoTeam(
                        member3,
                        RoleEnum.EXECUTIVE_LEADER,
                        Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
                ),
                created = CreatedInfo(Utils.memberAccessed.value, LocalDateTime.now())
            ),
            routerActions = RouterActionsProvider.provideRouterActions()
        )
        private val task2 = TaskViewModel(
            Task(
                id = generateUniqueId(),
                groupID = generateUniqueId(),
                title = "Financial report review",
                status = Utils.StatusEnum.PENDING,
                startDate = LocalDate.now().plusDays(3),
                startTime = LocalTime.of(9, 0),
                dueDate = LocalDate.now().plusDays(98),
                dueTime = LocalTime.of(12, 0),
                repeat = Utils.RepeatEnum.NO_REPEAT,
                repeatEndDate = LocalDate.of(2026, 9, 17),
                description = "Reviewing quarterly financial reports and preparing feedback " +
                        "entails carefully examining detailed financial data and performance " +
                        "metrics over a specific period, such as a quarter. This process involves " +
                        "analyzing various financial statements, including income statements, balance " +
                        "sheets, and cash flow statements, to assess the company's financial health and " +
                        "performance. Additionally, it requires identifying trends, anomalies, and areas " +
                        "for improvement based on the analysis. The feedback prepared typically includes " +
                        "recommendations for strategic adjustments, cost-saving measures, revenue " +
                        "enhancement strategies, and risk mitigation techniques. By providing insightful " +
                        "feedback, this process supports informed decision-making and helps steer the " +
                        "organization towards its financial goals and objectives.",
                category = Utils.CategoryEnum.FINANCE,
                tagList = listOf("Business", "Finance", "Report"),
                fileList = listOf(file2),
                linkList = listOf(link3),
                commentList = listOf(comment3, comment1),
                historyList = listOf(historyC3, historyF2, historyL3, historyAC1, historyAC3),
                delegateList = listOf(
                    MemberInfoTeam(
                        member3,
                        RoleEnum.ADMINISTRATIVE_SUPPORTER,
                        Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
                    MemberInfoTeam(
                        member3,
                        RoleEnum.EXECUTIVE_LEADER,
                        Utils.TimePartecipationTeamTypes.PART_TIME
                    ),
                    MemberInfoTeam(
                        member3,
                        RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                        Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
                    MemberInfoTeam(
                        member3,
                        RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                        Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
                ),
                created = CreatedInfo(Utils.member2, LocalDateTime.now().minusDays(2))
            ),
            routerActions = RouterActionsProvider.provideRouterActions()
        )
        private val task3 = TaskViewModel(
            Task(
                id = generateUniqueId(),
                groupID = generateUniqueId(),
                title = "Meeting with clients",
                status = Utils.StatusEnum.IN_PROGRESS,
                startDate = LocalDate.now().minusDays(1),
                startTime = LocalTime.of(10, 0),
                dueDate = LocalDate.now().plusDays(23),
                dueTime = LocalTime.of(14, 30),
                repeat = Utils.RepeatEnum.NO_REPEAT,
                repeatEndDate = LocalDate.now().plusDays(2), //Same date of start date if no repeat
                description = "Discussing project requirements and deliverables with clients involves " +
                        "engaging in comprehensive conversations to ascertain their needs, preferences, " +
                        "and objectives. This process entails actively listening to clients' ideas, " +
                        "concerns, and expectations, while also offering professional insights and " +
                        "guidance where necessary. By fostering open communication and collaboration, " +
                        "this interaction ensures a clear understanding of the project scope, timeline, " +
                        "and desired outcomes. It serves as a crucial initial step in establishing a strong " +
                        "client-provider relationship built on trust, transparency, and mutual understanding.",
                category = Utils.CategoryEnum.LOGISTICS,
                tagList = listOf("Business", "Meetings", "Client"),
                fileList = listOf(file2, file3),
                linkList = listOf(link1),
                commentList = listOf(comment1, comment2, comment3, comment4, comment6),
                historyList = listOf(
                    historyC1,
                    historyF1, historyF2,
                    historyL1,
                    historyAC1, historyAC2, historyAC3, historyAC4, historyAC6
                ),
                delegateList = listOf(
//                MemberInfoTeam(
//                    Utils.profileMember5,
//                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
//                    Utils.TimePartecipationTeamTypes.FULL_TIME
//                ),
                    MemberInfoTeam(
                        member3,
                        RoleEnum.TECHNOLOGY_AND_IT_MANAGER,
                        Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
                    MemberInfoTeam(
                        member3,
                        RoleEnum.SALES_AND_MARKETING_LEADER,
                        Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
//                MemberInfoTeam(
//                    Utils.profileMember4,
//                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
//                    Utils.TimePartecipationTeamTypes.FULL_TIME
//                ),
                ),
                created = CreatedInfo(member1, LocalDateTime.now().minusDays(1))
            ),
            routerActions = RouterActionsProvider.provideRouterActions()
        )
        private val task4 = TaskViewModel(
            Task(
                id = generateUniqueId(),
                groupID = generateUniqueId(),
                title = "Product development planning",
                status = Utils.StatusEnum.ON_HOLD,
                startDate = LocalDate.now().plusDays(1),
                startTime = LocalTime.of(15, 0),
                dueDate = LocalDate.now().plusDays(2),
                dueTime = LocalTime.of(17, 0),
                repeat = Utils.RepeatEnum.NO_REPEAT,
                repeatEndDate = LocalDate.now().plusDays(1),
                description = "Plan development sprints and prioritize feature implementation.",
                category = Utils.CategoryEnum.IT_SUPPORT,
                tagList = listOf("Business", "Development", "Planning"),
                fileList = listOf(file1, file4),
                linkList = listOf(link2),
                commentList = listOf(),
                historyList = listOf(historyC3, historyF1, historyF4, historyL2),
                delegateList = listOf(
                    MemberInfoTeam(
                        member3,
                        RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                        Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
                    MemberInfoTeam(
                        member3,
                        RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                        Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
//                MemberInfoTeam(
//                    Utils.profileMember7,
//                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
//                    Utils.TimePartecipationTeamTypes.FULL_TIME
//                ),
//                MemberInfoTeam(
//                    Utils.profileMember4,
//                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
//                    Utils.TimePartecipationTeamTypes.FULL_TIME
//                ),
                ),
                created = CreatedInfo(member3, LocalDateTime.now().minusDays(3))
            ),
            routerActions = RouterActionsProvider.provideRouterActions()
        )

        private
        val task5 = TaskViewModel(
            Task(
                id = generateUniqueId(),
                groupID = generateUniqueId(),
                title = "Marketing strategy brainstorming",
                status = Utils.StatusEnum.OVERDUE,
                startDate = LocalDate.now().minusDays(3),
                startTime = LocalTime.of(13, 0),
                dueDate = LocalDate.now().minusDays(2),
                dueTime = LocalTime.of(15, 30),
                repeat = Utils.RepeatEnum.NO_REPEAT,
                repeatEndDate = LocalDate.now().plusDays(5),
                description = "Brainstorm ideas for upcoming marketing campaigns.",
                category = Utils.CategoryEnum.MARKETING,
                tagList = listOf("Business", "Marketing", "Brainstorm"),
                fileList = listOf(),
                linkList = listOf(),
                commentList = listOf(comment3, comment5),
                historyList = listOf(historyC4, historyAC3, historyAC5),
                delegateList = listOf(
                    MemberInfoTeam(
                        member3,
                        RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                        Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
                    MemberInfoTeam(
                        member3,
                        RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                        Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
                    MemberInfoTeam(
                        member3,
                        RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                        Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
//                MemberInfoTeam(
//                    Utils.profileMember4, RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
//                    Utils.TimePartecipationTeamTypes.FULL_TIME
//                ),
                    MemberInfoTeam(
                        member3,
                        RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                        Utils.TimePartecipationTeamTypes.FULL_TIME
                    ),
                ),
                created = CreatedInfo(member4, LocalDateTime.now().minusDays(4))
            ),
            routerActions = RouterActionsProvider.provideRouterActions()
        )
        /*
                private
                val task6 = TaskViewModel(
                    Task(
                        id = generateUniqueId(),
                        groupID = generateUniqueId(),
                        title = "Research on industry trends",
                        status = Utils.StatusEnum.DONE,
                        startDate = LocalDate.now().minusDays(2),
                        startTime = LocalTime.of(11, 0),
                        dueDate = LocalDate.now().plusDays(3),
                        dueTime = LocalTime.of(14, 0),
                        repeat = Utils.RepeatEnum.NO_REPEAT,
                        repeatEndDate = LocalDate.now().plusDays(2),
                        description = "Conduct research on emerging trends in the industry.",
                        category = Utils.CategoryEnum.CUSTOMER_SERVICE,
                        tagList = listOf("Business", "Research", "Trends"),
                        fileList = listOf(),
                        linkList = listOf(),
                        commentList = listOf(),
                        historyList = listOf(historyC1),
                        delegateList = listOf(
                            MemberInfoTeam(
                                Utils.profileMember1,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember2,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember3,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
        //                MemberInfoTeam(
        //                    Utils.profileMember4, RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
        //                    Utils.TimePartecipationTeamTypes.FULL_TIME
        //                ),
                        ),
                        created = CreatedInfo(
                            member1,
                            LocalDateTime.now().minusDays(5)
                        ),
                    ),
                    routerActions = RouterActionsProvider.provideRouterActions()
                )

                private val task7 = TaskViewModel(
                    Task(
                        id = generateUniqueId(),
                        groupID = generateUniqueId(),
                        title = "Design mockup review",
                        status = Utils.StatusEnum.ON_HOLD,
                        startDate = LocalDate.now().plusDays(4),
                        startTime = LocalTime.of(10, 30),
                        dueDate = LocalDate.now().plusDays(15),
                        dueTime = LocalTime.of(13, 0),
                        repeat = Utils.RepeatEnum.NO_REPEAT,
                        repeatEndDate = LocalDate.now().plusDays(4),
                        description = "Review and provide feedback on design mockups for new features.",
                        category = Utils.CategoryEnum.MANAGEMENT,
                        tagList = listOf(
                            "Business",
                            "Design",
                            "Review"
                        ),
                        fileList = listOf(),
                        linkList = listOf(link1),
                        commentList = listOf(comment3),
                        historyList = listOf(
                            historyC1,
                            historyL1,
                            historyAC3
                        ),
                        delegateList = listOf(
                            MemberInfoTeam(
                                Utils.profileMember1,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
        //                MemberInfoTeam(
        //                    Utils.profileMember6, RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
        //                    Utils.TimePartecipationTeamTypes.FULL_TIME
        //                ),
                            MemberInfoTeam(
                                Utils.profileMember3,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
        //                MemberInfoTeam(
        //                    Utils.profileMember4, RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
        //                    Utils.TimePartecipationTeamTypes.FULL_TIME
        //                ),
                            MemberInfoTeam(
                                Utils.profileMemberAccessed.value,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                        ),
                        created = CreatedInfo(
                            member1,
                            LocalDateTime.now().minusDays(6)
                        )
                    ),
                    routerActions = RouterActionsProvider.provideRouterActions()
                )
                private val task8 = TaskViewModel(
                    Task(
                        id = generateUniqueId(),
                        groupID = generateUniqueId(),
                        title = "Team training session",
                        status = Utils.StatusEnum.DONE,
                        startDate = LocalDate.now()
                            .plusDays(3),
                        startTime = LocalTime.of(9, 0),
                        dueDate = LocalDate.now()
                            .plusDays(10),
                        dueTime = LocalTime.of(12, 0),
                        repeat = Utils.RepeatEnum.NO_REPEAT,
                        repeatEndDate = LocalDate.now()
                            .plusDays(3),
                        description = "Organize a training session for team members on new tools and technologies.",
                        category = Utils.CategoryEnum.HUMAN_RESOURCES,
                        tagList = listOf(
                            "Business",
                            "Training",
                            "Team"
                        ),
                        fileList = listOf(),
                        linkList = listOf(),
                        commentList = listOf(),
                        historyList = listOf(historyC3),
                        delegateList = listOf(
                            MemberInfoTeam(
                                Utils.profileMember1,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember2,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
        //                MemberInfoTeam(
        //                    Utils.profileMember5, RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
        //                    Utils.TimePartecipationTeamTypes.FULL_TIME
        //                ),
        //                MemberInfoTeam(
        //                    Utils.profileMember4, RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
        //                    Utils.TimePartecipationTeamTypes.FULL_TIME
        //                ),
                        ),
                        created = CreatedInfo(
                            member3,
                            LocalDateTime.now().minusDays(7)
                        ),
                    ),
                    routerActions = RouterActionsProvider.provideRouterActions()
                )
                private val task9 = TaskViewModel(
                    Task(
                        id = generateUniqueId(),
                        groupID = generateUniqueId(),
                        title = "Develop a Marketing Strategy for New Product Launch",
                        status = Utils.StatusEnum.DONE,
                        startDate = LocalDate.now()
                            .plusDays(5),
                        startTime = LocalTime.of(
                            10,
                            0
                        ),
                        dueDate = LocalDate.now()
                            .plusDays(6),
                        dueTime = LocalTime.of(
                            23,
                            59
                        ),
                        repeat = Utils.RepeatEnum.NO_REPEAT,
                        repeatEndDate = LocalDate.of(
                            2024,
                            9,
                            17
                        ),
                        description = "Create a comprehensive marketing strategy for the upcoming launch of our new product. " +
                                "This strategy should include market research, target audience identification, " +
                                "competitive analysis, promotional tactics, and a detailed timeline. " +
                                "The final deliverable should be a presentation summarizing " +
                                "the strategy along with actionable steps and KPIs to measure success.",
                        category = Utils.CategoryEnum.MARKETING,
                        tagList = listOf(
                            "Brainstorm",
                            "Meetings",
                            "Project Management"
                        ),
                        fileList = listOf(file1),
                        linkList = listOf(link1),
                        commentList = listOf(comment1, comment2),
                        historyList = listOf(
                            historyC1,
                            historyF1,
                            historyL1,
                            historyAC1,
                            historyAC2,
                        ),
                        delegateList = listOf(
                            MemberInfoTeam(
                                Utils.profileMemberAccessed.value,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),

                            ),
                        created = CreatedInfo(
                            member1,
                            LocalDateTime.now()
                        ),
                    ),
                    routerActions = RouterActionsProvider.provideRouterActions()
                )

                private
                val task10 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Design a Social Media Campaign for Product Awareness",
                            status = Utils.StatusEnum.IN_PROGRESS,
                            startDate = LocalDate.now()
                                .plusDays(
                                    5
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    6
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Develop a social media campaign aimed at increasing awareness of our new product. " +
                                    "The campaign should include a content calendar, creative assets, key messaging, " +
                                    "and engagement strategies for platforms such as Facebook, Instagram, " +
                                    "Twitter, and LinkedIn. Ensure the campaign aligns with our overall " +
                                    "marketing strategy and brand voice. The deliverable should be a detailed plan with scheduled posts," +
                                    " ad formats, budget estimates, and metrics for tracking performance.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Research",
                                "Trends",
                                "Design"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1
                            ),
                            commentList = listOf(comment2),
                            historyList = listOf(
                                historyC1,
                                historyF1,
                                historyL1,
                                historyAC2,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember2,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member1,
                                LocalDateTime.now()
                            ),
                        ), routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task11 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Conduct Customer Feedback Survey and Analysis",
                            status = Utils.StatusEnum.IN_PROGRESS,
                            startDate = LocalDate.now()
                                .plusDays(
                                    5
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    6
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Design and implement a customer feedback survey to gather insights on our " +
                                    "new product from a sample of our target audience. " +
                                    "This task involves creating the survey questionnaire, " +
                                    "distributing it through appropriate channels, " +
                                    "and collecting responses. After data collection, analyze the results to identify trends, " +
                                    "areas of improvement, and customer satisfaction levels. " +
                                    "The final deliverable should include a comprehensive report " +
                                    "detailing the survey findings, key insights, " +
                                    "and actionable recommendations for product and marketing improvements.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Report",
                                "Research"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1
                            ),
                            commentList = listOf(comment1, comment2, comment3),
                            historyList = listOf(
                                historyC2,
                                historyF1,
                                historyL1,
                                historyAC1,
                                historyAC2,
                                historyAC3
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember2,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember3,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                Utils.member2,
                                LocalDateTime.now()
                            ),
                        ), routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task12 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Develop Influencer Partnership Program",
                            status = Utils.StatusEnum.ON_HOLD,
                            startDate = LocalDate.now()
                                .plusDays(
                                    5
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    6
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Create a strategic plan to engage with influencers for promoting our new product." +
                                    " This task involves identifying potential influencers in our industry, " +
                                    "developing outreach materials, and crafting collaboration proposals. " +
                                    "Define the criteria for selecting influencers, outline the benefits for both parties, " +
                                    "and set clear objectives for each partnership. " +
                                    "The final deliverable should include a list of targeted influencers, proposed partnership ideas, " +
                                    "communication templates, and a timeline for executing the program. Additionally, establish metrics to evaluate " +
                                    "the success of these partnerships in terms of reach, engagement, and conversion rates.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Meetings",
                                "Project Management"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1
                            ),
                            commentList = listOf(comment1, comment2),
                            historyList = listOf(
                                historyCA,
                                historyF1,
                                historyL1,
                                historyAC1,
                                historyAC2,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember2,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                Utils.memberAccessed.value,
                                LocalDateTime.now()
                            ),
                        ), routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task13 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Implement User Authentication System",
                            status = Utils.StatusEnum.ON_HOLD,
                            startDate = LocalDate.now()
                                .plusDays(
                                    2
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    6
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Develop a secure user authentication system for our web application. " +
                                    "This includes designing and implementing features such as user registration, " +
                                    "login, password recovery, and multi-factor authentication. " +
                                    "Ensure the system adheres to best practices for security and privacy. " +
                                    "The final deliverable should be a fully functional authentication system, " +
                                    "along with documentation and automated tests to verify its security and performance.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Design"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1
                            ),
                            commentList = listOf(comment1, comment2),
                            historyList = listOf(
                                historyC3,
                                historyF1,
                                historyL1,
                                historyAC1,
                                historyAC2,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember2,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member3,
                                LocalDateTime.now()
                            ),
                        ), routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task14 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Optimize Database Performance",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .plusDays(
                                    2
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    9
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Analyze the current database setup and identify areas for performance improvement. " +
                                    "This task involves examining query performance, indexing strategies, and database configuration. " +
                                    "Implement optimizations to reduce query execution time and improve overall database efficiency. " +
                                    "The final deliverable should include a detailed report on the changes made, before-and-after performance metrics, " +
                                    "and recommendations for maintaining optimal performance.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Maintenance"
                            ),
                            fileList = listOf(
                                file3
                            ),
                            linkList = listOf(
                                link5
                            ),
                            commentList = listOf(comment1, comment3),
                            historyList = listOf(
                                historyC2,
                                historyF3,
                                historyL5,
                                historyAC1,
                                historyAC3,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember3,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                Utils.member2,
                                LocalDateTime.now()
                            ),
                        ), routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task15 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Develop API Documentation",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .plusDays(
                                    3
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    8
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Create comprehensive and clear documentation for our existing APIs. " +
                                    "This task involves detailing each API endpoint, including request and response formats, " +
                                    "authentication methods, and usage examples. Ensure the documentation is accessible and " +
                                    "easy to understand for developers. The final deliverable should be a complete set of API " +
                                    "documentation hosted on a platform such as Swagger or Postman, " +
                                    "along with guidelines for updating the documentation as APIs evolve.",
                            category = Utils.CategoryEnum.IT_SUPPORT,
                            tagList = listOf(
                                "Development",
                                "Design",
                                "Maintenance"
                            ),
                            fileList = listOf(
                                file5
                            ),
                            linkList = listOf(
                                link3
                            ),
                            commentList = listOf(comment1, comment4),
                            historyList = listOf(
                                historyC1,
                                historyF5,
                                historyL3,
                                historyAC1,
                                historyAC4,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member1,
                                LocalDateTime.now()
                            ),
                        ), routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task16 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Implement Continuous Integration/Continuous Deployment (CI/CD) Pipeline",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .plusDays(
                                    5
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    6
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Set up a CI/CD pipeline to automate the testing and deployment of our application. " +
                                    "This includes configuring tools such as Jenkins, Travis CI, or GitLab CI, " +
                                    "integrating automated tests, and establishing deployment workflows for different environments " +
                                    "(development, staging, production). The final deliverable should be a fully operational CI/CD " +
                                    "pipeline that ensures smooth, reliable, and rapid application updates, " +
                                    "along with documentation for maintaining the pipeline.",
                            category = Utils.CategoryEnum.IT_SUPPORT,
                            tagList = listOf(
                                "Development"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1,
                                link3
                            ),
                            commentList = listOf(comment1, comment2, comment7),
                            historyList = listOf(
                                historyC2,
                                historyF1,
                                historyL1,
                                historyL3,
                                historyAC1,
                                historyAC2,
                                historyAC7
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember2,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember7,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                Utils.member2,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task17 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Conduct Code Review and Refactoring",
                            status = Utils.StatusEnum.ON_HOLD,
                            startDate = LocalDate.now()
                                .plusDays(
                                    9
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    16
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Perform a thorough code review of the existing codebase to identify areas " +
                                    "that require improvement. This task includes refactoring code to enhance readability, " +
                                    "maintainability, and performance. Address any technical debt and ensure " +
                                    "the code adheres to coding standards and best practices. The final deliverable should be a clean, " +
                                    "optimized codebase with detailed notes on changes made, along with recommendations " +
                                    "for future code reviews and refactoring efforts.",
                            category = Utils.CategoryEnum.IT_SUPPORT,
                            tagList = listOf(
                                "Maintenance",
                                "Development"
                            ),
                            fileList = listOf(
                                file2,
                                file5
                            ),
                            linkList = listOf(
                                link1,
                                link6
                            ),
                            commentList = listOf(comment1, comment2, comment4),
                            historyList = listOf(
                                historyC4,
                                historyF1,
                                historyF5,
                                historyL1,
                                historyL6,
                                historyAC1,
                                historyAC2,
                                historyAC4
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember2,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member4,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task18 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Organize Professional Development Workshops",
                            status = Utils.StatusEnum.IN_PROGRESS,
                            startDate = LocalDate.now()
                                .minusDays(
                                    2
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    9
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Plan and execute a series of professional development workshops aimed at enhancing employees' " +
                                    "skills and career growth. This task involves identifying relevant topics, " +
                                    "sourcing qualified trainers, and scheduling the workshops. " +
                                    "Ensure the workshops address both technical skills and soft skills. " +
                                    "The final deliverable should be a calendar of workshops, along with registration details, " +
                                    "materials for each session, and a feedback mechanism to assess the effectiveness of the training programs.",
                            category = Utils.CategoryEnum.IT_SUPPORT,
                            tagList = listOf(
                                "Planning",
                                "Project Management"
                            ),
                            fileList = listOf(
                                file6
                            ),
                            linkList = listOf(
                                link4
                            ),
                            commentList = listOf(comment4, comment7),
                            historyList = listOf(
                                historyC1,
                                historyF6,
                                historyL4,
                                historyAC4,
                                historyAC7,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember7,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member1,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task19 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Create a Series of Blog Posts on Industry Trends",
                            status = Utils.StatusEnum.DONE,
                            startDate = LocalDate.now()
                                .minusDays(
                                    1
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    1
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Research and write a series of blog posts that explore current trends in our industry. " +
                                    "This task involves identifying relevant topics, conducting thorough research, " +
                                    "and writing engaging and informative articles. Each blog post should be optimized " +
                                    "for SEO and include visuals where appropriate. The final deliverable should be a collection " +
                                    "of at least five blog posts, ready for publication, " +
                                    "along with social media snippets to promote each post.",
                            category = Utils.CategoryEnum.IT_SUPPORT,
                            tagList = listOf(
                                "Meetings",
                                "Brainstorm",
                                "Research",
                                "Trends"
                            ),
                            fileList = listOf(
                                file3
                            ),
                            linkList = listOf(
                                link3
                            ),
                            commentList = listOf(comment1, comment2, comment4),
                            historyList = listOf(
                                historyC4,
                                historyF3,
                                historyL3,
                                historyAC1,
                                historyAC2,
                                historyAC4
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember2,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember3,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member4,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task20 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Develop an E-Book for Lead Generation",
                            status = Utils.StatusEnum.OVERDUE,
                            startDate = LocalDate.now()
                                .minusDays(
                                    5
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .minusDays(
                                    1
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Create a comprehensive e-book to serve as a lead generation tool. " +
                                    "This task involves selecting a relevant topic that addresses common challenges " +
                                    "faced by our target audience, conducting in-depth research, and writing high-quality content. " +
                                    "The e-book should include visuals, case studies, and actionable insights. " +
                                    "The final deliverable should be a professionally designed e-book, formatted for digital distribution, " +
                                    "along with a promotional plan to attract and capture leads.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Design"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1
                            ),
                            commentList = listOf(comment1, comment7),
                            historyList = listOf(
                                historyC7,
                                historyF1,
                                historyL1,
                                historyAC1,
                                historyAC7,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember7,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                Utils.member7,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task21 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Project Timeline and Milestone Plan",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .minusDays(
                                    1
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    6
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Create a detailed project timeline and milestone plan for the upcoming project. " +
                                    "This task involves identifying key phases of the project, " +
                                    "defining milestones, and establishing deadlines. " +
                                    "Ensure that the timeline aligns with project goals and resource availability. " +
                                    "The final deliverable should be a comprehensive Gantt chart or similar timeline visualization, " +
                                    "along with a milestone document outlining critical deliverables and their due dates.",
                            category = Utils.CategoryEnum.MANAGEMENT,
                            tagList = listOf(
                                "Project Management",
                                "Planning"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1
                            ),
                            commentList = listOf(comment1),
                            historyList = listOf(
                                historyC1,
                                historyF1,
                                historyL1,
                                historyAC1,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                )
                            ),
                            created = CreatedInfo(
                                member1,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task22 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Conduct Risk Assessment and Mitigation Planning",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .plusDays(
                                    5
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    6
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Perform a thorough risk assessment for the current project, " +
                                    "identifying potential risks and their impact. " +
                                    "Develop mitigation strategies for each identified risk. " +
                                    "This task involves organizing brainstorming sessions, documenting risks, " +
                                    "and creating a risk matrix. The final deliverable should be a risk management " +
                                    "plan that includes a risk register, mitigation strategies, " +
                                    "and contingency plans to ensure the project stays on track despite potential setbacks",
                            category = Utils.CategoryEnum.MANAGEMENT,
                            tagList = listOf(
                                "Finance",
                                "Business"
                            ),
                            fileList = listOf(
                                file3
                            ),
                            linkList = listOf(
                                link1
                            ),
                            commentList = listOf(comment1, comment3, comment7),
                            historyList = listOf(
                                historyC2,
                                historyF3,
                                historyL1,
                                historyAC1,
                                historyAC2,
                                historyAC7
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember3,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember7,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                )
                            ),
                            created = CreatedInfo(
                                Utils.member2,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task23 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Coordinate Stakeholder Communication Plan",
                            status = Utils.StatusEnum.ON_HOLD,
                            startDate = LocalDate.now()
                                .plusDays(
                                    2
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    9
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Develop and implement a communication plan to keep all stakeholders " +
                                    "informed and engaged throughout the project lifecycle. " +
                                    "This task includes identifying key stakeholders, determining their information needs, " +
                                    "and establishing regular communication channels and schedules. " +
                                    "The final deliverable should be a stakeholder communication plan that includes meeting schedules, " +
                                    "reporting formats, and communication protocols to ensure transparency and alignment.",
                            category = Utils.CategoryEnum.MANAGEMENT,
                            tagList = listOf(
                                "Project Management"
                            ),
                            fileList = listOf(
                                file3
                            ),
                            linkList = listOf(
                                link3
                            ),
                            commentList = listOf(comment3, comment4),
                            historyList = listOf(
                                historyCA,
                                historyF3,
                                historyL3,
                                historyAC3,
                                historyAC4,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMemberAccessed.value,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember3,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember7,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                )
                            ),
                            created = CreatedInfo(
                                Utils.memberAccessed.value,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task24 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Facilitate Project Kickoff Meeting",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .plusDays(
                                    15
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    26
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Organize and lead a project kickoff meeting to align the project team " +
                                    "and stakeholders on objectives, roles, and responsibilities. " +
                                    "This task involves preparing an agenda, creating presentation materials, " +
                                    "and facilitating discussions to ensure all parties understand the project scope and expectations. " +
                                    "The final deliverable should be a detailed kickoff meeting report that includes meeting minutes, " +
                                    "action items, and any agreements made during the session.",
                            category = Utils.CategoryEnum.MANAGEMENT,
                            tagList = listOf(
                                "Meetings",
                                "Project Management"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1
                            ),
                            commentList = listOf(comment1, comment2),
                            historyList = listOf(
                                historyC3,
                                historyF1,
                                historyL1,
                                historyAC1,
                                historyAC2,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember2,
                                    RoleEnum.ADMINISTRATIVE_SUPPORTER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                )
                            ),
                            created = CreatedInfo(
                                member3,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task25 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Implement Project Management Software and Training",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .minusDays(
                                    1
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    15
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Select and implement a project management software tool to streamline " +
                                    "project tracking and collaboration. This task involves researching suitable tools, " +
                                    "configuring the software to meet project needs, and training the team on how to use it effectively. " +
                                    "The final deliverable should be a fully operational project management software system, " +
                                    "along with training materials and user guides to ensure " +
                                    "all team members can efficiently use the tool for project management activities.",
                            category = Utils.CategoryEnum.MANAGEMENT,
                            tagList = listOf(
                                "Meetings",
                                "Project Management",
                                "Development"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1,
                                link5
                            ),
                            commentList = listOf(comment2, comment4),
                            historyList = listOf(
                                historyC1,
                                historyF1,
                                historyL1,
                                historyL5,
                                historyAC2,
                                historyAC4,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember2,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember5,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember7,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                )
                            ),
                            created = CreatedInfo(
                                member1,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task26 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Develop Employee Onboarding Program",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .plusDays(
                                    1
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    38
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Create a comprehensive onboarding program for new hires " +
                                    "to ensure a smooth integration into the company. " +
                                    "This task involves designing orientation sessions, creating training materials," +
                                    " and developing a mentorship system. The final deliverable should be a detailed " +
                                    "onboarding plan that includes a schedule, welcome packet, training modules, " +
                                    "and a feedback mechanism to continuously improve the program.",
                            category = Utils.CategoryEnum.HUMAN_RESOURCES,
                            tagList = listOf(
                                "Planning"
                            ),
                            fileList = listOf(
                                file3
                            ),
                            linkList = listOf(
                                link3
                            ),
                            commentList = listOf(comment3),
                            historyList = listOf(
                                historyCA,
                                historyF3,
                                historyL3,
                                historyAC3,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember3,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMemberAccessed.value,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                )
                            ),
                            created = CreatedInfo(
                                Utils.memberAccessed.value,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task27 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Conduct Employee Satisfaction Survey",
                            status = Utils.StatusEnum.OVERDUE,
                            startDate = LocalDate.now()
                                .minusDays(
                                    3
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .minusDays(
                                    1
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Design and administer an employee satisfaction survey to gauge overall " +
                                    "job satisfaction and identify areas for improvement. " +
                                    "This task includes developing survey questions, " +
                                    "distributing the survey, and analyzing the results. " +
                                    "The final deliverable should be a comprehensive report that summarizes the findings, " +
                                    "highlights key areas of concern, " +
                                    "and provides actionable recommendations to enhance employee satisfaction.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Research",
                                "Trends"
                            ),
                            fileList = listOf(
                                file2
                            ),
                            linkList = listOf(
                                link2
                            ),
                            commentList = listOf(comment2, comment7),
                            historyList = listOf(
                                historyC7,
                                historyF2,
                                historyL2,
                                historyAC2,
                                historyAC7,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember2,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember7,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                )
                            ),
                            created = CreatedInfo(
                                Utils.member7,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task28 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Revise and Update Employee Handbook",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .plusDays(
                                    5
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    6
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Review and update the employee handbook to ensure it reflects current company policies, " +
                                    "procedures, and legal requirements. This task involves collaborating with various departments " +
                                    "to gather the latest information, editing content for clarity and consistency, " +
                                    "and ensuring compliance with labor laws. The final deliverable should be an updated employee handbook, " +
                                    "ready for distribution, along with a summary of major changes communicated to all employees.",
                            category = Utils.CategoryEnum.HUMAN_RESOURCES,
                            tagList = listOf(
                                "Design"
                            ),
                            fileList = listOf(
                                file3
                            ),
                            linkList = listOf(),
                            commentList = listOf(comment3, comment5),
                            historyList = listOf(
                                historyC5,
                                historyF3,
                                historyAC3,
                                historyAC5,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember3,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember5,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember6,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMemberAccessed.value,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                )
                            ),
                            created = CreatedInfo(
                                member5,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task29 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Create Diversity and Inclusion Strategy",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .plusDays(
                                    2
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    12
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Create a strategic plan to enhance diversity and inclusion within the company. " +
                                    "This task involves conducting a current state assessment, " +
                                    "setting diversity goals, and developing initiatives to foster an inclusive workplace culture. " +
                                    "The final deliverable should be a diversity and inclusion strategy " +
                                    "document that includes specific actions, timelines, and metrics to measure progress.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Project Management"
                            ),
                            fileList = listOf(
                                file3,
                                file5
                            ),
                            linkList = listOf(
                                link3
                            ),
                            commentList = listOf(comment3, comment5, comment6),
                            historyList = listOf(
                                historyCA,
                                historyF3,
                                historyF5,
                                historyL3,
                                historyAC3,
                                historyAC5,
                                historyAC6,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember3,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember5,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember6,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMemberAccessed.value,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                )
                            ),
                            created = CreatedInfo(
                                Utils.memberAccessed.value,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task30 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Implement Performance Management System",
                            status = Utils.StatusEnum.IN_PROGRESS,
                            startDate = LocalDate.now()
                                .plusDays(
                                    0
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    19
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Design and implement a performance management system to track and evaluate " +
                                    "employee performance. This task involves selecting appropriate performance metrics, " +
                                    "developing evaluation forms, and setting up a process for regular performance reviews. " +
                                    "The final deliverable should be a comprehensive performance management system " +
                                    "that includes guidelines for managers, performance review schedules, " +
                                    "and tools for providing constructive feedback and setting development goals.",
                            category = Utils.CategoryEnum.MANAGEMENT,
                            tagList = listOf(
                                "Report"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1,
                                link5
                            ),
                            commentList = listOf(comment1, comment5),
                            historyList = listOf(
                                historyC5,
                                historyF1,
                                historyL1,
                                historyL5,
                                historyAC1,
                                historyAC5,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember5,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMemberAccessed.value,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                )
                            ),
                            created = CreatedInfo(
                                member5,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task31 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Develop Social Media Content Calendar",
                            status = Utils.StatusEnum.OVERDUE,
                            startDate = LocalDate.now()
                                .minusDays(
                                    2
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .minusDays(
                                    1
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Create a monthly content calendar for all social media platforms. " +
                                    "This task involves scheduling posts, creating engaging content, and aligning with marketing campaigns. " +
                                    "Deliver a detailed calendar with post dates, content ideas, and relevant hashtags.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Research",
                                "Trends"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link5
                            ),
                            commentList = listOf(comment1, comment5),
                            historyList = listOf(
                                historyC3,
                                historyF1,
                                historyL5,
                                historyAC1,
                                historyAC5,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember5,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member3,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task32 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = " Conduct Market Research for New Product",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .minusDays(
                                    1
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    6
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Research market trends and consumer preferences for the upcoming product launch. " +
                                    "This task includes surveys, focus groups, and competitive analysis. " +
                                    "Provide a report summarizing findings and strategic recommendations.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Research",
                                "Trends"
                            ),
                            fileList = listOf(
                                file1,
                                file6
                            ),
                            linkList = listOf(
                                link3
                            ),
                            commentList = listOf(comment1, comment3, comment6),
                            historyList = listOf(
                                historyC6,
                                historyF1,
                                historyF6,
                                historyL3,
                                historyAC1,
                                historyAC2,
                                historyAC6,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember3,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember6,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMemberAccessed.value,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                )
                            ),
                            created = CreatedInfo(
                                member6,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task33 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Implement Feature X in the Mobile App",
                            status = Utils.StatusEnum.DONE,
                            startDate = LocalDate.now()
                                .minusDays(
                                    2
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    0
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Develop and integrate Feature X into the existing mobile app. " +
                                    "This task includes coding, testing, and debugging. " +
                                    "Deliver a fully functional feature that meets user requirements and passes all tests.",
                            category = Utils.CategoryEnum.IT_SUPPORT,
                            tagList = listOf(
                                "Development",
                                "Maintenance"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1
                            ),
                            commentList = listOf(comment1, comment6),
                            historyList = listOf(
                                historyCA,
                                historyF1,
                                historyL1,
                                historyAC1,
                                historyAC6,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember6,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                Utils.memberAccessed.value,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task34 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Upgrade Legacy System to Latest Technology Stack",
                            status = Utils.StatusEnum.DONE,
                            startDate = LocalDate.now()
                                .minusDays(
                                    2
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    1
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Analyze the current legacy system and plan its upgrade to the latest technology stack. " +
                                    "This task includes assessing dependencies, coding, and testing. " +
                                    "Provide a deployment plan and ensure minimal downtime.",
                            category = Utils.CategoryEnum.LOGISTICS,
                            tagList = listOf(
                                "Project Management",
                                "Finance"
                            ),
                            fileList = listOf(
                                file3
                            ),
                            linkList = listOf(
                                link3
                            ),
                            commentList = listOf(comment3),
                            historyList = listOf(
                                historyC3,
                                historyF3,
                                historyL3,
                                historyAC3,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember3,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member3,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task35 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Write a Series of SEO-Optimized Articles",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .plusDays(
                                    1
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    23
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Create a series of five SEO-optimized articles on industry-related topics. " +
                                    "This task includes keyword research, writing, and editing. " +
                                    "Deliver articles ready for publication, complete with meta descriptions and keywords.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Brainstorm",
                                "Research"
                            ),
                            fileList = listOf(
                                file3,
                                file5
                            ),
                            linkList = listOf(
                                link5
                            ),
                            commentList = listOf(comment3, comment5),
                            historyList = listOf(
                                historyC1,
                                historyF3,
                                historyF5,
                                historyL5,
                                historyAC3,
                                historyAC5,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember3,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember5,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMemberAccessed.value,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                )
                            ),
                            created = CreatedInfo(
                                member1,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task36 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Develop a Monthly Newsletter",
                            status = Utils.StatusEnum.IN_PROGRESS,
                            startDate = LocalDate.now()
                                .plusDays(
                                    0
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    2
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Design and write content for the monthly company newsletter. " +
                                    "This task involves gathering news, writing articles, and designing the layout. " +
                                    "Deliver a newsletter ready for email distribution.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Brainstorm",
                                "Research",
                                "Design"
                            ),
                            fileList = listOf(
                                file5
                            ),
                            linkList = listOf(
                                link6
                            ),
                            commentList = listOf(comment5, comment6),
                            historyList = listOf(
                                historyC6,
                                historyF5,
                                historyL6,
                                historyAC5,
                                historyAC6,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember5,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember6,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMemberAccessed.value,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                )
                            ),
                            created = CreatedInfo(
                                member6,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task37 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Create a Project Charter for New Initiative",
                            status = Utils.StatusEnum.OVERDUE,
                            startDate = LocalDate.now()
                                .minusDays(
                                    4
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .minusDays(
                                    3
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Develop a project charter for the new initiative, outlining objectives, " +
                                    "scope, stakeholders, and deliverables. " +
                                    "Provide a comprehensive document that serves as a foundation for the project.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Brainstorm",
                                "Research"
                            ),
                            fileList = listOf(
                                file2
                            ),
                            linkList = listOf(
                                link2
                            ),
                            commentList = listOf(comment2),
                            historyList = listOf(
                                historyCA,
                                historyF2,
                                historyL2,
                                historyAC2,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember2,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                Utils.memberAccessed.value,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task38 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Conduct Project Post-Mortem Analysis",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .plusDays(
                                    8
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    10
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Analyze the recently completed project to identify successes and areas for improvement. " +
                                    "This task includes gathering team feedback and documenting lessons learned." +
                                    " Deliver a post-mortem report with actionable insights.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Research",
                                "Report"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link4
                            ),
                            commentList = listOf(comment1, comment4, comment7),
                            historyList = listOf(
                                historyC5,
                                historyF1,
                                historyL4,
                                historyAC1,
                                historyAC4,
                                historyAC7
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember7,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member5,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task39 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Develop Employee Recognition Program",
                            status = Utils.StatusEnum.IN_PROGRESS,
                            startDate = LocalDate.now()
                                .plusDays(
                                    0
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    8
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Create a program to recognize and reward outstanding employee performance. " +
                                    "This task includes designing criteria, selecting rewards, and planning implementation. " +
                                    "Deliver a complete program proposal with budget estimates.",
                            category = Utils.CategoryEnum.MANAGEMENT,
                            tagList = listOf(
                                "Project Management"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link5
                            ),
                            commentList = listOf(comment1, comment4),
                            historyList = listOf(
                                historyCA,
                                historyF1,
                                historyL5,
                                historyAC1,
                                historyAC4,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember5,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMemberAccessed.value,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                Utils.memberAccessed.value,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task40 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Conduct Annual Performance Reviews",
                            status = Utils.StatusEnum.ON_HOLD,
                            startDate = LocalDate.now()
                                .plusDays(
                                    14
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    64
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Plan and execute the annual performance review process. " +
                                    "This task includes scheduling reviews, preparing evaluation forms, and training managers. " +
                                    "Deliver a summary report on employee performance and development plans.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Report"
                            ),
                            fileList = listOf(
                                file4
                            ),
                            linkList = listOf(
                                link5
                            ),
                            commentList = listOf(comment4, comment5),
                            historyList = listOf(
                                historyC5,
                                historyF4,
                                historyL5,
                                historyAC4,
                                historyAC5,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMemberAccessed.value,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember5,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member5,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task41 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Implement Customer Feedback Loop",
                            status = Utils.StatusEnum.OVERDUE,
                            startDate = LocalDate.now()
                                .minusDays(
                                    1
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .minusDays(
                                    1
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Set up a system to collect and analyze customer feedback regularly. " +
                                    "This task involves designing feedback forms, creating a database, and establishing review processes. " +
                                    "Deliver a monthly feedback report with insights and recommendations.",
                            category = Utils.CategoryEnum.CUSTOMER_SERVICE,
                            tagList = listOf(
                                "Research",
                                "Trends"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1
                            ),
                            commentList = listOf(comment1, comment7),
                            historyList = listOf(
                                historyC4,
                                historyF1,
                                historyL1,
                                historyAC1,
                                historyAC7,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember7,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member4,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task42 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Create Knowledge Base Articles",
                            status = Utils.StatusEnum.ON_HOLD,
                            startDate = LocalDate.now()
                                .plusDays(
                                    15
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    41
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Write and organize knowledge base articles to help customers resolve common issues. " +
                                    "This task includes researching topics, writing clear instructions, " +
                                    "and formatting articles for the help center. " +
                                    "Deliver a set of comprehensive and accessible articles.",
                            category = Utils.CategoryEnum.ADMINISTRATIVE,
                            tagList = listOf(
                                "Disign"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1
                            ),
                            commentList = listOf(comment1, comment7),
                            historyList = listOf(
                                historyC7,
                                historyF1,
                                historyL1,
                                historyAC1,
                                historyAC7,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember7,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMemberAccessed.value,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                Utils.member7,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task43 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Develop Sales Training Program",
                            status = Utils.StatusEnum.IN_PROGRESS,
                            startDate = LocalDate.now()
                                .plusDays(
                                    15
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    32
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Design a training program to improve the sales team's skills and knowledge. " +
                                    "This task includes creating training materials, scheduling sessions, and evaluating outcomes." +
                                    " Deliver a detailed training plan and post-training assessment report.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Planning",
                                "Brainstorm",
                                "Research",
                                "Trends"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1
                            ),
                            commentList = listOf(comment1, comment5),
                            historyList = listOf(
                                historyC7,
                                historyF1,
                                historyL1,
                                historyAC1,
                                historyAC5,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember1,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember5,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                Utils.member7,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task44 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Analyze Sales Data to Identify Trends",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .plusDays(
                                    5
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    6
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Review and analyze recent sales data to identify trends and opportunities. " +
                                    "This task involves using analytics tools and preparing visual reports. " +
                                    "Deliver a detailed analysis with strategic recommendations.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Research",
                                "Trends"
                            ),
                            fileList = listOf(
                                file4
                            ),
                            linkList = listOf(
                                link6
                            ),
                            commentList = listOf(comment4, comment6),
                            historyList = listOf(
                                historyC4,
                                historyF4,
                                historyL6,
                                historyAC4,
                                historyAC6,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember6,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member4,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task45 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Prepare Quarterly Financial Report",
                            status = Utils.StatusEnum.DONE,
                            startDate = LocalDate.now()
                                .plusDays(
                                    0
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    1
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Compile and analyze financial data to prepare the quarterly financial report. " +
                                    "This task includes reviewing income statements, balance sheets, and cash flow statements. " +
                                    "Deliver a comprehensive report ready for presentation to stakeholders.",
                            category = Utils.CategoryEnum.FINANCE,
                            tagList = listOf(
                                "Report",
                                "Finance"
                            ),
                            fileList = listOf(
                                file6
                            ),
                            linkList = listOf(
                                link6
                            ),
                            commentList = listOf(comment4, comment6),
                            historyList = listOf(
                                historyCA,
                                historyF6,
                                historyL6,
                                historyAC4,
                                historyAC6,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember6,
                                    RoleEnum.ADMINISTRATIVE_SUPPORTER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                Utils.memberAccessed.value,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task46 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = " Develop Budget for Next Fiscal Year",
                            status = Utils.StatusEnum.IN_PROGRESS,
                            startDate = LocalDate.now()
                                .minusDays(
                                    3
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    6
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Create a detailed budget for the upcoming fiscal year. " +
                                    "This task involves forecasting revenues and expenses, allocating resources, " +
                                    "and preparing financial plans. Deliver a budget document with supporting justifications.",
                            category = Utils.CategoryEnum.FINANCE,
                            tagList = listOf(
                                "Finance"
                            ),
                            fileList = listOf(
                                file4
                            ),
                            linkList = listOf(
                                link4
                            ),
                            commentList = listOf(comment4, comment5),
                            historyList = listOf(
                                historyC4,
                                historyF4,
                                historyL4,
                                historyAC4,
                                historyAC5,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember5,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMemberAccessed.value,
                                    RoleEnum.ADMINISTRATIVE_SUPPORTER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member4,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task47 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Enhance Network Security Measures",
                            status = Utils.StatusEnum.IN_PROGRESS,
                            startDate = LocalDate.now()
                                .plusDays(
                                    0
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    12
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Review and upgrade the company’s network security protocols. " +
                                    "This task includes conducting a security audit, identifying vulnerabilities, " +
                                    "and implementing enhancements. Deliver a report on changes made and a plan for ongoing monitoring.",
                            category = Utils.CategoryEnum.LOGISTICS,
                            tagList = listOf(
                                "Design",
                                "Development",
                                "Maintenance"
                            ),
                            fileList = listOf(
                                file6
                            ),
                            linkList = listOf(
                                link6
                            ),
                            commentList = listOf(comment4, comment6),
                            historyList = listOf(
                                historyC4,
                                historyF6,
                                historyL6,
                                historyAC4,
                                historyAC6,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember6,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member4,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task48 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Implement Data Backup and Recovery Plan",
                            status = Utils.StatusEnum.DONE,
                            startDate = LocalDate.now()
                                .plusDays(
                                    0
                                ),
                            startTime = LocalTime.of(
                                6,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    0
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Design and implement a robust data backup and recovery plan. " +
                                    "This task includes selecting backup solutions, scheduling regular backups, " +
                                    "and testing recovery processes. Deliver a comprehensive plan with detailed procedures.",
                            category = Utils.CategoryEnum.IT_SUPPORT,
                            tagList = listOf(
                                "Maintenance"
                            ),
                            fileList = listOf(
                                file4
                            ),
                            linkList = listOf(
                                link4
                            ),
                            commentList = listOf(comment4, comment6),
                            historyList = listOf(
                                historyC6,
                                historyF4,
                                historyL4,
                                historyAC4,
                                historyAC6,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember6,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                member6,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task49 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Redesign Company Website",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .minusDays(
                                    3
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    7
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Plan and execute a complete redesign of the company website " +
                                    "to improve user experience and branding. " +
                                    "This task involves creating wireframes, designing new layouts, " +
                                    "and working with developers. Deliver a fully redesigned and functional website.",
                            category = Utils.CategoryEnum.IT_SUPPORT,
                            tagList = listOf(
                                "Planning",
                                "Brainstorm",
                                "Research",
                                "Trends",
                                "Design"
                            ),
                            fileList = listOf(
                                file6
                            ),
                            linkList = listOf(
                                link6
                            ),
                            commentList = listOf(comment4, comment6),
                            historyList = listOf(
                                historyCA,
                                historyF6,
                                historyL6,
                                historyAC4,
                                historyAC6,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember4,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMember6,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                Utils.memberAccessed.value,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )

                private
                val task50 =
                    TaskViewModel(
                        Task(
                            id = generateUniqueId(),
                            groupID = generateUniqueId(),
                            title = "Create Brand Style Guide",
                            status = Utils.StatusEnum.PENDING,
                            startDate = LocalDate.now()
                                .plusDays(
                                    5
                                ),
                            startTime = LocalTime.of(
                                10,
                                0
                            ),
                            dueDate = LocalDate.now()
                                .plusDays(
                                    6
                                ),
                            dueTime = LocalTime.of(
                                23,
                                59
                            ),
                            repeat = Utils.RepeatEnum.NO_REPEAT,
                            repeatEndDate = LocalDate.of(
                                2024,
                                9,
                                17
                            ),
                            description = "Develop a brand style guide to ensure consistent visual and messaging across all materials. " +
                                    "This task includes defining color schemes, typography, logos, and tone of voice. " +
                                    "Deliver a comprehensive guide ready for distribution to all departments.",
                            category = Utils.CategoryEnum.MARKETING,
                            tagList = listOf(
                                "Planning",
                                "Brainstorm"
                            ),
                            fileList = listOf(
                                file1
                            ),
                            linkList = listOf(
                                link1
                            ),
                            commentList = listOf(comment7),
                            historyList = listOf(
                                historyC7,
                                historyAC7,
                            ),
                            delegateList = listOf(
                                MemberInfoTeam(
                                    Utils.profileMember7,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                                MemberInfoTeam(
                                    Utils.profileMemberAccessed.value,
                                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                    Utils.TimePartecipationTeamTypes.FULL_TIME
                                ),
                            ),
                            created = CreatedInfo(
                                Utils.member7,
                                LocalDateTime.now()
                            ),
                        ),
                        routerActions = RouterActionsProvider.provideRouterActions()
                    )
        */
        /** Fake individual chats and messages **/


        /** Group chats **/
        /*
                val groupChat1 = ChatViewModel(
                    id = generateUniqueId(),
                    lastMessageDate = LocalDateTime.of(2024, 5, 20, 18, 27),
                    listOf(
                        MessageViewModel(
                            generateUniqueId(),
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum vel consequat est, et pharetra turpis. In leo lectus, placerat eu posuere at, euismod quis dolor. Aliquam dignissim nec purus non.",
                            LocalDateTime.of(2024, 5, 20, 8, 21),
                            Utils.profileMember2,
                            read = true
                        ),
                        MessageViewModel(
                            generateUniqueId(),
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer consequat, ex nec varius mattis, nibh orci eleifend nunc, vitae venenatis lectus elit efficitur mauris. Aenean lacus mauris, convallis in fringilla.",
                            LocalDateTime.of(2024, 5, 20, 16, 15),
                            Utils.profileMember1,
                            read = true
                        ),
                        MessageViewModel(
                            generateUniqueId(),
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer tincidunt nisi sed aliquet pulvinar. Mauris tempor justo id velit interdum tempus. Phasellus ac quam bibendum, dapibus lacus ut, efficitur purus.",
                            LocalDateTime.of(2024, 5, 20, 17, 59),
                            Utils.profileMemberAccessed.value,
                            read = true
                        ),
                        MessageViewModel(
                            generateUniqueId(),
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed pellentesque justo ut metus egestas finibus. Nam malesuada urna ac ipsum lobortis luctus. Nullam dictum rutrum est at finibus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Maecenas in nulla sit amet est varius mollis. Pellentesque.",
                            LocalDateTime.of(2024, 5, 20, 18, 10),
                            Utils.profileMember2,
                            read = true
                        ),
                        MessageViewModel(
                            generateUniqueId(),
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla gravida.",
                            LocalDateTime.of(2024, 5, 20, 18, 26),
                            Utils.profileMemberAccessed.value,
                            read = true
                        ),
                        MessageViewModel(
                            generateUniqueId(),
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent placerat lacus tellus, eget sagittis lorem.",
                            LocalDateTime.of(2024, 5, 20, 18, 27),
                            Utils.profileMemberAccessed.value,
                            read = true
                        )
                    ),
                    isTeamChat = true
                )

                val groupChat2 = ChatViewModel(
                    id = generateUniqueId(),
                    lastMessageDate = LocalDateTime.of(2024, 3, 10, 14, 7),
                    listOf(
                        MessageViewModel(
                            generateUniqueId(),
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer consequat, ex nec varius mattis, nibh orci eleifend nunc, vitae venenatis lectus elit efficitur mauris. Aenean lacus mauris, convallis in fringilla.",
                            LocalDateTime.of(2024, 3, 10, 13, 22),
                            Utils.profileMember1,
                            read = true
                        ),
                        MessageViewModel(
                            generateUniqueId(),
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer tincidunt nisi sed aliquet pulvinar. Mauris tempor justo id velit interdum tempus. Phasellus ac quam bibendum, dapibus lacus ut, efficitur purus.",
                            LocalDateTime.of(2024, 3, 10, 13, 37),
                            Utils.profileMemberAccessed.value,
                            read = true
                        ),
                        MessageViewModel(
                            generateUniqueId(),
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed pellentesque justo ut metus egestas finibus. Nam malesuada urna ac ipsum lobortis luctus. Nullam dictum rutrum est at finibus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Maecenas in nulla sit amet est varius mollis. Pellentesque.",
                            LocalDateTime.of(2024, 3, 10, 13, 40),
                            Utils.profileMember2,
                            read = true
                        ),
                        MessageViewModel(
                            generateUniqueId(),
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla gravida.",
                            LocalDateTime.of(2024, 3, 10, 13, 56),
                            Utils.profileMemberAccessed.value,
                            read = true
                        ),
                        MessageViewModel(
                            generateUniqueId(),
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent placerat lacus tellus, eget sagittis lorem.",
                            LocalDateTime.of(2024, 3, 10, 14, 4),
                            Utils.profileMember2
                        ),
                        MessageViewModel(
                            generateUniqueId(),
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse ac mi nisi. Proin eleifend purus eu porttitor pellentesque. Phasellus sit amet nisi vitae velit lacinia sagittis ut id lacus. Fusce.",
                            LocalDateTime.of(2024, 3, 10, 14, 7),
                            Utils.profileMember2
                        )
                    ),
                    isTeamChat = true
                )
        */
        /**
         * TEAMS
         */
        var team1Entity = Team(
            id = "1",
            name = "Speedy Workers",
            picture = null,
            membersList = listOf(
                MemberInfoTeam(
                    Utils.memberAccessed.value,
                    RoleEnum.EXECUTIVE_LEADER,
                    Utils.TimePartecipationTeamTypes.FULL_TIME
                ),
                MemberInfoTeam(
                    member3,
                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                    Utils.TimePartecipationTeamTypes.FULL_TIME
                ),
                MemberInfoTeam(
                    member3,
                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                    Utils.TimePartecipationTeamTypes.FULL_TIME
                ),
                MemberInfoTeam(
                    member3,
                    RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                    Utils.TimePartecipationTeamTypes.FULL_TIME
                ),
                /*Utils.profileMember3,
                Utils.profileMember4,
                Utils.profileMember2,
                Utils.profileMember5,
                Utils.profileMember7*/
            ),
            category = R.string.category_work,
            created = CreatedInfo(
                Utils.memberAccessed.value,
                LocalDateTime.now().minusDays(1)
            ),
            description = "The Speedy Workers are known for their speed and efficiency in completing tasks. With 35 tasks completed out of 40, they maintain a solid performance level (grade 6.8). With a dynamic team of dedicated members, they focus on intense and timely work projects.",
            requestsList = listOf(
                Utils.member6
            ),
            tasksList = listOf(
                task1,
                task2,
                task3,
                task4,
                task5
            )
        )

        /*
                var team2 = TeamViewModel(
                    id = "2",
                    nameParam = "Mighty Planners",
                    pictureParam = null,
                    membersList = listOf(
                        MemberInfoTeam(
                            Utils.memberAccessed.value,
                            RoleEnum.EXECUTIVE_LEADER,
                            Utils.TimePartecipationTeamTypes.FULL_TIME
                        ),
                        MemberInfoTeam(
                            member3,
                            RoleEnum.TECHNOLOGY_AND_IT_MANAGER,
                            Utils.TimePartecipationTeamTypes.PART_TIME
                        ),
                        MemberInfoTeam(
                            member3,
                            RoleEnum.ADMINISTRATIVE_SUPPORTER,
                            Utils.TimePartecipationTeamTypes.FULL_TIME
                        ),
                        MemberInfoTeam(
                            member3,
                            RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                            Utils.TimePartecipationTeamTypes.PART_TIME
                        ),
                        MemberInfoTeam(
                            member3,
                            RoleEnum.ADMINISTRATIVE_SUPPORTER,
                            Utils.TimePartecipationTeamTypes.FULL_TIME
                        ),
                        MemberInfoTeam(
                            member3,
                            RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                            Utils.TimePartecipationTeamTypes.PART_TIME
                        ),
                    ),
                    categoryParam = R.string.category_project,
                    created = Pair(
                        member3,
                        LocalDateTime.now()
                            .minusDays(
                                2
                            )
                    ),
                    description = "The Mighty Planners are experts in planning and managing complex projects. Despite completing 30 out of 45 tasks, their dedication and strategy (grade 4.5) make them a reference team in the project management category.",
                    requestsList = listOf(
                        Utils.member6
                    ),
                    chat = ChatViewModel(
                        ChatModel()
                    ),
                    tasksList = listOf(

                    ),
                    routerActions = RouterActionsProvider.provideRouterActions()
                )

                var team3 = TeamViewModel(
                    Team(
                        id = "3",
                        name = "Crazy Scholars",
                        picture = null,
                        membersList = listOf(
        //                MemberInfoTeam(
        //                    Utils.profileMemberAccessed.value, RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
        //                    Utils.TimePartecipationTeamTypes.FULL_TIME
        //                ),
                            MemberInfoTeam(
                                Utils.profileMember1,
                                RoleEnum.TECHNOLOGY_AND_IT_MANAGER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember2,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.PART_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember3,
                                RoleEnum.ADMINISTRATIVE_SUPPORTER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember4,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember5,
                                RoleEnum.ADMINISTRATIVE_SUPPORTER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember6,
                                RoleEnum.ADMINISTRATIVE_SUPPORTER,
                                Utils.TimePartecipationTeamTypes.PART_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember7,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.PART_TIME
                            ),
                        ),
                        category = R.string.category_study,
                        created = CreatedInfo(
                            member6,
                            LocalDateTime.now()
                                .minusDays(
                                    3
                                )
                        ),
                        description = "The Crazy Scholars are known for their curiosity and love for learning. With 25 tasks completed out of 35, their grade of 8.2 demonstrates exceptional academic competence and commitment to study.",
                        requestsList = listOf(),
                        tasksList = listOf(
                            task25
                        )
                    ),
                    routerActions = RouterActionsProvider.provideRouterActions()
                )

                var team4 = TeamViewModel(
                    Team(
                        id = "4",
                        name = "Smart Researchers",
                        picture = null,
                        membersList = listOf(
                            MemberInfoTeam(
                                Utils.profileMemberAccessed.value,
                                RoleEnum.SALES_AND_MARKETING_LEADER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember1,
                                RoleEnum.TECHNOLOGY_AND_IT_MANAGER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember2,
                                RoleEnum.ADMINISTRATIVE_SUPPORTER,
                                Utils.TimePartecipationTeamTypes.PART_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember3,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember7,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.PART_TIME
                            ),
                        ),
                        category = R.string.category_research,
                        created = CreatedInfo(
                            Utils.member2,
                            LocalDateTime.now()
                                .minusDays(
                                    4
                                )
                        ),
                        description = "The Smart Researchers stand out for their ability to conduct in-depth research. They have completed 28 out of 38 tasks with a grade of 3.7, reflecting a phase of transition and growth in their research capabilities.",
                        requestsList = listOf(
                            Utils.profileMember5
                        ),
                        tasksList = listOf(
                            task26,
                            task27
                        )
                    ),
                    routerActions = RouterActionsProvider.provideRouterActions()
                )

                var team5 = TeamViewModel(
                    Team(
                        id = "5",
                        name = "Wild Developers",
                        picture = null,
                        membersList = listOf(
                            MemberInfoTeam(
                                Utils.profileMemberAccessed.value,
                                RoleEnum.TECHNOLOGY_AND_IT_MANAGER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember1,
                                RoleEnum.TECHNOLOGY_AND_IT_MANAGER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember3,
                                RoleEnum.ADMINISTRATIVE_SUPPORTER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember4,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.PART_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember5,
                                RoleEnum.ADMINISTRATIVE_SUPPORTER,
                                Utils.TimePartecipationTeamTypes.PART_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember6,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                        ),
                        category = R.string.category_research,
                        created = CreatedInfo(
                            member5,
                            LocalDateTime.now()
                                .minusDays(
                                    5
                                )
                        ),
                        description = "The Wild Developers are renowned for their creativity and innovation in software development. With 40 tasks completed out of 42, their grade of 7.9 reflects high competence and consistent commitment to technological development.",
                        requestsList = listOf(
                            Utils.profileMember6
                        ),
                        tasksList = listOf(
                            task28,
                            task29,
                            task30,
                            task31,
                            task32,
                            task33,
                            task34,
                            task35,
                            task36,
                            task37
                        )
                    ),
                    routerActions = RouterActionsProvider.provideRouterActions()
                )

                var team6 = TeamViewModel(
                    Team(
                        id = "6",
                        name = "Brave Pioneers",
                        picture = null,
                        membersList = listOf(
                            MemberInfoTeam(
                                Utils.profileMemberAccessed.value,
                                RoleEnum.EXECUTIVE_LEADER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember1,
                                RoleEnum.TECHNOLOGY_AND_IT_MANAGER,
                                Utils.TimePartecipationTeamTypes.PART_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember4,
                                RoleEnum.ADMINISTRATIVE_SUPPORTER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember5,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.PART_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember7,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                        ),
                        category = R.string.category_innovation,
                        created = CreatedInfo(
                            member1,
                            LocalDateTime.now()
                                .minusDays(
                                    6
                                )
                        ),
                        description = "The Brave Pioneers are the pioneers of innovation, tackling complex challenges with courage. Having completed 32 out of 37 tasks, and with an excellent grade of 9.0, they are a benchmark in the innovation category.",
                        requestsList = listOf(
                            Utils.profileMember7
                        ),
                        tasksList = listOf(
                            task38,
                            task39,
                            task40,
                            task41,
                            task42,
                            task43
                        )
                    ),
                    routerActions = RouterActionsProvider.provideRouterActions()
                )

                var team7 = TeamViewModel(
                    Team(
                        id = "7",
                        name = "Fierce Marketers",
                        picture = null,
                        membersList = listOf(
                            MemberInfoTeam(
                                Utils.profileMemberAccessed.value,
                                RoleEnum.TECHNOLOGY_AND_IT_MANAGER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember4,
                                RoleEnum.TECHNOLOGY_AND_IT_MANAGER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember5,
                                RoleEnum.ADMINISTRATIVE_SUPPORTER,
                                Utils.TimePartecipationTeamTypes.PART_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember6,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                        ),
                        category = R.string.category_marketing,
                        created = CreatedInfo(
                            member4,
                            LocalDateTime.now()
                                .minusDays(
                                    7
                                )
                        ),
                        description = "The Fierce Marketers dominate the marketing sector with their aggressive strategy and creativity. With 45 tasks completed out of 50 and a grade of 5.6, they are continuously seeking to improve their marketing techniques.",
                        requestsList = listOf(
                            Utils.profileMember7
                        ),
                        tasksList = listOf(
                            task44,
                            task45,
                            task46,
                            task47,
                            task48,
                            task49
                        )
                    ),
                    routerActions = RouterActionsProvider.provideRouterActions()
                )

                var team8 = TeamViewModel(
                    Team(
                        id = "8",
                        name = "Bold Sellers",
                        picture = null,
                        membersList = listOf(
                            MemberInfoTeam(
                                Utils.profileMemberAccessed.value,
                                RoleEnum.TECHNOLOGY_AND_IT_MANAGER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                            MemberInfoTeam(
                                Utils.profileMember7,
                                RoleEnum.OPERATIONAL_AND_TECHNICAL_STAFFER,
                                Utils.TimePartecipationTeamTypes.FULL_TIME
                            ),
                        ),
                        category = R.string.category_sales,
                        created = CreatedInfo(
                            Utils.member7,
                            LocalDateTime.now()
                                .minusDays(
                                    8
                                )
                        ),
                        description = "The Bold Sellers stand out for their boldness in sales. With 33 tasks completed out of 40 and a grade of 6.3, they demonstrate solid skills and continuous growth in the sales sector.",
                        requestsList = listOf(
                            Utils.profileMember6
                        ),
                        tasksList = listOf(
                            task50
                        )
                    ),
                    routerActions = RouterActionsProvider.provideRouterActions()
                )
        */
        val allTeamList = listOf(
            team1Entity

        )

        /** END FAKE DATA **/
    }
}