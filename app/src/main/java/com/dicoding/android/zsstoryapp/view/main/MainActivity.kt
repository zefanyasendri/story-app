package com.dicoding.android.zsstoryapp.view.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.android.zsstoryapp.R
import com.dicoding.android.zsstoryapp.data.local.datastore.LoginPreferences
import com.dicoding.android.zsstoryapp.data.model.ListStoryItem
import com.dicoding.android.zsstoryapp.databinding.ActivityMainBinding
import com.dicoding.android.zsstoryapp.utils.AppConstants
import com.dicoding.android.zsstoryapp.view.PrefViewModelFactory
import com.dicoding.android.zsstoryapp.view.add.AddStoryActivity
import com.dicoding.android.zsstoryapp.view.detail.DetailActivity
import com.dicoding.android.zsstoryapp.view.maps.MapsActivity
import com.dicoding.android.zsstoryapp.view.welcome.WelcomeActivity
import com.google.android.gms.maps.model.LatLng

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory(this)
    }
    private lateinit var adapter: ListStoryAdapter
    private lateinit var binding: ActivityMainBinding

    // Map
    private var lStoryMap: ArrayList<LatLng>? = null
    private var lStoryMapName: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()

        supportActionBar?.title = "ZS Story User App"

        // List Story
        adapter = ListStoryAdapter()
        lStoryMap = ArrayList()
        lStoryMapName = ArrayList()
        adapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun itemClicked(story: ListStoryItem) {
                Intent(this@MainActivity, DetailActivity::class.java).also {
                    it.putExtra(AppConstants.EXTRA_PHOTO, story.photoUrl)
                    it.putExtra(AppConstants.EXTRA_CREATED_AT, story.createdAt)
                    it.putExtra(AppConstants.EXTRA_NAME, story.name)
                    it.putExtra(AppConstants.EXTRA_DESC, story.description)
                    startActivity(it)
                }
            }
        })

        mainViewModel.getStory().observe(this) {
            onLoading(true)
            if (it != null) {
                for (i in it.indices) {
                    lStoryMap!!.add(LatLng(it[i].lat, it[i].lon))
                    lStoryMapName!!.add(it[i].name)
                    onLoading(false)
                }
            }
        }

    }

    private fun getToken(token: String) {
        binding.apply {
            if (token.isEmpty()) return
            onLoading(true)
            mainViewModel.setListStory(token)
        }
    }

    private fun setupViewModel() {
        var name: String

        mainViewModel = ViewModelProvider(this, PrefViewModelFactory(LoginPreferences.getInstance(dataStore)))[MainViewModel::class.java]
        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                AppConstants.token = "Bearer ${user.token}"
                name = user.name
                binding.tvName.text = getString(R.string.greeting, user.name)
                binding.apply {
                    rvUser.layoutManager = LinearLayoutManager(this@MainActivity)
                    rvUser.adapter = adapter.withLoadStateFooter(
                        footer = LoadingStateAdapter {
                            adapter.retry()
                        }
                    )
                    getToken(AppConstants.token)
                }

                // Paging
                storyViewModel.stories().observe(this) { story ->
                    adapter.submitData(lifecycle, story)
                }

                // Add Story
                binding.fabAdd.setOnClickListener { view ->
                    if (view.id == R.id.fab_add) {
                        startActivity(
                            Intent(this@MainActivity, AddStoryActivity::class.java).apply {
                                putExtra(AppConstants.EXTRA_TOKEN, AppConstants.token)
                                putExtra(AppConstants.EXTRA_NAME, name)
                            }
                        )
                    }
                }
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    // Logout
    private fun setupAction() {
        binding.btnLogout.setOnClickListener {
            mainViewModel.logout()
        }
    }

    // Option Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        val list = menu.findItem(R.id.menu_list)
        list.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_localization -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.menu_maps -> {
                Intent(this@MainActivity, MapsActivity::class.java).also {
                    it.putExtra(AppConstants.EXTRA_MAP, lStoryMap)
                    it.putExtra(AppConstants.EXTRA_MAP_NAME, lStoryMapName)
                    startActivity(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Loading
    private fun onLoading(data: Boolean) {
        val visibilityState = if(data) View.VISIBLE else View.INVISIBLE
        binding.progressBar.visibility = visibilityState
        binding.tvMsg.visibility = visibilityState
    }
}