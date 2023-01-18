@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.qm.ui.contracts

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qm.R
import com.example.qm.helper.myErrToStr

@Composable
fun ContractsScreen(
    currency: String,
    viewModel: ContractsViewModel,
    onNavigationRequested: (nav: ContractsContract.Nav) -> Unit,
) {
    Log.e("C", "ContractsScreen")
    val state = viewModel.state.collectAsState().value

    LaunchedEffect(true) { viewModel.setEvent(ContractsContract.Event.GetContracts(currency)) }

    Scaffold(
        Modifier.padding(top = 8.dp, bottom = 8.dp),
        topBar = { ContractsToolbar(onNavigationRequested) }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .imePadding()
                .padding(innerPadding)
        ) {
            when {
                state.showProgress != null -> {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                        Arrangement.Center, Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(state.showProgress))
                        Box(Modifier.height(8.dp))
                        CircularProgressIndicator()
                    }
                }
                state.fail?.singly == true -> {
                    Text(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        text = myErrToStr(state.fail),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                    Box(Modifier.height(8.dp))
                    Button(onClick = {
                        viewModel.setEvent(
                            ContractsContract.Event.GetContracts(
                                currency
                            )
                        )
                    }) {
                        Text(text = stringResource(id = R.string.try_again))
                    }
                }
                state.success != null -> {  val contracts = state.success
                    LazyColumn {
                        items(contracts.size) { i ->
                            val expanded = remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
                                ListItem(
                                    modifier = Modifier.clickable {

                                    },
                                    headlineText = { Text(contracts.get(i).firstName ?: "No") },
                                    supportingText = { Text(contracts.get(i).lastName ?: "Name") },
                                    trailingContent = {
                                        Text(contracts.get(i).sum.toBigDecimal().toString(), fontSize = 14.sp)
                                    },
                                    leadingContent = {
                                        IconButton(onClick = { expanded.value = true }) {
                                            Icon(Icons.Filled.MoreVert, stringResource(id = R.string.options_menu))
                                        }
                                    }
                                )
                                Divider()
                                DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false } ) {
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(Icons.Filled.Person, stringResource(id = R.string.tag_friend))
                                        },
                                        text = {
                                            Text(stringResource(id = R.string.tag_friend))
                                        },
                                        onClick = {  }
                                    )
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(Icons.Filled.Edit, stringResource(id = R.string.edit))
                                        },
                                        text = {
                                            Text(stringResource(id = R.string.edit))
                                        },
                                        onClick = {

                                        }
                                    )
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(Icons.Filled.Delete, stringResource(id = R.string.delete))
                                        },
                                        text = {
                                            Text(stringResource(id = R.string.delete))
                                        },
                                        onClick = {

                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContractsToolbar(onNavigationRequested: (nav: ContractsContract.Nav) -> Unit) {
    TopAppBar(
        title = {
            Text(
                stringResource(id = R.string.contracts),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { onNavigationRequested(ContractsContract.Nav.ToBackStack) }) {
                Icon(Icons.Filled.ArrowBack, stringResource(id = R.string.back))
            }
        }
    )
}