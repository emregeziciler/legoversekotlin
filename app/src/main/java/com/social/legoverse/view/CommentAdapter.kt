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
import com.social.legoverse.util.Comment
import com.social.legoverse.util.Like
import com.social.legoverse.util.Post
import com.social.legoverse.util.UserNameOrMail
import com.social.legoverse.util.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentAdapter(private var comments: List<Comment>, private val context: Context) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val commentText: TextView = view.findViewById(R.id.textViewComment)
        val textViewUserName: TextView = view.findViewById(R.id.textViewUsername)
        val imageViewProfile: ImageView = view.findViewById(R.id.imageViewProfile)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        holder.commentText.text = comment.commentText
        val userDao = AppDatabase.getDatabase(context).userDao()

        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = userDao.findUserByUsernameOrEmail(UserNameOrMail.userNameOrMail)
            withContext(Dispatchers.Main) {
                if (currentUser != null) {
                    holder.textViewUserName.text = currentUser.userName
                }

                if(currentUser?.profileImage != null){
                    val bitmapProfile = byteArrayToBitmap(currentUser.profileImage)
                    holder.imageViewProfile.setImageBitmap(bitmapProfile)
                }

            }
        }




        }
    fun updateComments(newComments: List<Comment>) {
        this.comments = newComments
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return comments.size
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}
