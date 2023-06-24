package com.example.storyapp

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.utils.rotateFile
import com.example.storyapp.utils.uriToFile
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.add_story_header)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCameraX.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.validate_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun uploadImage() {
//        val pref = AuthPreferences.getInstance()
//        val mainViewModel = ViewModelProvider(this, MainViewModelFactory(pref))[MainViewModel::class.java]
//        val authViewModel = ViewModelProvider(this, AuthViewModelFactory(pref))[AuthViewModel::class.java]
//
//        val descText = binding.storyDesc.text.toString()
//
//        if (getFile != null && descText != "") {
//            val file = getFile as File
//
//            val desc = descText.toRequestBody("image/plain".toMediaType())
//            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
//            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
//                getString(R.string.img_upload_desc),
//                file.name,
//                requestImageFile
//            )
//
//            authViewModel.getToken().observe(this) { token ->
//                mainViewModel.addNewStory(desc, imageMultipart, token)
//            }
//
//            mainViewModel.isLoading.observe(this) {
//                showLoading(it)
//            }
//
//            val intentHome = Intent(this, MainActivity::class.java)
//            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            finish()
//            startActivity(intentHome)
//
//        } else {
//            Toast.makeText(this@AddStoryActivity, getString(R.string.validate_upload), Toast.LENGTH_SHORT).show()
//        }
    }

    private fun startGallery() {
        val intentGallery = Intent()
        intentGallery.action = ACTION_GET_CONTENT
        intentGallery.type = "image/*"
        val chooser = Intent.createChooser(intentGallery, getString(R.string.choose_pic))
        launcherIntentGallery.launch(chooser)
    }


    private fun startCameraX() {
        val intentCameraX = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intentCameraX)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.previewImgView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.previewImgView.setImageURI(uri)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.includeLoading.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}