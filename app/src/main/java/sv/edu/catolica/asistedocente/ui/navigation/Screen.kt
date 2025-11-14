package sv.edu.catolica.asistedocente.ui.navigation

/**
 * Define las rutas de navegación de la aplicación
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object GroupList : Screen("group_list")
    object GroupDetail : Screen("group_detail/{groupId}") {
        fun createRoute(groupId: Long) = "group_detail/$groupId"
    }
    object AddEditGroup : Screen("add_edit_group?groupId={groupId}") {
        fun createRoute(groupId: Long? = null) =
            if (groupId != null) "add_edit_group?groupId=$groupId"
            else "add_edit_group"
    }
    object StudentList : Screen("student_list/{groupId}") {
        fun createRoute(groupId: Long) = "student_list/$groupId"
    }
    object AddEditStudent : Screen("add_edit_student/{groupId}?studentId={studentId}") {
        fun createRoute(groupId: Long, studentId: Long? = null) =
            if (studentId != null) "add_edit_student/$groupId?studentId=$studentId"
            else "add_edit_student/$groupId"
    }
    object Attendance : Screen("attendance/{groupId}?date={date}") {
        fun createRoute(groupId: Long, date: Long? = null) =
            if (date != null) "attendance/$groupId?date=$date"
            else "attendance/$groupId"
    }
    object AttendanceHistory : Screen("attendance_history/{groupId}") {
        fun createRoute(groupId: Long) = "attendance_history/$groupId"
    }
    object Reports : Screen("reports")
    object ReportDetail : Screen("report_detail/{groupId}?startDate={startDate}&endDate={endDate}") {
        fun createRoute(groupId: Long, startDate: Long, endDate: Long) =
            "report_detail/$groupId?startDate=$startDate&endDate=$endDate"
    }
    object Settings : Screen("settings")
    object DocenteProfile : Screen("docente_profile")
}
