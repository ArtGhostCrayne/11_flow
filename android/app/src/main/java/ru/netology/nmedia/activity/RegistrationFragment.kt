package ru.netology.nmedia.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.RegistrationViewModel
import ru.netology.nmedia.databinding.FragmentRegistrationBinding
import ru.netology.nmedia.model.PhotoModel


class RegistrationFragment : Fragment() {


    private val registrationViewModel: RegistrationViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegistrationBinding.inflate(inflater, container, false)


        binding.bRegistration.setOnClickListener {
            binding.errorText.isVisible = false

            AndroidUtils.hideKeyboard(binding.root)

            val login = binding.login.text.toString().trim()
            val username = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val password2 = binding.password2.text.toString().trim()


            if (login.isBlank() || password.isBlank() || username.isBlank() || password2.isBlank()) {
                binding.errorText.text = "Заполните поля ввода"
                binding.errorText.isVisible = true
            } else
                if (password != password2) {
                    binding.errorText.text = "Пароли не совпадают"
                    binding.errorText.isVisible = true
                } else
                    registrationViewModel.registration(login, password, username, )

        }

        registrationViewModel.dataState.observe(viewLifecycleOwner) {
            binding.errorText.isVisible = it.error
            binding.errorText.text = it.errorText
            binding.progress.isVisible = it.loading

            if (it.success) {
                Toast.makeText(context, "Добро пожаловать, " + it.login, Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
                registrationViewModel.changePhoto(null, null)
                registrationViewModel.clean()
            }

        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        registrationViewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }

        registrationViewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.tChangeAvatar.visibility = View.VISIBLE
                return@observe
            }

             binding.tChangeAvatar.visibility = View.GONE
            binding.photo.setImageURI(it.uri)
        }

        binding.photo.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        return binding.root

    }

}
