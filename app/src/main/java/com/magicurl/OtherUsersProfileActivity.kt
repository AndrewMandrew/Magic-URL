package com.magicurl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_other_users_profile.*

class OtherUsersProfileActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var myListView : ListView
    private lateinit var dataArray: Array<Pair<*, *>>
    private lateinit var myFavouriteUrls: Array<Pair<*, *>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_users_profile)
        myListView = findViewById(R.id.other_user_list)

        myFavouriteUrls = arrayOf()


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
        //var skipControl = false

        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        //val currentUser = database.child(userId).child("username").get().toString()

        database.child(userId).child("favourites").get().addOnSuccessListener {
            if (it.exists()) {
                Log.i("firebase", "Got value ${it.value}")

                val map = it.value
                var array = (map as MutableMap<*, *>).toList().toTypedArray()

                this.myFavouriteUrls = array.sortedWith(compareBy({ it.first.toString().substringBefore("-") }))
                    .reversed().toTypedArray()

                //skipControl = true
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

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


                database.child(foundKey).child("urls").get().addOnSuccessListener {
                    if (it.exists()) {
                        Log.i("firebase", "Got value ${it.value}")

                        val map = it.value
                        val array = (map as MutableMap<*, *>).toList().toTypedArray()

                        this.dataArray = array.sortedWith(compareBy({ it.first.toString().substringBefore("-") }))
                            .reversed().toTypedArray()

                        myListView.adapter = OtherUsersProfileActivity.MyCustomAdapter(this, database)
                    }

                }.addOnFailureListener {
                    Log.e("firebase", "Error getting data", it)
                }


            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }


    }

    private class MyCustomAdapter(
        context: OtherUsersProfileActivity,
        database: DatabaseReference
    ) : BaseAdapter() {

        private val mContext: OtherUsersProfileActivity
        private val db: DatabaseReference
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()


        init {
            this.mContext = context
            this.db = database
        }


        //responsible for the number of rows in my list
        override fun getCount(): Int {
            return mContext.dataArray.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return "TEST"
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val LayoutInflater = LayoutInflater.from(mContext)
            val mainRow = LayoutInflater.inflate(R.layout.user_links, parent, false)
            val namePosition = mainRow.findViewById<TextView>(R.id.name_other_textView)
            val urlPosition = mainRow.findViewById<TextView>(R.id.link_other_textView)
            val myImage = mainRow.findViewById<ImageView>(R.id.favourite)
            var isFavourite = false

            namePosition.text = mContext.dataArray.get(position).first.toString().substringAfter("-")
            urlPosition.text = mContext.dataArray.get(position).second.toString()

            if (mContext.myFavouriteUrls.isNotEmpty() and mContext.myFavouriteUrls.contains(mContext.dataArray.get(position))){
                myImage.setImageResource(R.drawable.ic_star_yellow)
                isFavourite = true
            }


            setListeners(position, mainRow, isFavourite)

            return mainRow
        }

        private fun setListeners(position:Int, mainRow:View, isFavourite: Boolean){

            val favouritePosition = mainRow.findViewById<ImageView>(R.id.favourite)
            favouritePosition.setOnClickListener {

                if (isFavourite) {
                    val deleteElement = mContext.dataArray.get(position).first.toString()
                    val newList = mContext.myFavouriteUrls.toMutableList()

                    newList.remove(mContext.dataArray.get(position))

                    mContext.myFavouriteUrls = newList.toTypedArray()

                    db.child(userId).child("favourites").child(deleteElement).removeValue()

                    this.notifyDataSetChanged()
                }
                else{
                    val fav = mContext.dataArray.get(position)
                    val newList = mContext.myFavouriteUrls.toMutableList()

                    newList.add(fav)
                    mContext.myFavouriteUrls = newList.toTypedArray()

                    val database = Firebase.database
                    val tiny_url_name = database.getReference(userId).child("favourites")
                        .child(fav.first.toString())
                    tiny_url_name.setValue(fav.second.toString())

                    this.notifyDataSetChanged()
                }
            }
        }
    }
}