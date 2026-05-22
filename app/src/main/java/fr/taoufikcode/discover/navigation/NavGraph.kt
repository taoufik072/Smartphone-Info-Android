package fr.taoufikcode.discover.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.taoufikcode.presentation.details.SmartphoneDetailsScreenRoot
import fr.taoufikcode.presentation.list.SmartphoneListScreenRoot

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.SmartphoneList,
    ) {
        composable<Route.SmartphoneList> {
            SmartphoneListScreenRoot(
                onNavigate = { smartphoneId ->
                    navController.navigate(Route.SmartphoneDetails(smartphoneId))
                },
            )
        }

        composable<Route.SmartphoneDetails> {
            SmartphoneDetailsScreenRoot(
                onBackClick = { navController.navigateUp() },
            )
        }
    }
}
