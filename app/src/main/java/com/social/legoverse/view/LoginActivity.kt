package com.social.legoverse.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.ozgurbaykal.hostmobile.view.LoginStarterFragment
import com.social.legoverse.R
import com.social.legoverse.databinding.ActivityLoginBinding
import com.social.legoverse.manager.SharedPreferenceManager
import com.social.legoverse.util.AppDatabase
import com.social.legoverse.util.UserNameOrMail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val TAG = "_LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        SharedPreferenceManager.init(this@LoginActivity)

        if(SharedPreferenceManager.readBoolean("isNewUser", true) == true){
            val intent = Intent (this@LoginActivity, TutorialActivity::class.java)
            startActivity(intent)
        }
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(this@LoginActivity)
            val dao = database.userDao()

            if (dao.getLoggedInUser()?.keepLogged == true){
                if(dao.getLoggedInUser()?.userName != null){
                    UserNameOrMail.userNameOrMail = dao.getLoggedInUser()?.mail.toString()

                    val intent = Intent (this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent (this@LoginActivity, CreateProfileActivity::class.java)
                    intent.putExtra("user_name_or_mail", dao.getLoggedInUser()?.mail.toString())
                    startActivity(intent)
                    finish()
                }
            }
        }

      if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<LoginStarterFragment>(R.id.login_fragment_view, "LoginStarterFragmentTAG")
            }
        }


    }

    private fun changeFragment(fragment: Fragment, frameId: Int, tag: String) {
        supportFragmentManager.beginTransaction().apply {
            replace(frameId, fragment, tag)
            commit()
        }
    }

    override fun onResume() {
        super.onResume()


    }
}