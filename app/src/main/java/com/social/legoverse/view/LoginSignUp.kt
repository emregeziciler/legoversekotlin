package com.social.legoverse.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ozgurbaykal.hostmobile.view.LoginStarterFragment
import com.social.legoverse.R
import com.social.legoverse.databinding.LoginSignUpBinding
import com.social.legoverse.util.AppDatabase
import com.social.legoverse.util.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


class LoginSignUp : Fragment(R.layout.login_sign_up) {

    private val TAG = "_LoginSignUp"

    private var _binding: LoginSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var mailOrUsernameEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var passwordAgainEdit: EditText
    private lateinit var createAccountButton: Button
    private lateinit var keepMeLogIn: CheckBox

    private lateinit var goBackText: TextView
    private var keepLogValue: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView()")

        _binding = LoginSignUpBinding.inflate(inflater, container, false)
        val view = binding.root

        mailOrUsernameEdit = binding.mailOrUsername
        passwordEdit = binding.etPassword
        passwordAgainEdit = binding.passwordAgain
        createAccountButton = binding.createAccountButton
        goBackText = binding.goBackText
        keepMeLogIn = binding.keepMeLogCheckBox

        val arrayListEditText = ArrayList<EditText>()
        arrayListEditText.add(mailOrUsernameEdit)
        arrayListEditText.add(passwordEdit)
        arrayListEditText.add(passwordAgainEdit)

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (Character.isWhitespace(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }
        mailOrUsernameEdit.filters = arrayOf<InputFilter>(filter)

        createAccountButton.setOnClickListener {
            if(emptyInputControl(arrayListEditText)){
                Log.i(TAG, "All Inputs OK   1.: " + passwordEdit.text + " 2.: " + passwordAgainEdit.text)

                if(passwordEdit.text.toString() != passwordAgainEdit.text.toString())
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_LONG).show()
                else{
                    //GO MAINACTIVITY

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val database = AppDatabase.getDatabase(requireContext())
                            val dao = database.userDao()

                            keepLogValue = keepMeLogIn.isChecked

                            if(keepLogValue)
                                dao.updateAllUsersKeepLogged(false)

                            val newUser = Users(id= 0, mail = mailOrUsernameEdit.text.toString() , password = passwordEdit.text.toString(), keepLogged = keepLogValue)
                            dao.insert(newUser)


                            val intent = Intent (requireContext(), CreateProfileActivity::class.java)
                            intent.putExtra("user_name_or_mail", mailOrUsernameEdit.text.toString())
                            startActivity(intent)
                            activity?.finish()
                        }catch (e: Exception){
                            e.message

                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(), "Something happened, try again", Toast.LENGTH_LONG).show()
                            }
                        }

                    }

                }
            }else{
                Log.i(TAG, "Some Inputs Empty")
            }
        }

        goBackText.setOnClickListener {
            changeFragment(LoginStarterFragment(), R.id.login_fragment_view, "LoginStarterFragmentTAG")
        }

        return view
    }



    private fun emptyInputControl(editTextArray : ArrayList<EditText>) : Boolean{

        var isAllInputsOk : Boolean = true
        for(editText in editTextArray){
            if(editText.text.isEmpty()){
                val shake: Animation =
                    AnimationUtils.loadAnimation(requireContext(), R.anim.shake_animation)
                editText.startAnimation(shake)
                editText.setBackgroundResource(R.drawable.edittext_border_on_error)
                isAllInputsOk = false
            }else{
                editText.setBackgroundResource(R.drawable.edittext_border)
            }
        }

        if(!isAllInputsOk)
            return false


        return isAllInputsOk
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
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)

    }

}

