package com.example.domain.usecase

import com.example.domain.repository.Iauth

class IsAuthUC(
    private val authR: Iauth
) {

    suspend fun run(): Boolean {
        return authR.isAuth()
    }

}