@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.qm.ui.addcontract

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.qm.R
import com.example.qm.helper.Currencies
import com.example.qm.helper.compose.MyAutoTextField
import com.example.qm.helper.compose.MyTextField
import com.example.qm.source.room.ContactEntity
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

@Composable
fun ContactScreen(
    viewModel: AddContractViewModel,
    onNavigationRequested: (nav: AddContractContract.Nav) -> Unit,
) {
    Log.e("C", "ContactScreen")

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val state = viewModel.state.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }

    if(state.fail?.toast == true) {
        LaunchedEffect(true) {
            snackbarHostState.showSnackbar(context.getString(R.string.error) + " " + state.fail)
        }
    }

    LaunchedEffect(true) {
        when (val result = viewModel.nav.firstOrNull()) {
            is AddContractContract.Nav.ToBackStack -> {
                if(result.saved) {
                    snackbarHostState.showSnackbar(context.getString(R.string.success_saved_data))
                }
                onNavigationRequested(AddContractContract.Nav.ToBackStack(false))
            }
            null -> {
                onNavigationRequested(AddContractContract.Nav.ToBackStack(false))
            }
        }
    }

    val enabledAddButton = remember { mutableStateOf(false) }

    val textCurrency = remember { mutableStateOf(TextFieldValue("")) }
    val textFirstName = remember { mutableStateOf(TextFieldValue("")) }
    val textLastName = remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        Modifier.padding(top = 8.dp, bottom = 8.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { ContactToolbar(onNavigationRequested) }
    ) {  innerPadding ->
        Column(
            Modifier.fillMaxSize().imePadding().padding(innerPadding)) {
            when {
                state.showProgress != null -> {
                    Column(
                        Modifier.fillMaxSize().padding(top = 8.dp, start = 16.dp, end = 16.dp),
                        Arrangement.Center, Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(state.showProgress))
                        Box(Modifier.height(8.dp))
                        CircularProgressIndicator()
                    }
                }
                state.success -> {
                    val isErrorCurrency = remember { mutableStateOf(true) }
                    val isErrorFirstName = remember { mutableStateOf(true) }
                    val isErrorLastName = remember { mutableStateOf(true) }

                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(1f)
                    ) {
                        Column(Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
                            val onValueChange = { enabledAddButton.value =
                                !isErrorFirstName.value && !isErrorLastName.value && !isErrorCurrency.value }

                            CurrencyAutoTextField(isError = isErrorCurrency, text = textCurrency, onValueChange = { onValueChange() }, context = context )
                            Box(Modifier.height(16.dp))
                            FirstNameTextField(isError = isErrorFirstName, text = textFirstName) { onValueChange() }
                            Box(Modifier.height(16.dp))
                            LastNameTextField(isError = isErrorLastName, text = textLastName) { onValueChange() }
                        }
                    }
                }
            }
            if (state.success) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { onNavigationRequested(AddContractContract.Nav.ToBackStack(false)) }
                    ) { Text(stringResource(id = R.string.cancel)) }
                    Button(enabled = enabledAddButton.value, onClick = {
                        enabledAddButton.value = false
                        focusManager.clearFocus()

                        viewModel.setEvent(
                            AddContractContract.Event.SetContact(
                                ContactEntity(0, "", textFirstName.value.text,
                                    textLastName.value.text), textCurrency.value.text.uppercase(Locale.getDefault())
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
fun CurrencyAutoTextField(
    isError: MutableState<Boolean>,
    text: MutableState<TextFieldValue>,
    onValueChange: ()->Unit,
    context: Context
) {
    val supportingText = remember{ mutableStateOf(R.string.enter_your_currency) }
    val options = remember{ mutableStateOf(mapOf<String, Int>()) }
    val optionsSize = remember{ mutableStateOf(options.value.size) }

    MyAutoTextField(
        isError = isError,
        supportingText = supportingText,
        textFieldValue = text,
        label = stringResource(id = R.string.enter_your_currency),
        optionsSize = optionsSize,
        OnOptionClick = { options.value.keys.elementAt(it) },
        optionText = { Text(options.value.keys.elementAt(it) + " " + stringResource(id = options.value.values.elementAt(it))) },
        validate = { txt ->
            if (txt.isEmpty()) {
                options.value = mapOf()
                optionsSize.value = options.value.size

                isError.value = true
                supportingText.value = R.string.enter_your_currency
            } else {
                options.value = Currencies(context).getItems(txt)
                optionsSize.value = options.value.size

                if(options.value.isEmpty()) {
                    isError.value = true
                    supportingText.value = R.string.not_found
                } else {
                    if(options.value.size == 1 && txt.equals(options.value.keys.elementAt(0), ignoreCase = true)) {
                        supportingText.value = options.value.values.elementAt(0)
                        text.value = TextFieldValue(text.value.text.uppercase(Locale.getDefault()),
                            selection = TextRange(text.value.text.length))
                        isError.value = false

                        options.value = mapOf()
                        optionsSize.value = options.value.size
                    } else {
                        isError.value = true
                        supportingText.value = R.string.select_one
                    }
                }
            }
            onValueChange()
        }
    )
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
private fun ContactToolbar(onNavigationRequested: (nav: AddContractContract.Nav) -> Unit) {
    TopAppBar(
        title = {
            Text(
                stringResource(id = R.string.contact),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { onNavigationRequested(AddContractContract.Nav.ToBackStack(false)) }) {
                Icon(Icons.Filled.ArrowBack, stringResource(id = R.string.back))
            }
        }
    )
}