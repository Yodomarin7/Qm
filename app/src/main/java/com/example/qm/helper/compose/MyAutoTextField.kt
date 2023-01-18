@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.qm.helper.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.PopupProperties
import com.example.qm.R

@Composable
fun MyAutoTextField(
    isError: MutableState<Boolean>,
    supportingText: MutableState<Int>,
    textFieldValue: MutableState<TextFieldValue>,
    validate: (String) -> Unit,
    label: String,
    optionsSize: MutableState<Int>,
    OnOptionClick: (pos: Int) -> String,
    optionText: @Composable (pos: Int) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val expanded: MutableState<Boolean> = remember{ mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().onFocusEvent {
                expanded.value = it.isFocused
            },
            isError = isError.value,
            leadingIcon = { Icon(Icons.Filled.Search, stringResource(id = R.string.search)) },
            trailingIcon = {
                if(textFieldValue.value.text.isNotEmpty()) {
                    IconButton(onClick = {
                        textFieldValue.value = TextFieldValue(text = "", selection = TextRange("".length))
                        validate("")
                    }) {
                        Icon(Icons.Filled.Clear, stringResource(id = R.string.clear))
                    }
                }
            },
            singleLine = true,
            value = textFieldValue.value,
            supportingText = { Text(stringResource(id = supportingText.value)) },
            onValueChange = {
                textFieldValue.value = TextFieldValue(text = it.text, selection = TextRange(it.text.length))
                validate(it.text)
            },
            label = { Text(label) },
            keyboardOptions = keyboardOptions,
            keyboardActions = onKeyboardActions
        )
        DropdownMenu(
            expanded = expanded.value,
            properties = PopupProperties(focusable = false),
            onDismissRequest = {
                //expanded.value = false
            },
            //content = popupMenu
        ) {
//            for(i in 1..optionsSize.value) {
//                DropdownMenuItem(
//                    onClick = { textFieldValue.value = TextFieldValue(OnOptionClick(i)) },
//                    text = { optionText(i) }
//                )
//            }

            for(i in 0 until optionsSize.value) {
                DropdownMenuItem(
                    onClick = {
                        val txt = OnOptionClick(i)
                        textFieldValue.value = TextFieldValue(text = txt, selection = TextRange(txt.length))
                        validate(txt)
                    },
                    text = { optionText(i) }
                )
            }
        }
    }
}



