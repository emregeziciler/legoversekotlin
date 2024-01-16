package com.social.legoverse.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.social.legoverse.R
import com.social.legoverse.databinding.FragmentTutorialPage1Binding
import com.social.legoverse.databinding.HomeFragmentBinding
import com.social.legoverse.databinding.LoginStarterFragmentBinding
import com.social.legoverse.util.AppDatabase
import com.social.legoverse.util.Post
import com.social.legoverse.util.UserNameOrMail
import com.social.legoverse.view.PostAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeFragment : Fragment(R.layout.home_fragment) {

    private val TAG = "_HomeFragment"

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var nothingSharedTextView: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        val postDao = AppDatabase.getDatabase(requireContext()).postDao()

        CoroutineScope(Dispatchers.IO).launch {
            val posts = postDao.getAll()

            val postAdapter = PostAdapter(posts, requireContext())
            recyclerView.adapter = postAdapter

            if(posts.isEmpty())
                nothingSharedTextView.visibility = View.VISIBLE
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView()")

        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = binding.recyclerViewPosts
        nothingSharedTextView = binding.nothingSharedText
        return view
    }

    override fun onResume() {
        super.onResume()

    }

}

