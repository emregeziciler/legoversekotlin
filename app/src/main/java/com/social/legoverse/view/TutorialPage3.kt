package com.ozgurbaykal.hostmobile.view

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.social.legoverse.R
import com.social.legoverse.databinding.FragmentTutorialPage2Binding
import com.social.legoverse.databinding.FragmentTutorialPage3Binding


class TutorialPage3 : Fragment(R.layout.fragment_tutorial_page3) {

    private val TAG = "_TutorialPage3"

    private lateinit var listener: TutorialPageListener

    private var _binding: FragmentTutorialPage3Binding? = null
    private val binding get() = _binding!!

    private lateinit var buttonNext : ImageButton
    private lateinit var skipButton : TextView
    private lateinit var buttonProgressBar : ProgressBar

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is TutorialPageListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement TutorialPageListener")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView()")

        _binding = FragmentTutorialPage3Binding.inflate(inflater, container, false)
        val view = binding.root
        buttonNext = binding.buttonNext
        skipButton = binding.skipButton
        buttonProgressBar = binding.progressButton

        val drawable = DrawableCompat.wrap(buttonNext.background)
        DrawableCompat.setTint(drawable, ContextCompat.getColor(requireContext(), R.color.main_green))
        buttonNext.background = drawable

        val layerDrawable = buttonProgressBar.progressDrawable as LayerDrawable
        val progressLayer = layerDrawable.findDrawableByLayerId(android.R.id.progress)

        val yeniRenk = ContextCompat.getColor(requireContext(), R.color.main_red)
        progressLayer.setColorFilter(yeniRenk, PorterDuff.Mode.SRC_IN)

        buttonProgressBar.progress = 75

        buttonNext.setOnClickListener {
            Log.i(TAG, "buttonNext CLICKED")
            listener.onNextPage()
        }

        skipButton.setOnClickListener {
            listener.skipButton()
        }

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.main_green)

        return view
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.main_green)

    }
}

