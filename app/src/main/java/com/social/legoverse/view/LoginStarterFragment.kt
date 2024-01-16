package com.ozgurbaykal.hostmobile.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.social.legoverse.R
import com.social.legoverse.databinding.LoginStarterFragmentBinding
import com.social.legoverse.view.LoginSignIn
import com.social.legoverse.view.LoginSignUp
import java.lang.Exception

class LoginStarterFragment : Fragment(R.layout.login_starter_fragment) {

    private val TAG = "_LoginStarterFragment"

    private var _binding: LoginStarterFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var buttonSignIn: Button
    private lateinit var buttonSignUp: Button
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView()")

        _binding = LoginStarterFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        buttonSignIn = binding.buttonSignIn
        buttonSignUp = binding.buttonSignUp

        buttonSignIn.setOnClickListener {
            changeFragment(LoginSignIn(), R.id.login_fragment_view, "LoginSignInTAG")
        }

        buttonSignUp.setOnClickListener {
            changeFragment(LoginSignUp(), R.id.login_fragment_view, "LoginSignUpTAG")
        }

        return view
    }
    private fun changeFragment(fragment: Fragment, frameId: Int, tag: String) {
        try {
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(frameId, fragment, tag)
                addToBackStack(tag)
                commit()
            }
        }catch (e: Exception){
            e.message
        }
    }
    override fun onResume() {
        super.onResume()
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.main_yellow)

    }

}

