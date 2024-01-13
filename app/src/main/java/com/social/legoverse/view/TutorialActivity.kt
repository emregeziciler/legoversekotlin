package com.social.legoverse.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ozgurbaykal.hostmobile.view.TutorialPage1
import com.ozgurbaykal.hostmobile.view.TutorialPage2
import com.ozgurbaykal.hostmobile.view.TutorialPage3
import com.ozgurbaykal.hostmobile.view.TutorialPage4
import com.ozgurbaykal.hostmobile.view.TutorialPageListener
import com.social.legoverse.databinding.ActivityTutorialBinding
import com.social.legoverse.manager.SharedPreferenceManager

class TutorialActivity : AppCompatActivity(), TutorialPageListener {
    private val TAG = "_TutorialActivity"

    private lateinit var viewPager : ViewPager2

    private lateinit var binding: ActivityTutorialBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        viewPager = binding.tutorialViewPager
        viewPager.adapter = TutorialPagerAdapter(this)

        SharedPreferenceManager.writeBoolean("isNewUser", false)
    }

    override fun onNextPage() {
        val currentItem = viewPager.currentItem
        if (currentItem < 4) {
            viewPager.currentItem = currentItem + 1
        }
    }

    override fun skipButton() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

class TutorialPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TutorialPage1()
            1 -> TutorialPage2()
            2 -> TutorialPage3()
            3 -> TutorialPage4()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}