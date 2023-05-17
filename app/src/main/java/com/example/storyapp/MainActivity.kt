package com.example.storyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.data.local.AuthPreferences
import com.example.storyapp.data.responses.ListStoryItem
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.models.AuthViewModel
import com.example.storyapp.models.AuthViewModelFactory
import com.example.storyapp.models.MainViewModel
import com.example.storyapp.models.MainViewModelFactory
import com.example.storyapp.ui.adapters.StoryListAdapter


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = AuthPreferences.getInstance(dataStore)
        val mainViewModel = ViewModelProvider(this, MainViewModelFactory(pref))[MainViewModel::class.java]
        val authViewModel = ViewModelProvider(this, AuthViewModelFactory(pref))[AuthViewModel::class.java]

        binding.rvStoryApp.setHasFixedSize(true)
        showRecyclerView()

        mainViewModel.listStory.observe(this){ list ->
            setUser(list)
        }

        mainViewModel.isLoading.observe(this){
            showLoading(it)
        }

        binding.btnLogout.setOnClickListener{
            authViewModel.logout()
            val intentLogout = Intent(this, AuthenticationActivity::class.java)
            finish()
            startActivity(intentLogout)
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.includeLoading.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setUser(items: List<ListStoryItem>) {
        val listStory = StoryListAdapter(items)
        binding.rvStoryApp.adapter = listStory
    }

    private fun showRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStoryApp.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStoryApp.addItemDecoration(itemDecoration)
    }
}