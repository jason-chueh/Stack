package com.example.stack.login

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.stack.MainViewModel
import com.example.stack.NavigationDirections
import com.example.stack.R
import com.example.stack.databinding.DialogLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginDialog : AppCompatDialogFragment() {

    /**
     * Lazily initialize our [LoginViewModel].
     */
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: DialogLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.LoginDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogLoginBinding.inflate(inflater, container, false)

        binding.layoutLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_up))

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        binding.buttonLoginNative.setOnClickListener {
            if(!binding.editLoginName.text.isNullOrBlank()){
                viewModel.nativeLogin(binding.editLoginName.text.toString())
            }
        }

        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        viewModel.user.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                mainViewModel.setupUser(it)
            }
        }

        viewModel.navigateToLoginSuccess.observe(
            viewLifecycleOwner
        ) {
            it?.let {
//                    mainViewModel.navigateToLoginSuccess(it)
                dismiss()
            }
        }


        viewModel.registerErrorToast.observe(
            viewLifecycleOwner
        ) {
            if (it) {
                Toast.makeText(
                    this.context,
                    "Register Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
                viewModel.onRegisterErrorToastCompleted()
            }
        }


        viewModel.leave.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                dismiss()
                viewModel.onLeaveCompleted()
                findNavController().navigate(NavigationDirections.navigateToHomeFragment())
            }
        }


        return binding.root
    }

    override fun dismiss() {
        binding.layoutLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_down))
        Handler().postDelayed({ super.dismiss() }, 200)
    }

}
