package com.example.broccoli.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.broccoli.model.User
import com.example.broccoli.model.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class FormViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPref = application.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPref.edit()
    val invitedKey = "is_invited"
    private val _isInvited = MutableLiveData(sharedPref.getBoolean(invitedKey, false))
    val isInvited: LiveData<Boolean> = _isInvited

    private val _myResponse: MutableLiveData<Response<String>> = MutableLiveData()
    val myResponse: LiveData<Response<String>> = _myResponse

    private val _isCancelButtonEnabled = MutableLiveData(sharedPref.getBoolean(invitedKey, false))
    val isCancelButtonEnabled: LiveData<Boolean> = _isCancelButtonEnabled

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isVisible = MutableLiveData(true)
    val isVisible: LiveData<Boolean> = _isVisible

    fun updateUIbyInvitationStatus() {
        _isCancelButtonEnabled.value = _isInvited.value
        _isVisible.value = !_isInvited.value!!
    }

    private val nameValidation = NameValidation(3)
    private val emailValidation = EmailValidation()

    private val _nameError = MutableLiveData<String>()
    val nameError: LiveData<String> = _nameError
    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String> = _emailError
    private val _confirmEmailError = MutableLiveData<String?>()
    val confirmEmailError: LiveData<String?> = _confirmEmailError

    private fun validate(name: String?, email: String?, confirmEmail: String?): Boolean {

        var isValid = true

        _nameError.value = name?.let { nameValidation.validate(it) }
        if (_nameError.value != null)
            isValid = false

        _emailError.value = email?.let { emailValidation.validate(it) }
        if (_emailError.value != null)
            isValid = false

        if (confirmEmail != email) {
            _confirmEmailError.value = "Emails do not match"
            isValid = false
        } else {
            _confirmEmailError.value = null
        }

        return isValid
    }

    fun submitUser(name: String?, email: String?, confirmEmail: String?) {

        val valid = validate(name, email, confirmEmail)
        if (valid) {
            val user = User(name!!, email!!)
            viewModelScope.launch {
                _isLoading.value = true
                val response = Repository.sendUser(user)
                _myResponse.value = response
                _isLoading.value = false

                _isInvited.value = response.isSuccessful
            }
        }
    }

    fun cancelInvitation() {
        _isInvited.value = false
    }
}