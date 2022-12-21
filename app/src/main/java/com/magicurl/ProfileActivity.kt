package com.magicurl

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profile_home.setOnClickListener {

            startActivity(Intent(this@ProfileActivity, HomeActivity::class.java))
        }
        profile_search.setOnClickListener {

            startActivity(Intent(this@ProfileActivity, SearchActivity::class.java))
        }
        profile_star.setOnClickListener {

            startActivity(Intent(this@ProfileActivity, FavouriteActivity::class.java))
        }
        btn_logout.setOnClickListener {
            // Logout from app.
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
            finish()
        }
        btn_modify.setOnClickListener{


        }
    }
}