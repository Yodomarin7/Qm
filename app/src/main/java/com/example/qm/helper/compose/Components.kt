package com.example.qm.helper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun myErrToStr(myErr: MyErr?): String {
    return when(myErr) {
        is MyErr.Res -> { stringResource(myErr.r) }
        is MyErr.Txt -> { myErr.txt }
        null -> { "" }
    }
}