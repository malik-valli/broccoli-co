package com.example.broccoli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.broccoli.databinding.ActivityMainBinding
import com.example.broccoli.viewmodel.FormViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: FormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            submitButton.setOnClickListener {
                viewModel.submitUser(
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
                        getString(R.string.invitation_canceled_title),
                        getString(R.string.invitation_canceled_description),
                        R.layout.image_cancelled
                    )
                }
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
                    submitButton.text = getString(R.string.submitting)
                    nameLayout.isEnabled = false
                    emailLayout.isEnabled = false
                    confirmEmailLayout.isEnabled = false
                }
            } else {
                binding.apply {
                    submitButton.text = getString(R.string.submit)
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
        }
        viewModel.emailError.observe(this) {
            binding.emailLayout.error = it
        }
        viewModel.confirmEmailError.observe(this) {
            binding.confirmEmailLayout.error = it
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
