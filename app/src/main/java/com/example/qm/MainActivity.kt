@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.example.qm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.qm.Navigation.Args.CURRENCY_ID
import com.example.qm.Navigation.Routes.ADD_CONTACT
import com.example.qm.Navigation.Routes.CONTRACTS
import com.example.qm.Navigation.Routes.CURRENCIES
import com.example.qm.Navigation.Routes.LOGIN
import com.example.qm.Navigation.Routes.PROFILE
import com.example.qm.ui.addcontract.ContactScreen
import com.example.qm.ui.contracts.ContractsContract
import com.example.qm.ui.contracts.ContractsScreen
import com.example.qm.ui.currencies.CurrenciesContract
import com.example.qm.ui.currencies.CurrenciesScreen
import com.example.qm.ui.login.LoginScreen
import com.example.qm.ui.profile.ProfileContract
import com.example.qm.ui.profile.ProfileScreen
import com.example.qm.ui.theme.QmmTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DefaultPreview()
        }
        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        //WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

@Composable
fun DefaultPreview(
    navController: NavHostController = rememberNavController()
) {
    QmmTheme {
        Surface(
            Modifier.fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = CURRENCIES
            ) {
                composable(
                    route = CURRENCIES,
                ) {
                    CurrenciesScreen(
                        viewModel = hiltViewModel(),
                        onNavigationRequested = { navigation ->
                            when(navigation) {
                                is CurrenciesContract.Nav.ToAddContact -> {
                                    navController.navigate(ADD_CONTACT)
                                }
                                is CurrenciesContract.Nav.ToLogin -> {
                                    navController.navigate(LOGIN)
                                }
                                is CurrenciesContract.Nav.ToProfile -> {
                                    navController.navigate(PROFILE)
                                }
                                is CurrenciesContract.Nav.ToContracts -> {
                                    navController.navigate("$CONTRACTS/${navigation.currency}")
                                }
                            }
                        },
                    )
                }
                composable(
                    route = LOGIN
                ) {
                    LoginScreen(viewModel = hiltViewModel()) { navController.popBackStack() }
                }
                composable(
                    route = ADD_CONTACT
                ) {
                    ContactScreen(viewModel = hiltViewModel()) { navController.popBackStack() }
                }
                composable(
                    route = PROFILE
                ) {
                    ProfileScreen(
                        onNavigationRequested = { navigation ->
                            when(navigation) {
                                is ProfileContract.Nav.ToBackStack -> {
                                    navController.popBackStack()
                                }
                            }
                        },
                        viewModel = hiltViewModel()
                    )
                }
                composable(
                    route = "$CONTRACTS/{$CURRENCY_ID}"
                ) { backStackEntry ->
                    val currency = backStackEntry.arguments?.getString(CURRENCY_ID)
                    if(currency != null) {
                        ContractsScreen(currency = currency,
                            viewModel = hiltViewModel(),
                            onNavigationRequested = { navigation ->
                                when(navigation) {
                                    ContractsContract.Nav.ToBackStack -> {
                                        navController.popBackStack()
                                    }
                                }
                            })
                    }
                }
            }
        }
    }
}

object Navigation {

    object Args {
        const val CURRENCY_ID = "currencyId"
    }

    object Routes {
        const val CURRENCIES = "CurrenciesScreen"

        const val CONTRACTS = "ContractsScreen"
        //const val CURRENCIES_WITH_SCAFFOLD = "$CURRENCIES/{$SHOW_SCAFFOLD}"
        const val ADD_CONTACT = "ContactScreen"
        const val LOGIN = "LoginScreen"
        const val PROFILE = "ProfileScreen"
    }

}














