package it.polito.students.showteamdetails.routers

import androidx.navigation.NavHostController
import it.polito.students.showteamdetails.Utils

class RouterActions(
    private val navController: NavHostController
) {
    val getCurrentRoute: () -> String = {
        navController.currentBackStackEntry?.destination?.route ?: "no route"
    }
    val goBack: () -> Unit = {
        navController.popBackStack()
    }
    val goBackUntilRouteDifferentFrom: (Set<String>) -> Unit = { routes ->
        while (true) {
            if (!navController.popBackStack()) {
                break
            }
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute != null && !routes.contains(currentRoute)) {
                break
            }

        }
    }

    // TEAM
    val navigateToHomeTeam: () -> Unit = {
        navController.navigate(Utils.RoutesEnum.HOME_TEAM.name)
    }
    val navigateToTeam: (String) -> Unit = { teamId ->
        navController.navigate("${Utils.RoutesEnum.TEAM_PAGE.name}/$teamId")
    }
    val navigateToAddTeam: () -> Unit = {
        navController.navigate(Utils.RoutesEnum.ADD_TEAM.name)
    }
    val navigateToEditTeam: (String) -> Unit = { teamId ->
        navController.navigate("${Utils.RoutesEnum.MODIFY_TEAM.name}/$teamId")
    }
    val navigateToStatistics: (String) -> Unit = { teamId ->
        navController.navigate("${Utils.RoutesEnum.TEAM_STATISTICS.name}/$teamId")
    }
    val navigateToQrCodeSharePage: (String) -> Unit = { teamId ->
        navController.navigate("${Utils.RoutesEnum.TEAM_QRCODE_SHARE_PAGE.name}/$teamId")
    }
    val navigateToConfirmJoinTeam: (String) -> Unit = { teamId ->
        navController.navigate("${Utils.RoutesEnum.TEAM_CONFIRM_JOIN.name}/$teamId")
    }
    val navigateToRequests: (String) -> Unit = { teamId ->
        navController.navigate("${Utils.RoutesEnum.TEAM_REQUESTS.name}/$teamId")
    }
    val navigateToRoleAssignationMember: (String, String) -> Unit = { memberId, teamId ->
        navController.navigate("$teamId/${Utils.RoutesEnum.ROLE_ASSIGNATION_REQUEST_MEMBER}/$memberId")
    }
    val navigateToRoleAssignationTeam: (String, String) -> Unit = { memberId, teamId ->
        navController.navigate("$teamId/${Utils.RoutesEnum.ROLE_ASSIGNATION_REQUEST_TEAM}/$memberId")
    }
    val navigateToLeaveTeam: (String) -> Unit = { teamId ->
        navController.navigate("${Utils.RoutesEnum.TEAM_PAGE}/$teamId/${Utils.RoutesEnum.LEAVE_TEAM}")
    }
    val navigateToDeleteTeam: (String) -> Unit = { teamId ->
        navController.navigate("${Utils.RoutesEnum.TEAM_PAGE}/$teamId/${Utils.RoutesEnum.DELETE_TEAM}")
    }
    val navigateToDeleteTeamMember: (String, String) -> Unit = { teamId, memberId ->
        navController.navigate("${Utils.RoutesEnum.TEAM_PAGE}/$teamId/${Utils.RoutesEnum.DELETE_TEAM_MEMBER.name}/$memberId")
    }

    // CHAT
    val navigateToHomeChat: () -> Unit = {
        navController.navigate(Utils.RoutesEnum.HOME_CHAT.name)
    }
    val navigateToChat: (chatId: String) -> Unit = { chatId ->
        navController.navigate("${Utils.RoutesEnum.CHAT_PAGE.name}/$chatId")
    }

    val navigateToNewChatPage: () -> Unit = {
        navController.navigate(Utils.RoutesEnum.NEW_CHAT_PAGE.name)
    }

    // PROFILE
    val navigateToProfile: () -> Unit = {
        navController.navigate(Utils.RoutesEnum.PROFILE.name)
    }
    val enterProfileEditMode: () -> Unit = {
        navController.navigate("${Utils.RoutesEnum.PROFILE.name}/edit")
    }

    val navigateToAccountSettings: () -> Unit = {
        navController.navigate(Utils.RoutesEnum.ACCOUNT_SETTINGS.name)
    }

    val navigateToMemberProfile: (String) -> Unit = { memberId ->
        navController.navigate("${Utils.RoutesEnum.MEMBER_PROFILE.name}/$memberId")
    }

    val navigateToAboutUs: () -> Unit = {
        navController.navigate(Utils.RoutesEnum.ABOUTUS.name)
    }

    val navigateToViewTask: (String, String) -> Unit = { teamId, taskId ->
        navController.navigate("${Utils.RoutesEnum.TEAM_PAGE.name}/$teamId/${Utils.RoutesEnum.VIEW_TASK.name}/$taskId")
    }

    val navigateToModifyTask: (String, String) -> Unit = { teamId, taskId ->
        navController.navigate("${Utils.RoutesEnum.TEAM_PAGE.name}/$teamId/${Utils.RoutesEnum.MODIFY_TASK.name}/$taskId")
    }

    val navigateToCreateTask: (String) -> Unit = { teamId ->
        navController.navigate("${Utils.RoutesEnum.TEAM_PAGE.name}/$teamId/${Utils.RoutesEnum.CREATE_TASK.name}")
    }

    val navigateToMemberRequestPreview: (String, String) -> Unit = { memberId, teamId ->
        navController.navigate("$teamId/${Utils.RoutesEnum.TEAM_REQUESTS}/$memberId")
    }

    val navigateToLogOut: () -> Unit = {
        navController.navigate(Utils.RoutesEnum.LOGOUT.name)
    }

    val navigateToLogIn: () -> Unit = {
        navController.navigate(Utils.RoutesEnum.LOGIN.name)
    }

    val navigateToRegister: () -> Unit = {
        navController.navigate(Utils.RoutesEnum.REGISTER.name)
    }
}