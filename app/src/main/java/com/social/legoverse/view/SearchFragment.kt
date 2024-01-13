package com.ozgurbaykal.hostmobile.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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
import com.social.legoverse.databinding.SearchFragmentBinding
import com.social.legoverse.util.AppDatabase
import com.social.legoverse.util.Post
import com.social.legoverse.util.UserNameOrMail
import com.social.legoverse.view.PostAdapter
import com.social.legoverse.view.SearchAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class SearchFragment : Fragment(R.layout.search_fragment) {

    private val TAG = "_SearchFragment"

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        val postDao = AppDatabase.getDatabase(requireContext()).postDao()

        CoroutineScope(Dispatchers.IO).launch {
            val posts = postDao.getAll()

            val searchAdapter = SearchAdapter(posts, requireContext())
            recyclerView.adapter = searchAdapter
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    performSearch(it.toString())
                }
            }
        })
    }

    private fun performSearch(query: String) {
        val postDao = AppDatabase.getDatabase(requireContext()).postDao()

        CoroutineScope(Dispatchers.IO).launch {
            val filteredPosts = postDao.getAll().filter {
                it.projectName?.contains(query, ignoreCase = true) ?: false
            }

            withContext(Dispatchers.Main) {
                (recyclerView.adapter as? SearchAdapter)?.filterList(filteredPosts)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView()")

        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = binding.recyclerViewSearch
        searchEditText = binding.searchEditText

        return view
    }

    override fun onResume() {
        super.onResume()

    }

}

