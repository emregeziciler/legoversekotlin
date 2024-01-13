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

class SearchAdapter(private var projects: List<Post>, private val context: Context) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewProjectNameContent: TextView = view.findViewById(R.id.textViewProjectName)
        val projectItemCard: CardView = view.findViewById(R.id.projectItemCard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search, parent, false)



        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val project = projects[position]

        holder.textViewProjectNameContent.text = project.projectName

        holder.projectItemCard.setOnClickListener {
            val intent = Intent (context, ViewPostActivity::class.java)
            Log.i("PostAdapter", "post_id: ${project.postId}")
            intent.putExtra("post_id", project.postId)
            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return projects.size
    }

    fun filterList(filteredProjects: List<Post>) {
        this.projects = filteredProjects
        notifyDataSetChanged()
    }
}
