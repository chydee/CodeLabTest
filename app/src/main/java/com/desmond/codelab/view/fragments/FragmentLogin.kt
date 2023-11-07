package com.desmond.codelab.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desmond.codelab.R
import com.desmond.codelab.core.SettingsContext
import com.desmond.codelab.databinding.FragmentLoginBinding
import com.desmond.codelab.domain.body.LoginBody
import com.desmond.codelab.domain.response.LoginResponse
import com.desmond.codelab.view.vm.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentLogin : Fragment() {

    @Inject
    lateinit var settingsContext: SettingsContext
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by activityViewModels<MainViewModel>()
    private var isRememberMe = false
    private var username = ""
    private var password = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (settingsContext.isRememberMe) {
            binding.inputUsername.setText(settingsContext.userName)
            binding.inputPassword.setText(settingsContext.password)
            binding.cbRememberMe.isChecked = true
        }
        setClickListeners()
        observeLiveData()
    }

    private fun setClickListeners() {
        binding.btnLogin.setOnClickListener {
            verifyInput()
        }
        binding.btnForgotPass.setOnClickListener {
            Toast.makeText(requireContext(), "use: 0lelplR", Toast.LENGTH_LONG).show()
        }
        binding.cbRememberMe.setOnCheckedChangeListener { buttonView, isChecked ->
            isRememberMe = isChecked
        }
    }

    private fun verifyInput() {
        username = binding.inputUsername.text.toString()
        password = binding.inputPassword.text.toString()

        if (username.isEmpty()) {
            binding.usernameInputLayout.error = "invalid username!"
        } else if (password.isEmpty()) {
            binding.passwordInputLayout.error = "wrong password!"
        } else {
            showProgress()
            val body = LoginBody(username, password)
            performLogin(body)
        }
    }

    private fun performLogin(body: LoginBody) {
        viewModel.authenticateUser(body)
    }

    private fun observeLiveData() {
        viewModel.userLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                updateProgress()
                persistUserData(it)
                findNavController().navigate(R.id.action_fragmentLogin_to_fragmentProfile)
            }
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            updateProgress()
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun persistUserData(data: LoginResponse) {
        settingsContext.id = data.id
        settingsContext.userName = data.username
        settingsContext.email = data.email
        settingsContext.avatar = data.image
        settingsContext.gender = data.gender
        settingsContext.token = data.token
        settingsContext.firstName = data.firstName
        settingsContext.lastName = data.lastName
        settingsContext.isRememberMe = binding.cbRememberMe.isChecked
        viewModel.setUserData(data)
        saveCredentials()
    }

    private fun saveCredentials() {
        if (isRememberMe) {
            settingsContext.password = password
            settingsContext.userName = username
        }
    }

    private fun showProgress() {
        binding.btnLogin.apply {
            isEnabled = false
            text = getString(R.string.please_wait)
        }
    }

    private fun updateProgress() {
        binding.btnLogin.apply {
            isEnabled = true
            text = getString(R.string.btn_login_text)
        }
    }

}