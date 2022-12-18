package com.magicurl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_favourite.*


class FavouriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)

        fav_home.setOnClickListener {

            startActivity(Intent(this@FavouriteActivity, HomeActivity::class.java))
        }
        fav_search.setOnClickListener {

            startActivity(Intent(this@FavouriteActivity, SearchActivity::class.java))
        }

        fav_profile.setOnClickListener {

            startActivity(Intent(this@FavouriteActivity, ProfileActivity::class.java))
        }

    }
}