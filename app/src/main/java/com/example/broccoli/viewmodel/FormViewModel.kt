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

    fun updateForm() {
        _isCancelButtonEnabled.value = _isInvited.value
        _isVisible.value = !_isInvited.value!!
    }

    val nameValidator = NameValidator(length = 3)

    private val _isNameValid = MutableLiveData<Boolean>()
    val isNameValid: LiveData<Boolean> = _isNameValid
    private val _isEmailValid = MutableLiveData<Boolean>()
    val isEmailValid: LiveData<Boolean> = _isEmailValid
    private val _isEmailConfirmed = MutableLiveData<Boolean>()
    val isEmailConfirmed: LiveData<Boolean> = _isEmailConfirmed

    private fun validate(name: String?, email: String?, confirmEmail: String?): Boolean {
        return isNameValid(name) && isEmailValidAndConfirmed(email, confirmEmail)
    }

    fun isNameValid(name: String?): Boolean {
        _isNameValid.value = name?.let { !nameValidator.validate(it) }
        return isNameValid.value!!
    }

    fun isEmailValidAndConfirmed(email: String?, confirmEmail: String?): Boolean {
        _isEmailValid.value = email?.let { EmailValidator.validate(it) }
        _isEmailConfirmed.value = confirmEmail == email
        return isEmailValid.value!! && isEmailConfirmed.value!!
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