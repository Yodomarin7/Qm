@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.qm.ui.profile

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.qm.R
import com.example.qm.helper.compose.MyTextField
import com.example.qm.helper.isFail
import com.example.qm.helper.myErrToStr
import com.example.qm.model.Profile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigationRequested: (nav: ProfileContract.Nav) -> Unit,
) {
    Log.e("C", "ProfileScreen")

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val enabledAddButton = remember { mutableStateOf(false) }

    val textFirstName = remember { mutableStateOf(TextFieldValue("")) }
    val textLastName = remember { mutableStateOf(TextFieldValue("")) }

    val state = viewModel.state.collectAsState().value

    val snackbarHostState = remember { SnackbarHostState() }

    if(state.fail?.toast == true) {
        LaunchedEffect(true) {
            snackbarHostState.showSnackbar(context.getString(R.string.error) + " " + state.fail)
        }
    }

    LaunchedEffect(true) {
        when (val result = viewModel.nav.firstOrNull()) {
            is ProfileContract.Nav.ToBackStack -> {
                if(result.saved) {
                    snackbarHostState.showSnackbar(context.getString(R.string.success_saved_data))
                }
                onNavigationRequested(ProfileContract.Nav.ToBackStack(false))
            }
            null -> {
                onNavigationRequested(ProfileContract.Nav.ToBackStack(false))
            }
        }
    }

    Scaffold(
        Modifier.padding(top = 8.dp, bottom = 8.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { ProfileToolbar(onNavigationRequested) }
    ) {  innerPadding ->
        Column(
            Modifier.fillMaxSize().imePadding().padding(innerPadding)) {
            when {
                state.showProgress != null || state.fail?.singly == true -> {
                    Column(
                        Modifier.fillMaxSize().padding(top = 8.dp, start = 16.dp, end = 16.dp),
                        Arrangement.Center, Alignment.CenterHorizontally
                    ) {
                        when {
                            state.showProgress  != null -> {
                                Text(text = stringResource(state.showProgress))
                                Box(Modifier.height(8.dp))
                                CircularProgressIndicator()
                            }
                            state.fail?.singly == true -> {
                                Text(
                                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                                    text = myErrToStr(state.fail),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Box(Modifier.height(8.dp))
                                Button(onClick = { viewModel.setEvent(ProfileContract.Event.GetMyProfile) }) {
                                    Text(text = stringResource(id = R.string.try_again))
                                }
                            }
                        }
                    }
                }
                state.success != null -> {
                    val isErrorFirstName = remember { mutableStateOf(true) }
                    val isErrorLastName = remember { mutableStateOf(true) }

                    val onValueChange = { enabledAddButton.value = !isErrorFirstName.value && !isErrorLastName.value }

                    Column(
                        Modifier.verticalScroll(rememberScrollState()).weight(1f)
                    ) {
                        Column(
                            Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                        ) {
                            FirstNameTextField(isError = isErrorFirstName, text = textFirstName) { onValueChange() }
                            Box(Modifier.height(16.dp))
                            LastNameTextField(isError = isErrorLastName, text = textLastName) { onValueChange() }

                            LaunchedEffect(true) {
                                delay(100)
                                textFirstName.value = TextFieldValue(state.success.firstname!!)
                                textLastName.value = TextFieldValue(state.success.lastName!!)
                            }
                        }
                    }
                }
            }
            if (state.success != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { onNavigationRequested(ProfileContract.Nav.ToBackStack(false)) }
                    ) { Text(stringResource(id = R.string.cancel)) }
                    Button(enabled = enabledAddButton.value, onClick = {
                        enabledAddButton.value = false
                        focusManager.clearFocus()

                        viewModel.setEvent(
                            ProfileContract.Event.SetMyProfile(
                                Profile(textFirstName.value.text, textLastName.value.text)
                            )
                        )
                    }) {
                        Text(stringResource(id = R.string.add))
                    }
                }
            }
        }
    }
}

@Composable
private fun FirstNameTextField(
    isError: MutableState<Boolean>,
    text: MutableState<TextFieldValue>,
    onValueChange: ()->Unit,
) {
    val supportingText = remember{ mutableStateOf(R.string.enter_your_first_name) }

    MyTextField(isError = isError, supportingText = supportingText,
        textFieldValue = text, label = stringResource(id = R.string.first_name),
        validate = { txt ->
            if (txt.isEmpty()) {
                isError.value = true
                supportingText.value = R.string.enter_your_first_name
            } else if (txt.length > 16) {
                isError.value = true
                supportingText.value = R.string.first_name_not_must_more_16
            } else {
                isError.value = false
                supportingText.value = R.string.correct
            }
            onValueChange()
        }
    )
}

@Composable
private fun LastNameTextField(
    isError: MutableState<Boolean>,
    text: MutableState<TextFieldValue>,
    onValueChange: ()->Unit
) {
    val supportLastName = remember{ mutableStateOf(R.string.enter_your_last_name) }

    MyTextField(isError = isError, supportingText = supportLastName,
        textFieldValue = text, label = stringResource(id = R.string.last_name),
        validate = { txt ->
            if (txt.isEmpty()) {
                isError.value = true
                supportLastName.value = R.string.enter_your_last_name
            } else if (txt.length > 16) {
                isError.value = true
                supportLastName.value = R.string.last_name_not_must_more_16
            } else {
                isError.value = false
                supportLastName.value = R.string.correct
            }
            onValueChange()
        }
    )
}

@Composable
private fun ProfileToolbar(onNavigationRequested: (nav: ProfileContract.Nav) -> Unit) {
    TopAppBar(
        title = {
            Text(
                stringResource(id = R.string.profile),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { onNavigationRequested(ProfileContract.Nav.ToBackStack(false)) }) {
                Icon(Icons.Filled.ArrowBack, stringResource(id = R.string.back))
            }
        }
    )
}