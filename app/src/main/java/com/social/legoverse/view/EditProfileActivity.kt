package com.social.legoverse.view

import android.app.Activity
import android.app.DatePickerDialog
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
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.social.legoverse.R
import com.social.legoverse.databinding.ActivityCreateProfileBinding
import com.social.legoverse.databinding.ActivityEditProfileBinding
import com.social.legoverse.util.AppDatabase
import com.social.legoverse.util.UserNameOrMail
import com.social.legoverse.util.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {
    private val TAG = "_EditProfileActivity"

    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var addImageButton: FrameLayout
    private lateinit var profileImage: ImageView

    private lateinit var realNameEdit: EditText
    private lateinit var userNameEdit: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var birthDay: EditText
    private lateinit var editProfileButton: Button

    private lateinit var selectedDate: Date
    private  var  imageUri: Uri? = null

    lateinit var profileImageBytes: ByteArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        addImageButton = binding.addImageButton
        profileImage = binding.imageViewProfile

        realNameEdit = binding.realNameEdit
        userNameEdit = binding.userNameEdit
        phoneNumber = binding.phoneNumber
        birthDay = binding.birthDay
        editProfileButton = binding.editProfileButton
        val userDao = AppDatabase.getDatabase(applicationContext).userDao()


        birthDay.setOnClickListener {
            //OPENDATEPICKER
            openDatePicker()
        }

        GlobalScope.launch {
            val user = userDao.findUserByUsernameOrEmail(UserNameOrMail.userNameOrMail)

            if(user != null){
                if(user.profileImage!= null){
                    val bitmapProfile = byteArrayToBitmap(user.profileImage)
                    profileImageBytes = user.profileImage
                    runOnUiThread {
                        profileImage.setImageBitmap(bitmapProfile)
                    }

                }

                runOnUiThread {
                    realNameEdit.setText(user.name)
                    userNameEdit.setText(user.userName)
                    phoneNumber.setText(user.phoneNumber)

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                    birthDay.setText(dateFormat.format(user.birthday))

                    if(user.birthday != null)
                        selectedDate = user.birthday
                }

            }



        }



        editProfileButton.setOnClickListener {
            if(realNameEdit.text.isNotEmpty() && userNameEdit.text.isNotEmpty() && phoneNumber.text.isNotEmpty() && selectedDate != null){
                val usernameOrEmail = UserNameOrMail.userNameOrMail

                // Sadece yeni bir resim seçildiyse, profileImageBytes'i güncelle
                if(imageUri != null){
                    profileImageBytes = uriToByteArray(imageUri!!)
                }

                if(profileImageBytes != null){
                    // Kullanıcı girişlerini al
                    val realName = realNameEdit.text.toString()
                    val userName = userNameEdit.text.toString()
                    val phone = phoneNumber.text.toString()
                    val birthdayValue: Date = selectedDate

                    // Arka planda database güncelleme işlemini yap

                    GlobalScope.launch {
                        // Kullanıcıyı username veya email ile bul
                        val user = userDao.findUserByUsernameOrEmail(usernameOrEmail)
                        if (user != null) {
                            // Güncellenecek kullanıcı bilgilerini ayarla
                            var updatedUser: Users
                            if(profileImageBytes!=null){
                                Log.i(TAG, "profileImageBytes!=null")
                                updatedUser = user.copy(
                                    userName = userName,
                                    name = realName,
                                    phoneNumber =  phone,
                                    birthday = birthdayValue,
                                    profileImage = profileImageBytes
                                )

                            }else{
                                updatedUser = user.copy(
                                    userName = userName,
                                    name = realName,
                                    phoneNumber =  phone,
                                    birthday = birthdayValue,
                                )
                            }

                            // Kullanıcıyı güncelle
                            userDao.update(updatedUser)

                            // Ana Activity'e dön
                            withContext(Dispatchers.Main) {
                                Log.i(TAG, "AAAAA")


                                finish()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@EditProfileActivity, "User not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }else{
                    Toast.makeText(this@EditProfileActivity, "Please select image", Toast.LENGTH_SHORT).show()

                }


            }else{
                Toast.makeText(this@EditProfileActivity, "Please fill all field and be sure select date", Toast.LENGTH_SHORT).show()
            }
        }


        addImageButton.setOnClickListener {
            val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(pickImageIntent)
        }

    }

    private fun uriToByteArray(uri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            imageUri?.let {
                profileImage.setImageURI(it)
            }
        }
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->

            birthDay.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear))

            calendar.set(selectedYear, selectedMonth, selectedDay)
            selectedDate = calendar.time

        }, year, month, day)

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }


}