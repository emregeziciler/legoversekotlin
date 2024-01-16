package com.social.legoverse.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.social.legoverse.R
import com.social.legoverse.databinding.ActivityMainBinding
import com.social.legoverse.util.AppDatabase
import com.social.legoverse.util.UserNameOrMail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val TAG = "_MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var topBar : Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private var mailOrUserName: String = ""
    var isLegoVerseYellow = true
    var isFollowYellow = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)


        bottomNav = binding.bottomNavigation

        drawerLayout = binding.drawerLayout
        topBar = binding.toolbar
        navigationView = binding.navView

        setSupportActionBar(topBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24)

        topBar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)


        }
        navigationView.itemIconTintList = null


        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logOutAccount -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val database = AppDatabase.getDatabase(this@MainActivity)
                        val dao = database.userDao()

                        dao.updateAllUsersKeepLogged(false)

                        val intent = Intent (this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }

                R.id.notifLegoVerse -> {
                    isLegoVerseYellow = !isLegoVerseYellow
                    if (isLegoVerseYellow) {
                        menuItem.icon = ContextCompat.getDrawable(this, R.drawable.lego_yellow)
                    } else {
                        menuItem.icon = ContextCompat.getDrawable(this, R.drawable.lego_green)
                    }
                    true
                }

                R.id.notifFollow -> {
                    isFollowYellow = !isFollowYellow
                    if (isFollowYellow) {
                        menuItem.icon = ContextCompat.getDrawable(this, R.drawable.follow_yellow)
                    } else {
                        menuItem.icon = ContextCompat.getDrawable(this, R.drawable.follow_green)
                    }
                    true
                }


                else -> false
            }
        }


        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                val userDao = AppDatabase.getDatabase(this@MainActivity).userDao()
                val userNameMenuItem = navigationView.menu.findItem(R.id.UserName)

                CoroutineScope(Dispatchers.IO).launch {
                    val currentUser = userDao.findUserByUsernameOrEmail(UserNameOrMail.userNameOrMail)
                    withContext(Dispatchers.Main) {
                        if (currentUser != null) {
                            userNameMenuItem.title = currentUser.userName
                        }

                        if(currentUser?.profileImage != null){

                            Log.i(TAG, "(currentUser?.profileImage != null)")
                            val bitmapProfile = byteArrayToBitmap(currentUser.profileImage)
                            val drawableProfile = BitmapDrawable(resources, bitmapProfile)

                            drawableProfile.clearColorFilter()
                            userNameMenuItem.icon = drawableProfile
                        }

                    }
                }



            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        }
        )


        bottomNav.setOnItemSelectedListener  {item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    Log.i(TAG, "Bottom Nav ClickEvent -> PAGE 1")
                    changeFragment(HomeFragment(), R.id.mainActivityContainer, "CustomFragmentTag")
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_search -> {
                    Log.i(TAG, "Bottom Nav ClickEvent -> PAGE 2")

                    changeFragment(SearchFragment(), R.id.mainActivityContainer, "SearchFragmentTag")

                    return@setOnItemSelectedListener true
                }
                R.id.navigation_add -> {
                    Log.i(TAG, "Bottom Nav ClickEvent -> PAGE 3")

                    mailOrUserName = intent.getStringExtra("user_name_or_mail").toString()

                    val intent = Intent (this@MainActivity, AddPostActivity::class.java)
                    startActivity(intent)

                    return@setOnItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    Log.i(TAG, "Bottom Nav ClickEvent -> PAGE 4")

                    val intent = Intent (this@MainActivity, ProfileActivity::class.java)
                    startActivity(intent)

                    return@setOnItemSelectedListener true
                }
            }
            false
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
        bottomNav.selectedItemId = R.id.navigation_home;
        changeFragment(HomeFragment(), R.id.mainActivityContainer,"HomeFragmentTAG")

    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}