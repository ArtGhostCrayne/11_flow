package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentLoginBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.LoginViewModel

class LoginFragment : Fragment() {


    private val loginViewModel: LoginViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)


        binding.login.setOnClickListener {
            binding.errorText.isVisible = false

            AndroidUtils.hideKeyboard(binding.root)

            val login = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (login.isBlank() || password.isBlank()) {
                binding.errorText.text = "Заполните поля ввода"
                binding.errorText.isVisible = true
            } else
                loginViewModel.signIn(login, password)

        }

        loginViewModel.dataState.observe(viewLifecycleOwner) {
            binding.errorText.isVisible = it.error
            binding.errorText.text = it.errorText
            binding.progress.isVisible = it.loading

            if (it.success) {
                Toast.makeText(context, "Добро пожаловать, " + it.login, Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
                loginViewModel.clean()
            }

        }

        return binding.root

    }


}
