package com.magicurl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile.*



class ProfileActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

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

        var username = FirebaseAuth.getInstance().currentUser?.email.toString().substringBefore('@')
        txt_username.text = username

        val magicLinks = getLinksFromDb()

        // Create an ArrayAdapter to bind the items to the ListView
        val adapter = ArrayAdapter(this@ProfileActivity, android.R.layout.simple_list_item_1, magicLinks)

        // Set the adapter on the ListView
        Url_list.adapter = adapter

    }


    private fun getLinksFromDb(): List<Any> {
        database = Firebase.database.reference

        val magicLinks = hashMapOf<Any, Any>();

        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        database.child("urls").child(userId).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")



        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        println(magicLinks)
        return emptyList<Any>()
    }

}