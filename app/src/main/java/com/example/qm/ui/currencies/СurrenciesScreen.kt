@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.qm.ui.currencies

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.qm.R
import com.example.qm.helper.Currencies
import com.example.qm.helper.myErrToStr
import com.example.qm.model.SortCurrency
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

@Composable
fun CurrenciesScreen(
    viewModel: CurrenciesViewModel,
    onNavigationRequested: (nav: CurrenciesContract.Nav) -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    Log.e("C", "CurrenciesScreen")

    val context = LocalContext.current

    val state = viewModel.state.collectAsState().value

    DisposableEffect(true) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (state.fail != null) {
                    viewModel.setEvent(CurrenciesContract.Event.GetItems)
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(true) {
        when (viewModel.nav.firstOrNull()) {
            CurrenciesContract.Nav.ToLogin -> {
                viewModel.setEvent(CurrenciesContract.Event.SetError("Nav Login Error"))
                onNavigationRequested(CurrenciesContract.Nav.ToLogin)
            }
            CurrenciesContract.Nav.ToProfile -> {
                viewModel.setEvent(CurrenciesContract.Event.SetError("Nav Login Error"))
                onNavigationRequested(CurrenciesContract.Nav.ToProfile)
            }
            else -> {}
        }
    }

    val fab: MutableState<@Composable () -> Unit> = remember { mutableStateOf({ }) }
    val enabledLogOutButton = remember { mutableStateOf(false) }

    Scaffold(
        Modifier.padding(top = 8.dp, bottom = 8.dp),
        topBar = {
            MyToolbar(onNavigationRequested, enabledLogOutButton.value, viewModel, state.sorting)
        },
        floatingActionButton = fab.value
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.showProgress != null || state.fail?.singly == true -> {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                        Arrangement.Center, Alignment.CenterHorizontally
                    ) {
                        when {
                            state.showProgress != null -> {
                                enabledLogOutButton.value = false
                                fab.value = { }

                                Text(text = stringResource(state.showProgress))
                                Box(Modifier.height(8.dp))
                                CircularProgressIndicator()
                            }
                            state.fail?.singly == true -> {
                                enabledLogOutButton.value = false
                                fab.value = { }

                                Text(
                                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                                    text = myErrToStr(state.fail),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Box(Modifier.height(8.dp))
                                Button(onClick = { viewModel.setEvent(CurrenciesContract.Event.GetItems) }) {
                                    Text(text = stringResource(id = R.string.try_again))
                                }
                            }
                        }
                    }
                }
                state.success != null -> {
                    val itemsMap = state.success
                    enabledLogOutButton.value = true
                    fab.value = {
                        FloatingActionButton(
                            onClick = {
                                onNavigationRequested(CurrenciesContract.Nav.ToAddContact)
                            },
                        ) {
                            Icon(Icons.Filled.Add, stringResource(id = R.string.add_new_contact))
                        }
                    }
                    LazyColumn {
                        items(itemsMap.size) { i ->
                            val code = itemsMap.keys.elementAt(i).uppercase(Locale.getDefault())
                            val r = Currencies(context).getItem(code)

                            ListItem(
                                modifier = Modifier.clickable {
                                    onNavigationRequested(
                                        CurrenciesContract.Nav.ToContracts(
                                            itemsMap.keys.elementAt(i)
                                        )
                                    )
                                },
                                headlineText = { Text(code) },
                                supportingText = {
                                    if (r != null) {
                                        Text(stringResource(r))
                                    }
                                },
                                trailingContent = {
                                    Text(
                                        itemsMap[itemsMap.keys.elementAt(i)].toString(),
                                        fontSize = 16.sp
                                    )
                                }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun MyToolbar(
    onNavigationRequested: (nav: CurrenciesContract.Nav) -> Unit,
    enabledLogOutButton: Boolean,
    viewModel: CurrenciesViewModel,
    sort: SortCurrency
) {
    var showMenu: Boolean by remember { mutableStateOf(false) }
    var showSorting: Boolean by remember { mutableStateOf(false) }
    val context = LocalContext.current

    TopAppBar(
        title = {
            Text(
                stringResource(id = R.string.app_name),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = { showSorting = !showSorting }) {
                Icon(
                    painterResource(R.drawable.ic_baseline_sort_24),
                    stringResource(id = R.string.sorting)
                )
            }
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Filled.List, stringResource(id = R.string.options_menu))
            }
            DropdownMenu(
                expanded = showSorting,
                onDismissRequest = { showSorting = false }
            ) {
                DropdownMenuItem(
                    leadingIcon = {
                        if(sort == SortCurrency.ByName) {
                            Icon(Icons.Filled.Star, stringResource(id = R.string.selecting))
                        }
                    },
                    onClick = { showSorting = false
                        viewModel.setEvent(CurrenciesContract.Event.SortByName)
                    },
                    text = { Text(stringResource(id = R.string.by_name)) }
                )
                DropdownMenuItem(
                    leadingIcon = {
                        if(sort == SortCurrency.BySum) {
                            Icon(Icons.Filled.Star, stringResource(id = R.string.selecting))
                        }
                    },
                    onClick = { showSorting = false
                        viewModel.setEvent(CurrenciesContract.Event.SortBySum)},
                    text = { Text(stringResource(id = R.string.by_sum)) },
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        onNavigationRequested(CurrenciesContract.Nav.ToProfile)
                    },
                    text = { Text(stringResource(id = R.string.profile)) }
                )
                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        viewModel.setEvent(CurrenciesContract.Event.LogOut(context))
                    },
                    text = { Text(stringResource(id = R.string.logout)) },
                    enabled = enabledLogOutButton
                )
            }
        }
    )
}