package com.magicurl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListAdapter
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
    private lateinit var dataArray: Array<String>
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

        user_list.setOnItemClickListener{parent, view, position, id ->
            val intent =
                Intent(this@SearchActivity, OtherUsersProfileActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("user_name", dataArray.get(position))
            startActivity(intent)
            finish()


        }
    }
    private fun searchUser(){
        database = Firebase.database.reference

        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val searchId = et_src_user.text.toString()

        //val currentUser = database.child(userId).child("username").get().toString()

        database.get().addOnSuccessListener {

            val mySearchResults : MutableList<String> = mutableListOf()

            if (it.exists()) {
                Log.i("firebase", "Got value ${it.value}")
                val results: HashMap<*, *> = it.value as HashMap<*, *>

                for ((key, value) in results) {
                    val nameFound = (value as HashMap<*, *>).get("username").toString()

                    if (nameFound.contains(searchId, ignoreCase = true)){
                        mySearchResults.add(nameFound)
                    }
                }

                println(mySearchResults)
                val array = mySearchResults.toTypedArray()
                this.dataArray = array

                val arrayAdapter: ArrayAdapter<*>
                val mListView = findViewById<ListView>(R.id.user_list)
                arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, array)
                mListView.adapter = arrayAdapter
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

    }


}