package com.teamtechnojam.campusconnect.ui.activity

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.teamtechnojam.campusconnect.R
import com.teamtechnojam.campusconnect.databinding.ActivityCompleteRegistractionBinding

class CompleteRegistrationActivity : AppCompatActivity() {

    private lateinit var courseName: String
    private lateinit var phoneNumber: String
    private lateinit var collageName: String
    private lateinit var userName: String
    private var about: String = ""
    private var skills: String = ""
    private lateinit var binding: ActivityCompleteRegistractionBinding
    private lateinit var mAuth: FirebaseAuth
    private var imageUri: Uri? = null
    private lateinit var loadingDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteRegistractionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = Firebase.auth
        loadingDialog = Dialog(this@CompleteRegistrationActivity)
        loadingDialog.setContentView(R.layout.loading_pop_up)
        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.setCancelable(false)

        binding.btnBack.setOnClickListener {
            mAuth.signOut()
            onBackPressedDispatcher.onBackPressed()
        }
        binding.ivUserProfilePic.setOnClickListener {
            showImageAttachMenu()
        }

        binding.btnSaveInfo?.setOnClickListener {
            userName = binding.etUserName.text.toString()
            collageName = binding.etCollageName?.text.toString()
            phoneNumber = binding.etPhoneNumber?.text.toString()
            courseName = binding.etCourse?.text.toString()
            skills = binding.etSkills?.text.toString()
            about = binding.etAbout?.text.toString()


            if (userName.isEmpty()) {
                showToast("User Name can't be Empty.")
            } else if (collageName.isEmpty()) {
                showToast("Please enter your University/Collage.")
            } else if (phoneNumber.isEmpty() || phoneNumber.length < 13) {
                showToast("Please check your phone Number.")
            } else if (courseName.isEmpty()) {
                showToast("Please enter Course Name.")
            } else {
                saveDataToFireDatabase()
            }

        }
    }

    private fun saveDataToFireDatabase() {
        loadingDialog.show()
        val filePathAndName = "ProfileImages/" + mAuth.uid

        val storageRef = FirebaseStorage.getInstance().getReference(filePathAndName)
        try {
            if (imageUri != null) {
                storageRef.putFile(imageUri!!)
                    .addOnSuccessListener { taskSnapshot ->

                        val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                        while (!uriTask.isSuccessful);
                        val uploadedImageUrl = uriTask.result.toString()

                        updateProfile(uploadedImageUrl)

                        Toast.makeText(this, " Uploaded Successfully.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Uploading Failed Due to ${it.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        loadingDialog.dismiss()
                    }
            } else {
                val packageName = packageName.toString()
                val defaultImageUri =
                    Uri.parse("android.resource://$packageName/${R.drawable.ic_person}")
                storageRef.putFile(defaultImageUri!!)
                    .addOnSuccessListener { taskSnapshot ->

                        val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                        while (!uriTask.isSuccessful);
                        val uploadedImageUrl = uriTask.result.toString()

                        updateProfile(uploadedImageUrl)

                        Toast.makeText(this, " Uploaded Successfully.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Uploading Failed Due to ${it.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        loadingDialog.dismiss()
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateProfile(uploadedImageUri: String) {

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["userName"] = userName
        hashMap["university"] = collageName
        hashMap["phoneNumber"] = phoneNumber
        hashMap["courseName"] = courseName
        hashMap["about"] = about
        hashMap["skills"] = skills
        if (imageUri != null) {
            hashMap["profileImage"] = uploadedImageUri
        }

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(mAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this, " Update Successfully.", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
                startActivity(Intent(this@CompleteRegistrationActivity, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update Failed due to ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@CompleteRegistrationActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showImageAttachMenu() {
        val popupMenu = PopupMenu(this, binding.ivUserProfilePic)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Camera")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == 0) {
                pickImageCamera()
            } else if (id == 1) {
                pickImageGallery()
            }
            true
        }
    }

    private fun pickImageCamera() {
        val cameraPermission = android.Manifest.permission.CAMERA
        val storagePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(
                this,
                cameraPermission
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                storagePermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "Temp_title")
            values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_description")

            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            cameraActionResultLauncher.launch(intent)
        } else {
            requestPermissions(arrayOf(cameraPermission, storagePermission))
        }
    }

    private val cameraActionResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.ivUserProfilePic.setImageURI(imageUri)
            } else {
                Toast.makeText(this@CompleteRegistrationActivity, "Cancelled", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    )

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActionResultLauncher.launch(intent)
    }

    private val galleryActionResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data?.data
                binding.ivUserProfilePic.setImageURI(imageUri)
            } else {
                Toast.makeText(this@CompleteRegistrationActivity, "Cancelled", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    )

    private fun requestPermissions(permissions: Array<String>) {
        val shouldShowRequestPermissionRationale = permissions.any { permission ->
            shouldShowRequestPermissionRationale(permission)
        }

        if (shouldShowRequestPermissionRationale) {
            AlertDialog.Builder(this)
                .setMessage("Camera and storage permissions are required to capture photos.")
                .setPositiveButton("OK") { _, _ ->
                    requestPermissionLauncher.launch(permissions)
                }
                .setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(
                        this@CompleteRegistrationActivity,
                        "Permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .show()
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            pickImageCamera()
        } else {
            Toast.makeText(
                this@CompleteRegistrationActivity,
                "Permission denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}