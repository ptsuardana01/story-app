package com.example.storyapp

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bumptech.glide.Glide
import com.example.storyapp.data.local.preference.AuthPreferencesViewModel
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.models.AuthViewModelFactory
import com.example.storyapp.models.MainViewModel
import com.example.storyapp.models.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val authPreferencesViewModel: AuthPreferencesViewModel by viewModels {
        AuthViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_header_story)

        mainViewModel.apply {
            isLoading.observe(this@DetailStoryActivity) {
                showLoading(it)
            }

            val id = intent?.getStringExtra(ID)
            if (id != null) {
                authPreferencesViewModel.getToken().observe(this@DetailStoryActivity) { token ->
                    mainViewModel.getDetailStory(id, token)
                }
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
    }

    private fun showLoading(isLoading: Boolean) {
        binding.includeLoading.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    companion object {
        const val ID = "0"
        const val EXTRA_STORY_ITEMS = "extra_story"
    }
}