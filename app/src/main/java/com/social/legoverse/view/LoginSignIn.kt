package com.ozgurbaykal.hostmobile.view

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
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.social.legoverse.R
import com.social.legoverse.databinding.FragmentTutorialPage1Binding
import com.social.legoverse.databinding.LoginSignInBinding
import com.social.legoverse.databinding.LoginStarterFragmentBinding
import com.social.legoverse.util.AppDatabase
import com.social.legoverse.util.UserNameOrMail
import com.social.legoverse.util.Users
import com.social.legoverse.view.CreateProfileActivity
import com.social.legoverse.view.MainActivity
import com.social.legoverse.view.TutorialActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginSignIn : Fragment(R.layout.login_sign_in) {

    private val TAG = "_LoginSignIn"

    private var _binding: LoginSignInBinding? = null
    private val binding get() = _binding!!


    private lateinit var mailOrUsernameEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var loginButton: Button
    private lateinit var goBackText: TextView
    private lateinit var keepMeLogIn: CheckBox


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView()")

        _binding = LoginSignInBinding.inflate(inflater, container, false)
        val view = binding.root


        mailOrUsernameEdit = binding.mailOrUsername
        passwordEdit = binding.etPassword
        loginButton = binding.loginButton
        goBackText = binding.goBackText
        keepMeLogIn = binding.keepMeLogCheckBox

        val arrayListEditText = ArrayList<EditText>()
        arrayListEditText.add(mailOrUsernameEdit)
        arrayListEditText.add(passwordEdit)

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (Character.isWhitespace(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }
        mailOrUsernameEdit.filters = arrayOf<InputFilter>(filter)

        loginButton.setOnClickListener {
            if(emptyInputControl(arrayListEditText)){


                    //GO MAINACTIVITY

                    CoroutineScope(Dispatchers.IO).launch {
                        val database = AppDatabase.getDatabase(requireContext())
                        val dao = database.userDao()
                        var isUserExist = dao.getSpecificUser(mailOrUsernameEdit.text.toString(), passwordEdit.text.toString())

                        if(isUserExist != null){
                            Log.i(TAG, "LOGIN SUCCESS")

                            if(keepMeLogIn.isChecked){
                                dao.updateAllUsersKeepLogged(false)

                                var updatedUser: Users = isUserExist.copy(
                                    keepLogged = true
                                )

                                dao.update(updatedUser)

                            }

                            if(isUserExist.userName != null){
                                UserNameOrMail.userNameOrMail = mailOrUsernameEdit.text.toString()

                                val intent = Intent (requireContext(), MainActivity::class.java)
                                startActivity(intent)
                                activity?.finish()
                            }else{
                                val intent = Intent (requireContext(), CreateProfileActivity::class.java)
                                intent.putExtra("user_name_or_mail", isUserExist.mail.toString())
                                startActivity(intent)
                                activity?.finish()
                            }



                        }else{
                            Log.i(TAG, "LOGIN ERROR")
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(), "WRONG PASSWORD OR USERNAME, TRY AGAIN", Toast.LENGTH_LONG).show()
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

