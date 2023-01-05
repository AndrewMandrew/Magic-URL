package com.magicurl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_favourite.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference


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

        btn_search.setOnClickListener {
            when {
                TextUtils.isEmpty(et_src_user.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@SearchActivity,
                        "Please enter a username or email",
                        Toast.LENGTH_SHORT
                    ).show()
                }else ->{

                    searchUser()
                }


            }
        }
    }
    private fun searchUser(){
        database = Firebase.database.reference

        val searchId = et_src_user.text.toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()




    }


}