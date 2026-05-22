package com.turkcell.ticketapp.navigation

import android.window.SplashScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.ticketapp.screen.HomeScreen
import com.turkcell.ticketapp.screen.LoginScreen
import com.turkcell.ticketapp.screen.RegisterScreen
import com.turkcell.ticketapp.screen.TicketDetailScreen
import com.turkcell.ticketapp.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    authRepository: AuthRepository = koinInject()
)
{
    val isLoggedIn by authRepository.isLoggedIn.collectAsStateWithLifecycle(initialValue = null)

    when(isLoggedIn)
    {
        null -> SplashScreen()
        true -> AuthedNavHost(navController)
        false -> UnAuthedNavHost(navController)
    }
}

@Composable
private fun SplashScreen(){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        CircularProgressIndicator()
    }
}

@Composable
private fun AuthedNavHost(navController: NavHostController){
    NavHost(navController=navController, startDestination = Home){
        composable<Home> {
            HomeScreen()
        }
    }
}

@Composable
private fun UnAuthedNavHost(navController: NavHostController){
    NavHost(navController=navController, startDestination = Login) {
        composable<Login>{
            LoginScreen(
                onLoginSuccess = { navController.navigate(Home) },
                onNavigateToRegister = {navController.navigate(Register)}
            )
        }
        composable<Register> {
            Text("Register Screen")
        }
    }
}

@Composable
private fun AuthedNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                onTicketClick = { ticketId -> navController.navigate(TicketDetail(ticketId)) }
            )
        }
        composable<TicketDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<TicketDetail>()
            TicketDetailScreen(ticketId = route.ticketId)
        }
    }
}