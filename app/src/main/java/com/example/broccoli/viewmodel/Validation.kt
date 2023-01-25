package com.example.broccoli.viewmodel

import android.util.Patterns

class NameValidation(private val length: Int) {

    fun validate(value: String): String? {
        return if (value.length < length) {
            "Must be at least $length characters long"
        } else {
            null
        }
    }
}

class EmailValidation {

    private val email = Patterns.EMAIL_ADDRESS

    fun validate(value: String): String? {
        return if (!email.matcher(value).matches()) {
            "Invalid email format"
        } else {
            null
        }
    }
}


