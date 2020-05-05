package com.saitejajanjirala.iniestanewsapp.utils

class EmailValidator {
        fun isEmailValid(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
}