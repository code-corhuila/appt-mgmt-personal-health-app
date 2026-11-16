package com.saludactiva.personalhealth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.saludactiva.personalhealth.R
import com.saludactiva.personalhealth.data.ProfileInputValidator
import com.saludactiva.personalhealth.data.ProfileUser
import com.saludactiva.personalhealth.databinding.DialogEditProfileBinding
import com.saludactiva.personalhealth.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.setOnMenuItemClickListener(::onMenuItemSelected)
        binding.editProfileButton.setOnClickListener {
            viewModel.user.value?.let(::showEditDialog)
                ?: Snackbar.make(binding.root, "No se encontraron datos de perfil.", Snackbar.LENGTH_LONG).show()
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) bindUser(user)
        }
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.profileProgress.visibility = if (loading) View.VISIBLE else View.GONE
            binding.editProfileButton.isEnabled = !loading
        }
        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
        viewModel.sessionExpired.observe(viewLifecycleOwner) { expired ->
            if (expired) {
                Snackbar.make(binding.root, R.string.session_expired, Snackbar.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            }
        }
    }

    private fun bindUser(user: ProfileUser) {
        binding.profileName.text = user.nombre
        binding.profileNameValue.text = user.nombre
        binding.profileEmailValue.text = user.email
        binding.profilePhoneValue.text = user.telefono?.takeIf(String::isNotBlank) ?: "—"
        binding.avatarInitials.text = ProfileInputValidator.initials(user.nombre).ifBlank { "SA" }
    }

    private fun onMenuItemSelected(item: MenuItem): Boolean {
        if (item.itemId != R.id.action_logout) return false
        viewModel.signOut()
        return true
    }

    private fun showEditDialog(user: ProfileUser) {
        val form = DialogEditProfileBinding.inflate(layoutInflater)
        form.nameInput.setText(user.nombre)
        form.phoneInput.setText(user.telefono.orEmpty())

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_profile)
            .setView(form.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = form.nameInput.text?.toString().orEmpty()
                val phone = form.phoneInput.text?.toString().orEmpty()
                val validationMessage = ProfileInputValidator.validateName(name)
                    ?: ProfileInputValidator.validatePhone(phone)
                if (validationMessage != null) {
                    Snackbar.make(form.root, validationMessage, Snackbar.LENGTH_LONG).show()
                } else {
                    viewModel.updateProfile(name, phone)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
