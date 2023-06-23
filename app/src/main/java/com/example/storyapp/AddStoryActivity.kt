package com.example.storyapp

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.local.AuthPreferences
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.models.AuthViewModel
import com.example.storyapp.models.AuthViewModelFactory
import com.example.storyapp.models.MainViewModel
import com.example.storyapp.models.MainViewModelFactory
import com.example.storyapp.utils.rotateFile
import com.example.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var getFile: File? = null
    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()

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
        Log.d("Upload", "Btn Upload clicked!")
        val pref = AuthPreferences.getInstance(dataStore)
        val mainViewModel = ViewModelProvider(this, MainViewModelFactory(application,pref))[MainViewModel::class.java]
        val authViewModel = ViewModelProvider(this, AuthViewModelFactory(pref))[AuthViewModel::class.java]

        val descText = binding.storyDesc.text.toString()

        if (getFile != null && descText != "") {
            val file = getFile as File

            val desc = descText.toRequestBody("image/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                getString(R.string.img_upload_desc),
                file.name,
                requestImageFile
            )
            val lat = currentLocation?.latitude?.toFloat()
            val lon = currentLocation?.longitude?.toFloat()

            authViewModel.getToken().observe(this) { token ->
                mainViewModel.addNewStory(desc, imageMultipart, token, lat, lon)
                Log.d("GU", "$desc, $imageMultipart")
            }

            mainViewModel.apply {
                isLoading.observe(this@AddStoryActivity) {
                    showLoading(it)
                }

                msgUpload.observe(this@AddStoryActivity) { msg ->
                    Toast.makeText(this@AddStoryActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }

            val intentHome = Intent(this, MainActivity::class.java)
            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            finish()
            startActivity(intentHome)

        } else {
            Toast.makeText(this@AddStoryActivity, getString(R.string.validate_upload), Toast.LENGTH_SHORT).show()
        }
    }
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if(checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        getString(R.string.location_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
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

    private fun showLoading(isLoading: Boolean) {
        binding.includeLoading.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {}
            }
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

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}