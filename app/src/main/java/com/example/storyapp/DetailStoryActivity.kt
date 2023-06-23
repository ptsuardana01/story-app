package com.example.storyapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.data.local.preference.AuthPreferences
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.models.AuthViewModel
import com.example.storyapp.models.AuthViewModelFactory
import com.example.storyapp.models.MainViewModel
import com.example.storyapp.models.MainViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_header_story)

        val pref = AuthPreferences.getInstance(dataStore)
        val mainViewModel = ViewModelProvider(this, MainViewModelFactory(pref))[MainViewModel::class.java]
        val authViewModel = ViewModelProvider(this, AuthViewModelFactory(pref))[AuthViewModel::class.java]

        authViewModel.getToken().observe(this) { token ->
            val id = intent?.getStringExtra(ID)
            if (id != null) {
                mainViewModel.getDetailStory(id, token)
            }
        }

        mainViewModel.detailStory.observe(this) { detail ->
            binding.apply {
                Glide.with(this@DetailStoryActivity)
                    .load(detail.story.photoUrl)
                    .into(detailImg)

                detailUsername.text = detail.story.name
                detailCreatedAt.text = detail.story.createdAt
                detailDesc.text = detail.story.description
            }
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.includeLoading.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    companion object {
        const val ID = "0"
    }
}