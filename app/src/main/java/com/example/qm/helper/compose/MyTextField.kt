@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.qm.helper.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.example.qm.R

@Composable
fun MyTextField(
    isError: MutableState<Boolean>,
    supportingText: MutableState<Int>,
    textFieldValue: MutableState<TextFieldValue>,
    validate: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        singleLine = true,
        isError = isError.value,
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
        modifier = Modifier.fillMaxWidth(),
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
}