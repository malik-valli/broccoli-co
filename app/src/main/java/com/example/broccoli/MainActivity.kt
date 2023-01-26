package com.example.broccoli

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.broccoli.databinding.ActivityMainBinding
import com.example.broccoli.viewmodel.FormViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        val viewModelFactory = FormViewModel.Factory(sharedPreferences)
        viewModel = ViewModelProvider(this, viewModelFactory)[FormViewModel::class.java]

        binding.apply {
            sendButton.setOnClickListener {
                viewModel.sendUser(
                    binding.name.text.toString(),
                    binding.email.text.toString(),
                    binding.confirmEmail.text.toString()
                )
            }
            cancelInviteButton.setOnClickListener {
                showAlert(
                    getString(R.string.cancel_alert_title),
                    getString(R.string.cancel_alert_description)
                ) {
                    viewModel.cancelInvitation()
                    showPopUp(
                        getString(R.string.invitation_cancelled_title),
                        getString(R.string.invitation_cancelled_description),
                        R.layout.image_cancelled
                    )
                }
            }

            name.doOnTextChanged { text, _, _, _ ->
                viewModel.isNameValid(text.toString())
            }
            email.doOnTextChanged { text, _, _, _ ->
                viewModel.areEmailsValid(text.toString(), confirmEmail.text.toString())
            }
            confirmEmail.doOnTextChanged { text, _, _, _ ->
                viewModel.areEmailsValid(email.text.toString(), text.toString())
            }
        }

        viewModel.myResponse.observe(this) {
            if (it != null) {
                if (it.isSuccessful) {
                    showPopUp(
                        getString(R.string.response_successful_title),
                        getString(R.string.response_successful_description),
                        R.layout.image_invited
                    )
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.used_email_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.isInvited.observe(this) {
            viewModel.updateUIbyInvitationStatus()
            viewModel.editor.putBoolean(viewModel.invitedKey, it)
            viewModel.editor.apply()
        }

        viewModel.isLoading.observe(this) {
            if (it) {
                binding.apply {
                    sendButton.text = getString(R.string.sending)
                    sendButton.isEnabled = false
                    nameLayout.isEnabled = false
                    emailLayout.isEnabled = false
                    confirmEmailLayout.isEnabled = false
                }
            } else {
                binding.apply {
                    sendButton.text = getString(R.string.send)
                    sendButton.isEnabled = true
                    nameLayout.isEnabled = true
                    emailLayout.isEnabled = true
                    confirmEmailLayout.isEnabled = true
                }
            }
        }

        viewModel.isVisible.observe(this) {
            binding.form.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.isCancelButtonEnabled.observe(this) {
            binding.cancelInviteButton.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.nameError.observe(this) {
            binding.nameLayout.error = it
            binding.nameLayout.isErrorEnabled = !it.isNullOrEmpty()
        }
        viewModel.emailError.observe(this) {
            binding.emailLayout.error = it
            binding.emailLayout.isErrorEnabled = !it.isNullOrEmpty()
        }
        viewModel.confirmEmailError.observe(this) {
            binding.confirmEmailLayout.error = it
            binding.confirmEmailLayout.isErrorEnabled = !it.isNullOrEmpty()
        }
    }

    private fun showPopUp(messageTitle: String, message: String, viewResId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(messageTitle)
        builder.setMessage(message)
        builder.setView(viewResId)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showAlert(messageTitle: String, message: String, action: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(messageTitle)
            .setMessage(message)
            .setView(R.layout.image_alert)
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Yes") { dialog, _ ->
                action()
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }
}
