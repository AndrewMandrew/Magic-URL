package com.magicurl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_other_users_profile.*
import kotlinx.android.synthetic.main.activity_profile.*

class OtherUsersProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_users_profile)


        other_home.setOnClickListener {

            startActivity(Intent(this@OtherUsersProfileActivity, HomeActivity::class.java))
        }
        other_search.setOnClickListener {

            startActivity(Intent(this@OtherUsersProfileActivity, SearchActivity::class.java))
        }
        other_star.setOnClickListener {

            startActivity(Intent(this@OtherUsersProfileActivity, FavouriteActivity::class.java))
        }
        other_profile.setOnClickListener {

            startActivity(Intent(this@OtherUsersProfileActivity, ProfileActivity::class.java))
        }


        val userId = intent.getStringExtra("user_name")?.substringBefore("@")
        txt_otheruser.text = userId
    }
}