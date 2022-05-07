package com.dicoding.android.zsstoryapp.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.android.zsstoryapp.R
import com.dicoding.android.zsstoryapp.data.model.ListStoryItem
import com.dicoding.android.zsstoryapp.databinding.ActivityDetailBinding
import com.dicoding.android.zsstoryapp.utils.withDateFormat

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()

        // Action Bar
        supportActionBar?.title = getString(R.string.actionbar_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupData() {
        val story = intent.getParcelableExtra<ListStoryItem>("Story") as ListStoryItem
        Glide.with(applicationContext)
            .load(story.photoUrl)
            .apply(RequestOptions())
            .into(findViewById(R.id.iv_detail))
        binding.tvDetailName.text = story.name
        binding.tvDetailName.text = story.description
        binding.tvDetailCreatedAt.text = getString(R.string.user_created_at,
            story.createdAt?.withDateFormat()
        )

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}