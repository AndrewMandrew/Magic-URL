package com.magicurl

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_other_users_profile.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_search.*

class OtherUsersProfileActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

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
        val placeholder = userId +"'S URLs"
        name_urls.text = placeholder
        userLinks(intent.getStringExtra("user_name").toString())
    }

    private fun userLinks(searchId: String){
        database = Firebase.database.reference

        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        //val currentUser = database.child(userId).child("username").get().toString()

        database.get().addOnSuccessListener {

            var foundKey = "/"

            if (it.exists()) {
                Log.i("firebase", "Got value ${it.value}")
                val results: HashMap<*, *> = it.value as HashMap<*, *>

                for ((key, value) in results) {
                    val nameFound = (value as HashMap<*, *>).get("username").toString()

                    if (nameFound.equals(searchId, ignoreCase = true)){
                        foundKey = key.toString()
                    }
                }

                database.child(foundKey).get().addOnSuccessListener {
                    val map = it.value
                    val array = (map as MutableMap<*, *>).toList().toTypedArray()

                    // Create an ArrayAdapter to bind the items to the ListView
                    val adapter =
                        ArrayAdapter(this@OtherUsersProfileActivity, android.R.layout.simple_list_item_1, array)

                    // Set the adapter on the ListView
                    Url_list.adapter = OtherUsersProfileActivity.MyCustomAdapter(this, array, database)

                    println(map)
                }

                //println(urls)
                //val array = (urls as MutableMap<*, *>).toList().toTypedArray()


            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

    }

    private class MyCustomAdapter(
        context: Context,
        array: Array<Pair<*, *>>,
        database: DatabaseReference
    ) : BaseAdapter() {

        private val mContext: Context
        private val linkArray: Array<Pair<*, *>>
        private val db: DatabaseReference
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()


        init {
            this.mContext = context
            this.linkArray = array
            this.db = database

        }


        //responsible for the number of rows in my list
        override fun getCount(): Int {
            return linkArray.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return "TEST"
        }


        //responsible for rendering out each row
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val LayoutInflater = LayoutInflater.from(mContext)
            val mainRow = LayoutInflater.inflate(R.layout.user_links, parent, false)
            val namePosition = mainRow.findViewById<TextView>(R.id.name_textView)
            val urlPosition = mainRow.findViewById<TextView>(R.id.link_textView)
            val delete = mainRow.findViewById<TextView>(R.id.delete)
            val modify = mainRow.findViewById<TextView>(R.id.modify)

            namePosition.text = linkArray.get(position).first.toString().substringAfter("-")
            urlPosition.text = linkArray.get(position).second.toString()
            delete.setOnClickListener {
                val deleteElement = linkArray.get(position).first.toString()
                linkArray.drop(position)
                db.child(userId).child("urls").child(deleteElement).removeValue()

            }
            modify.setOnClickListener {

            }
            return mainRow
        }
    }
}