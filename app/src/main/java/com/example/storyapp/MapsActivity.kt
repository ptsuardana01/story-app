package com.example.storyapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.local.AuthPreferences
import com.example.storyapp.data.responses.ListStoryItem
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.models.AuthViewModel
import com.example.storyapp.models.AuthViewModelFactory
import com.example.storyapp.models.MainViewModel
import com.example.storyapp.models.MainViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var listData: ArrayList<ListStoryItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment


        val pref = AuthPreferences.getInstance(dataStore)
        val mainViewModel =
            ViewModelProvider(this, MainViewModelFactory(application ,pref))[MainViewModel::class.java]
        val authViewModel =
            ViewModelProvider(this, AuthViewModelFactory(pref))[AuthViewModel::class.java]

        authViewModel.getToken().observe(this) { token ->
            if (token.isEmpty()) {
                jumpToLogin()
            } else {
                mainViewModel.getStoryLocation(token)
            }
        }

        mainViewModel.listWithLocation.observe(this) { location ->
            setDataMaps(location)
            Log.d("Map", location.size.toString())
            mapFragment.getMapAsync(this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        addManyMarker()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun jumpToLogin() {
        val intentToLogin = Intent(this@MapsActivity, AuthenticationActivity::class.java)
        startActivity(intentToLogin)
        finish()
    }

    private fun setDataMaps(listDataStory: List<ListStoryItem>) {
        listData = ArrayList()
        for (data in listDataStory) {
            val list = ListStoryItem(
                data.id,
                data.photoUrl,
                data.createdAt,
                data.name,
                data.description,
                data.lon,
                data.lat
            )
            listData.add(list)
        }

    }

    private fun addManyMarker() {
        listData.forEach { data ->
            val latLng = LatLng(data.lat, data.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(data.name)
                    .snippet(data.description)
            )
        }
    }
}