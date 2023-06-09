package com.example.storyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
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

        authViewModel.getToken().observe(this) { token ->
            mainViewModel.getAllListStory(token)
        }

        mainViewModel.listStory.observe(this) {
            setUser(it)
        }

        mainViewModel.isLoading.observe(this){
            showLoading(it)
        }

        binding.btnAddStory.setOnClickListener {
            val intentAddStory = Intent(this, AddStoryActivity::class.java)
            startActivity(intentAddStory)
        }

        binding.btnMaps.setOnClickListener {
            val intentMaps = Intent(this, MapsActivity::class.java)
            startActivity(intentMaps)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val pref = AuthPreferences.getInstance(dataStore)
        val authViewModel = ViewModelProvider(this, AuthViewModelFactory(pref))[AuthViewModel::class.java]
        when (item.itemId) {
            R.id.logout -> {
                authViewModel.logout()
                val intentLogout = Intent(this, AuthenticationActivity::class.java)
                finish()
                startActivity(intentLogout)
            }
            R.id.setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.includeLoading.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setUser(items: List<ListStoryItem>) {
        val listStory = StoryListAdapter(items)
        binding.rvStoryApp.adapter = listStory

        listStory.setOnStoryListClickCallback( object: StoryListAdapter.OnStoryListClickCallback {
            override fun onItemClicked(story: ListStoryItem) {
                val intentToDetail = Intent(this@MainActivity, DetailStoryActivity::class.java)
                intentToDetail.putExtra(DetailStoryActivity.ID, story.id)
                startActivity(intentToDetail)
            }

        })
    }

    private fun showRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStoryApp.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStoryApp.addItemDecoration(itemDecoration)
    }
}