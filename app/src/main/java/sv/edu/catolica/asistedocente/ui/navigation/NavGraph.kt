package sv.edu.catolica.asistedocente.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import sv.edu.catolica.asistedocente.ui.screens.attendance.AttendanceScreen
import sv.edu.catolica.asistedocente.ui.screens.groups.AddEditGroupScreen
import sv.edu.catolica.asistedocente.ui.screens.groups.GroupDetailScreen
import sv.edu.catolica.asistedocente.ui.screens.home.HomeScreen
import sv.edu.catolica.asistedocente.ui.screens.login.LoginScreen
import sv.edu.catolica.asistedocente.ui.screens.profile.DocenteProfileScreen
import sv.edu.catolica.asistedocente.ui.screens.register.RegisterScreen
import sv.edu.catolica.asistedocente.ui.screens.reports.ReportsScreen
import sv.edu.catolica.asistedocente.ui.screens.settings.SettingsScreen
import sv.edu.catolica.asistedocente.ui.screens.students.AddEditStudentScreen
import sv.edu.catolica.asistedocente.utils.AuthHelper

/**
 * Grafo de navegación principal de la aplicación
 * Define todas las rutas y sus transiciones
 */
@Composable
fun NavGraph(
    navController: NavHostController
) {
    val context = LocalContext.current
    val isLoggedIn = AuthHelper.isLoggedIn(context)
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de Login
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Pantalla de Registro
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de inicio/Home
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToGroupDetail = { groupId ->
                    navController.navigate(Screen.GroupDetail.createRoute(groupId))
                },
                onNavigateToAddGroup = {
                    navController.navigate(Screen.AddEditGroup.createRoute())
                },
                onNavigateToAttendance = { groupId ->
                    navController.navigate(Screen.Attendance.createRoute(groupId))
                },
                onNavigateToReports = {
                    navController.navigate(Screen.Reports.route)
                },
                onNavigateToEditGroup = { groupId ->
                    navController.navigate(Screen.AddEditGroup.createRoute(groupId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.DocenteProfile.route)
                },
                onLogout = {
                    AuthHelper.logout(context)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Lista de grupos
        composable(route = Screen.GroupList.route) {
            // TODO: Implementar GroupListScreen
        }

        // Detalle de grupo
        composable(
            route = Screen.GroupDetail.route,
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: return@composable
            GroupDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditGroup = { id ->
                    navController.navigate(Screen.AddEditGroup.createRoute(id))
                },
                onNavigateToAddStudent = { id ->
                    navController.navigate(Screen.AddEditStudent.createRoute(id))
                },
                onNavigateToEditStudent = { groupId, studentId ->
                    navController.navigate(Screen.AddEditStudent.createRoute(groupId, studentId))
                },
                onNavigateToAttendance = { id ->
                    navController.navigate(Screen.Attendance.createRoute(id))
                }
            )
        }

        // Agregar/Editar grupo
        composable(
            route = Screen.AddEditGroup.route,
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId")
            AddEditGroupScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // Lista de estudiantes
        composable(
            route = Screen.StudentList.route,
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: return@composable
            // TODO: Implementar StudentListScreen
        }

        // Agregar/Editar estudiante
        composable(
            route = Screen.AddEditStudent.route,
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.LongType
                },
                navArgument("studentId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: return@composable
            val studentId = backStackEntry.arguments?.getLong("studentId")
            AddEditStudentScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // Toma de asistencia (PANTALLA CRÍTICA)
        composable(
            route = Screen.Attendance.route,
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.LongType
                },
                navArgument("date") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: return@composable
            val date = backStackEntry.arguments?.getLong("date")
            AttendanceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Historial de asistencia
        composable(
            route = Screen.AttendanceHistory.route,
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: return@composable
            // TODO: Implementar AttendanceHistoryScreen
        }

        // Reportes
        composable(route = Screen.Reports.route) {
            ReportsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Detalle de reporte
        composable(
            route = Screen.ReportDetail.route,
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.LongType
                },
                navArgument("startDate") {
                    type = NavType.LongType
                },
                navArgument("endDate") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: return@composable
            val startDate = backStackEntry.arguments?.getLong("startDate") ?: return@composable
            val endDate = backStackEntry.arguments?.getLong("endDate") ?: return@composable
            // TODO: Implementar ReportDetailScreen
        }

        // Configuración
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Perfil de Docente
        composable(route = Screen.DocenteProfile.route) {
            DocenteProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
