package com.social.legoverse.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ozgurbaykal.hostmobile.view.HomeFragment
import com.social.legoverse.R
import com.social.legoverse.databinding.ActivityMainBinding
import com.social.legoverse.databinding.ActivityViewPostBinding
import com.social.legoverse.util.AppDatabase
import com.social.legoverse.util.Comment
import com.social.legoverse.util.UserNameOrMail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewPostActivity : AppCompatActivity() {
    private val TAG = "_ViewPostActivity"

    private lateinit var binding: ActivityViewPostBinding

    private lateinit var toolbar: Toolbar
    private var postIdFromIntent: Int = -1

    private lateinit var postImage: ImageView
    private lateinit var postDescription: TextView
    private lateinit var postProjectName: TextView
    private lateinit var imageViewProfile: ImageView
    private lateinit var sendCommentButton: ImageView
    private lateinit var commentEdit: EditText

    private lateinit var recyclerView: RecyclerView

    private lateinit var commentAdapter: CommentAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewPostBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        toolbar = binding.toolbar

        postImage = binding.postImage
        postDescription = binding.textViewPostContent
        postProjectName = binding.textViewProjectName
        imageViewProfile = binding.imageViewProfile
        recyclerView = binding.recyclerViewPosts
        sendCommentButton = binding.sendCommentIcon
        commentEdit = binding.commentEdit

        postIdFromIntent = intent.getIntExtra("post_id", -1)

        if(postIdFromIntent == -1){
            Toast.makeText(this@ViewPostActivity, "Something happened, try again", Toast.LENGTH_LONG).show()
            finish()
        }else{
            val postDao = AppDatabase.getDatabase(this@ViewPostActivity).postDao()
            val userDao = AppDatabase.getDatabase(this@ViewPostActivity).userDao()
            val commentDao = AppDatabase.getDatabase(this@ViewPostActivity).commentDao()

            CoroutineScope(Dispatchers.IO).launch {
                Log.i(TAG, "")
                val post = postDao.getPostsById(postIdFromIntent)
                val currentUserId = userDao.findUserByUsernameOrEmail(UserNameOrMail.userNameOrMail)?.profileImage

                withContext(Dispatchers.Main) {
                    postDescription.text = post.content
                    postProjectName.text = post.projectName

                    if(post.image != null){
                        val bitmapPost = byteArrayToBitmap(post.image)
                        postImage.setImageBitmap(bitmapPost)
                    }
                    if(currentUserId != null){
                        val bitmapProfile = byteArrayToBitmap(currentUserId)
                        imageViewProfile.setImageBitmap(bitmapProfile)
                    }


                }
            }


            recyclerView.layoutManager = LinearLayoutManager(this@ViewPostActivity)


            CoroutineScope(Dispatchers.IO).launch {
                val currentUserId = userDao.findUserByUsernameOrEmail(UserNameOrMail.userNameOrMail)?.id

                if(currentUserId != null){
                    val comments = commentDao.getAll(postIdFromIntent, currentUserId)

                    commentAdapter = CommentAdapter(comments, this@ViewPostActivity)
                    recyclerView.adapter = commentAdapter
                }

            }

            sendCommentButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {

                    val post = postDao.getPostsById(postIdFromIntent).postId
                    val currentUserId = userDao.findUserByUsernameOrEmail(UserNameOrMail.userNameOrMail)?.id

                    if(currentUserId != null){
                        var comments = commentDao.insert(Comment(0, currentUserId, post, commentEdit.text.toString()))

                        val currentUserId = userDao.findUserByUsernameOrEmail(UserNameOrMail.userNameOrMail)?.id

                        if(currentUserId != null) {
                            val commentsUpdated = commentDao.getAll(postIdFromIntent, currentUserId)


                        withContext(Dispatchers.Main) {
                            commentAdapter.updateComments(commentsUpdated)
                            recyclerView.scrollToPosition(commentsUpdated.size - 1)

                            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                            inputMethodManager.hideSoftInputFromWindow(commentEdit.windowToken, 0)

                            commentEdit.setText("")

                         }
                        }

                    }



                }
            }

        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

    }


    override fun onResume() {
        super.onResume()

    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}