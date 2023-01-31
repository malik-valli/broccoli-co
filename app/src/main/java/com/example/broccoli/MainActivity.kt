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
                viewModel.isEmailValidAndConfirmed(text.toString(), confirmEmail.text.toString())
            }
            confirmEmail.doOnTextChanged { text, _, _, _ ->
                viewModel.isEmailValidAndConfirmed(email.text.toString(), text.toString())
            }
        }

        viewModel.myResponse.observe(this) {
            if (it != null) {
                if (it.isSuccessful) {
                    showPopUp(
                        getString(R.string.invitation_sent_title),
                        getString(R.string.invitation_sent_description),
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
            viewModel.updateForm()
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

        viewModel.isNameValid.observe(this) {
            binding.nameLayout.error = if (!it) getString(
                R.string.name_error,
                viewModel.nameValidator.length
            ) else null
            binding.nameLayout.isErrorEnabled = !it
        }
        viewModel.isEmailValid.observe(this) {
            binding.emailLayout.error = if (!it) getString(R.string.email_error) else null
            binding.emailLayout.isErrorEnabled = !it
        }
        viewModel.isEmailConfirmed.observe(this) {
            binding.confirmEmailLayout.error =
                if (!it) getString(R.string.email_confirmation_error) else null
            binding.confirmEmailLayout.isErrorEnabled = !it
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
