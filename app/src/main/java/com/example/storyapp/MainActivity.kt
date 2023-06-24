package com.example.storyapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.data.local.preference.AuthPreferencesViewModel
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.models.AuthViewModelFactory
import com.example.storyapp.models.MainViewModel
import com.example.storyapp.models.ViewModelFactory
import com.example.storyapp.ui.adapters.LoadingStateAdapter
import com.example.storyapp.ui.adapters.StoryListAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val authPreferencesViewModel: AuthPreferencesViewModel by viewModels {
        AuthViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showRecyclerView()

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.apply {
            btnAddStory.setOnClickListener {
                val intentAddStory = Intent(this@MainActivity, AddStoryActivity::class.java)
                startActivity(intentAddStory)
            }

            btnMaps.setOnClickListener {
                val intentMaps = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intentMaps)
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun showRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStoryApp.layoutManager = layoutManager

        binding.rvStoryApp.setHasFixedSize(true)

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStoryApp.addItemDecoration(itemDecoration)

        val listStory = StoryListAdapter()
        binding.rvStoryApp.adapter = listStory

        binding.rvStoryApp.adapter = listStory.withLoadStateFooter(
            footer = LoadingStateAdapter {
                listStory.retry()
            }
        )

        lifecycleScope.launch {
            authPreferencesViewModel.getToken().observe(this@MainActivity) { token ->
                mainViewModel.listStory(token).observe(this@MainActivity) { pagingData ->
                    listStory.submitData(lifecycle, pagingData)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                authPreferencesViewModel.logout()
                val intentLogout = Intent(this, AuthenticationActivity::class.java)
                intentLogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intentLogout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                finish()
                startActivity(intentLogout)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.includeLoading.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}