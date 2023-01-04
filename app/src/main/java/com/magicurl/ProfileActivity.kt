package com.magicurl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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


        var username = FirebaseAuth.getInstance().currentUser?.email.toString().substringBefore('@')
        txt_username.text = username

        updateList()


    }


    private fun updateList(){
        database = Firebase.database.reference

        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        database.child(userId).child("urls").get().addOnSuccessListener {
            if(it.exists()) {
                Log.i("firebase", "Got value ${it.value}")

                val map = it.value
                val array = (map as MutableMap<*, *>).toList().toTypedArray()

                // Create an ArrayAdapter to bind the items to the ListView
                val adapter =
                    ArrayAdapter(this@ProfileActivity, android.R.layout.simple_list_item_1, array)

                // Set the adapter on the ListView
                Url_list.adapter = MyCustomAdapter(this, array, database)

                println(map)
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
            val mainRow = LayoutInflater.inflate(R.layout.home_row, parent, false)
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