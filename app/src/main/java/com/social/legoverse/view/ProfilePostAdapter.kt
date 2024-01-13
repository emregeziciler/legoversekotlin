package com.social.legoverse.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.social.legoverse.R
import com.social.legoverse.util.AppDatabase
import com.social.legoverse.util.Like
import com.social.legoverse.util.Post
import com.social.legoverse.util.UserNameOrMail
import com.social.legoverse.util.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfilePostAdapter(private val user: Users, private val posts: List<Post>, private val context: Context) :
    RecyclerView.Adapter<ProfilePostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewProfile: ImageView = view.findViewById(R.id.imageViewProfile)
        val textViewUsername: TextView = view.findViewById(R.id.textViewUsername)
        val imageViewPostImage: ImageView = view.findViewById(R.id.imageViewPostImage)
        val textViewPostContent: TextView = view.findViewById(R.id.textViewPostContent)
        val textViewLikeCount: TextView = view.findViewById(R.id.textViewLikeCount)
        val likeButton: ImageView = view.findViewById(R.id.likeButton)
        val textViewProjectNameContent: TextView = view.findViewById(R.id.textViewProjectName)
        val postItemCard: CardView = view.findViewById(R.id.postItemCard)
        val deletePost: ImageView = view.findViewById(R.id.deleteButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_profile, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        Log.i("ProfilePostAdapter", "onBindViewHolder() -> ")

        holder.textViewPostContent.text = post.content
        holder.textViewProjectNameContent.text = post.projectName
        holder.textViewLikeCount.text = "${post.likes} Likes"

        holder.postItemCard.setOnClickListener {
            val intent = Intent (context, ViewPostActivity::class.java)
            Log.i("PostAdapter", "post_id: ${post.postId}")
            intent.putExtra("post_id", post.postId)
            context.startActivity(intent)


        }

        val userDao = AppDatabase.getDatabase(context).userDao()
        val postDao = AppDatabase.getDatabase(context).postDao()
        val commentDao = AppDatabase.getDatabase(context).commentDao()
        val likeDao = AppDatabase.getDatabase(context).likeDao()

        CoroutineScope(Dispatchers.IO).launch {
            val users = userDao.findUserWithId(post.userId)
                    withContext(Dispatchers.Main) {
                        if (users != null) {
                            if(users.userName == UserNameOrMail.userNameOrMail || users.mail == UserNameOrMail.userNameOrMail){
                                holder.deletePost.visibility = View.VISIBLE

                                holder.deletePost.setOnClickListener {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        postDao.deletePost(post)
                                        commentDao.deleteCommentWithPostId(post.postId)
                                        likeDao.deleteLikeWithPostId(post.postId)

                                        (posts as MutableList).removeAt(holder.adapterPosition)

                                        withContext(Dispatchers.Main) {
                                            notifyItemRemoved(holder.adapterPosition)
                                            notifyItemRangeChanged(holder.adapterPosition, posts.size)
                                        }
                                    }
                                }


                            }


                            Log.i("PostAdapter", "users != null")
                            val bitmapPost = post.image?.let { it1 -> byteArrayToBitmap(it1) }
                            holder.imageViewPostImage.setImageBitmap(bitmapPost)
                            holder.textViewUsername.text = users.userName

                            users.profileImage?.let {
                                Log.i("PostAdapter", "users.profileImage?.let")

                                val bitmapProfile = byteArrayToBitmap(users.profileImage)
                                holder.imageViewProfile.setImageBitmap(bitmapProfile)
                            }
                        }
                    }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val currentUserId = userDao.findUserByUsernameOrEmail(UserNameOrMail.userNameOrMail)?.id
            val likeDao = AppDatabase.getDatabase(context).likeDao()

            if(currentUserId != null){
                val existingLike = likeDao.findLikeByUserAndPost(currentUserId, post.postId)

                if (existingLike != null) {
                    holder.likeButton.setColorFilter(context.resources.getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_IN)
                }
            }



        }

        holder.likeButton.setOnClickListener {

                CoroutineScope(Dispatchers.IO).launch {
                    val currentUserId = userDao.findUserByUsernameOrEmail(UserNameOrMail.userNameOrMail)?.id

                    val postToLike = postDao.getPostsById(post.postId)


                    if(currentUserId != null){
                        val likeDao = AppDatabase.getDatabase(context).likeDao()
                        val existingLike = likeDao.findLikeByUserAndPost(currentUserId, post.postId)

                        Log.i("PostAdapter", "postToLike.likes: " + postToLike.likes + " ")

                        if (existingLike == null) {
                            // Like yoksa, ekle
                            val newLike = Like(userId = currentUserId, postId = post.postId)
                            likeDao.insertLike(newLike)
                            postDao.updatePostLikes(post.postId, postToLike.likes + 1)
                        } else {
                            // Like varsa, kaldır
                            likeDao.deleteLike(existingLike)
                            postDao.updatePostLikes(post.postId, postToLike.likes - 1)
                        }

                        withContext(Dispatchers.Main) {
                            // UI güncellemesi
                            if (existingLike == null) {
                                holder.likeButton.setColorFilter(context.resources.getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_IN)
                                holder.textViewLikeCount.text = "${postToLike.likes + 1} Likes"
                            } else {
                                holder.likeButton.setColorFilter(context.resources.getColor(R.color.heart_gray), PorterDuff.Mode.SRC_IN)
                                holder.textViewLikeCount.text = "${postToLike.likes - 1} Likes"
                            }
                        }
                    }

                }
        }


    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}
