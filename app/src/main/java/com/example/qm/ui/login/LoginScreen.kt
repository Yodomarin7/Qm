package com.example.qm.ui.login

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import kotlinx.coroutines.flow.firstOrNull
import com.example.qm.R
import com.example.qm.helper.isFail

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onBackStack: () -> Unit,
) {
    Log.e("C", "LoginScreen")

    val context = LocalContext.current

    val state = viewModel.state.collectAsState().value

    val signInLauncher = rememberLauncherForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        val response = res.idpResponse
        if(res.resultCode == RESULT_OK) {
            viewModel.setEvent(LoginContract.Event.ToBackStack)
        }
        else {
            if(response?.error?.errorCode != null) {
                viewModel.setEvent(
                    LoginContract.Event.SetError(
                        response.error?.cause?.message ?: context.getString(R.string.error_try_again)
                    )
                )
            } else { viewModel.setEvent(LoginContract.Event.ToBackStack) }
        }
    }

    LaunchedEffect(true) {
        viewModel.nav.firstOrNull {
            when(it) {
                LoginContract.Nav.LaunchLogin -> {
                    val providers = arrayListOf(
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                    )

                    val signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build()

                    signInLauncher.launch(signInIntent)

                    false
                }
                LoginContract.Nav.ToBackStack -> {
                    onBackStack()
                    true
                }
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp),
        Arrangement.Center, Alignment.CenterHorizontally
    ) {
        Text(text = "This is login screen", color = Color.Green)
        when {
            state.showProgress != null -> {
                Text(text = stringResource(id = state.showProgress), color = Color.Blue)
                Box(Modifier.height(8.dp))
                CircularProgressIndicator()
            }
            isFail(state.fail) -> {
                Text(modifier = Modifier.padding(start = 8.dp, end = 8.dp), text = "Login " + state.fail,
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error
                )
                Box(Modifier.height(8.dp))
                Button(onClick = onBackStack) {
                    Text(text = stringResource(id = R.string.try_again))
                }
            }
            state.success -> {
                viewModel.setEvent(LoginContract.Event.ToBackStack)
            }
        }
    }
}