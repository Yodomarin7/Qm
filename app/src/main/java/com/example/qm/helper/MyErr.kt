package com.example.qm.helper

import com.example.qm.R

sealed class MyErr(val singly: Boolean = false, val toast: Boolean = false) {
    class Txt(val txt: String, singly: Boolean = false, toast: Boolean = false): MyErr(singly, toast)
    class Res(val r: Int, singly: Boolean = false, toast: Boolean = false): MyErr(singly, toast)
}

fun expToMyErr(e: Exception, singly: Boolean = false, toast: Boolean = false): MyErr {
    var err: MyErr = MyErr.Res(R.string.error_try_again, singly, toast)
    if(e.message != null) err = MyErr.Txt(e.message!!, singly, toast)
    return err
}

fun strToMyErr(str: String?, singly: Boolean = false, toast: Boolean = false): MyErr {
    var err: MyErr = MyErr.Res(R.string.error_try_again, singly, toast)
    if(str != null) err = MyErr.Txt(str, singly, toast)
    return err
}

fun isFail(myErr: MyErr?): Boolean {
    return when(myErr) {
        is MyErr.Res -> { true }
        is MyErr.Txt -> { myErr.txt.isNotEmpty() }
        null -> { false }
    }
}



















