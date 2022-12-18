package com.magicurl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        search_home.setOnClickListener {

            startActivity(Intent(this@SearchActivity, HomeActivity::class.java))
        }
        search_star.setOnClickListener {

            startActivity(Intent(this@SearchActivity, FavouriteActivity::class.java))
        }
        search_profile.setOnClickListener {

            startActivity(Intent(this@SearchActivity, ProfileActivity::class.java))
        }
    }
}