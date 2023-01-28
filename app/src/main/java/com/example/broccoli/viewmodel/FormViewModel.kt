package com.example.broccoli.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.example.broccoli.model.User
import com.example.broccoli.model.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class FormViewModel(sharedPref: SharedPreferences) : ViewModel() {

    class Factory(private val sharedPref: SharedPreferences) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return FormViewModel(sharedPref) as T
        }
    }

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
        return isNameValid(name) && areEmailsValid(email, confirmEmail)
    }

    fun isNameValid(name: String?): Boolean {
        _nameError.value = name?.let { nameValidation.validate(it) }
        if (_nameError.value != null)
            return false
        return true
    }

    fun areEmailsValid(email: String?, confirmEmail: String?): Boolean {
        _emailError.value = email?.let { emailValidation.validate(it) }
        _confirmEmailError.value = if (confirmEmail != email) "Emails do not match" else null
        if (_emailError.value != null || _confirmEmailError.value != null)
            return false
        return true
    }

    fun sendUser(name: String?, email: String?, confirmEmail: String?) {

        val valid = validate(name, email, confirmEmail)
        if (valid) {
            val user = User(name!!, email!!)
            viewModelScope.launch {
                _isLoading.value = true
                val response = Repository.sendUser(user)
                _myResponse.value = response
                _isLoading.value = false

                _isInvited.value = response?.isSuccessful
            }
        }
    }

    fun cancelInvitation() {
        _isInvited.value = false
    }
}