package com.social.legoverse.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import com.social.legoverse.databinding.ActivityAddPostBinding
import com.social.legoverse.util.AppDatabase
import com.social.legoverse.util.Post
import com.social.legoverse.util.UserNameOrMail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class AddPostActivity : AppCompatActivity() {
    private val TAG = "_AddPostActivity"

    private lateinit var binding: ActivityAddPostBinding
    private lateinit var toolbar: Toolbar

    private lateinit var addImageButton: ImageView
    private lateinit var postDescription: EditText
    private lateinit var projectName: EditText
    private lateinit var shareButton: Button

    private  var  imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPostBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        toolbar = binding.toolbar
        addImageButton = binding.addImageButton
        postDescription = binding.postDescription
        shareButton = binding.shareButton
        projectName = binding.projectName



        addImageButton.setOnClickListener {
            Log.i(TAG, "addImageButton.setOnClickListener")

            val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(pickImageIntent)
        }

        shareButton.setOnClickListener {
            Log.i(TAG, "shareButton.setOnClickListener: " + UserNameOrMail.userNameOrMail)


            val postDescriptionText = postDescription.text.toString()
            val projectNameText = projectName.text.toString()

            if(postDescriptionText != "" && projectNameText != "" ){
                if(imageUri != null){
                    val postImageBytes = imageUri?.let { uri -> uriToByteArray(uri) }


                    val userDao = AppDatabase.getDatabase(applicationContext).userDao()
                    CoroutineScope(Dispatchers.IO).launch {
                        val user = userDao.findUserByUsernameOrEmail(UserNameOrMail.userNameOrMail)

                        user?.let {
                            Log.i(TAG, "shareButton clickListener in user")

                            val postDao = AppDatabase.getDatabase(applicationContext).postDao()
                            val newPost = Post(
                                userId = it.id,
                                image = postImageBytes,
                                content = postDescriptionText,
                                likes = 0,
                                projectName = projectNameText
                            )
                            postDao.insertPost(newPost)
                            runOnUiThread {
                                Toast.makeText(this@AddPostActivity, "Post shared successfully!", Toast.LENGTH_SHORT).show()
                            }
                            finish()
                        } ?: run {
                            runOnUiThread {
                                Toast.makeText(this@AddPostActivity, "User not found!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this@AddPostActivity, "Please select image", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }else{
                runOnUiThread {
                    Toast.makeText(this@AddPostActivity, "Please fill all field", Toast.LENGTH_SHORT)
                        .show()
                }
            }


        }


        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            imageUri?.let {
                addImageButton.setImageURI(it)
            }
        }
    }

    private fun uriToByteArray(uri: Uri): ByteArray? {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}