package com.example.broccoli.viewmodel

import android.util.Patterns

class NameValidator(val length: Int) {

    fun validate(value: String): Boolean = value.length < length
}

object EmailValidator {

    private val email = Patterns.EMAIL_ADDRESS

    fun validate(value: String): Boolean = email.matcher(value).matches()
}


