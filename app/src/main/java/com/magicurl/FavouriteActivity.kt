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
import kotlinx.android.synthetic.main.activity_favourite.*
import kotlinx.android.synthetic.main.activity_home.*


class FavouriteActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var myListView : ListView
    private lateinit var dataArray: Array<Pair<*, *>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)
        myListView = findViewById(R.id.list_fav)

        fav_home.setOnClickListener {

            startActivity(Intent(this@FavouriteActivity, HomeActivity::class.java))
        }
        fav_search.setOnClickListener {

            startActivity(Intent(this@FavouriteActivity, SearchActivity::class.java))
        }

        fav_profile.setOnClickListener {

            startActivity(Intent(this@FavouriteActivity, ProfileActivity::class.java))
        }

        database = Firebase.database.reference

        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        database.child(userId).child("favourites").get().addOnSuccessListener {
            if (it.exists()) {
                Log.i("firebase", "Got value ${it.value}")

                val map = it.value
                var array = (map as MutableMap<*, *>).toList().toTypedArray()

                this.dataArray = array.sortedWith(compareBy({ it.first.toString().substringBefore("-") }))
                    .reversed().toTypedArray()

                myListView.adapter = FavouriteActivity.MyCustomAdapter(this, database)
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }


    }
    private class MyCustomAdapter(
        context: FavouriteActivity,
        database: DatabaseReference
    ) : BaseAdapter() {

        private val mContext: FavouriteActivity
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


            namePosition.text = mContext.dataArray.get(position).first.toString().substringAfter("-")
            urlPosition.text = mContext.dataArray.get(position).second.toString()

            setListeners(position, mainRow)

            return mainRow
        }

        private fun setListeners(position:Int, mainRow:View){
            val delete = mainRow.findViewById<ImageView>(R.id.favourite)

            delete.setOnClickListener {
                val deleteElement = mContext.dataArray.get(position).first.toString()
                val newList = mContext.dataArray.toMutableList()

                newList.remove(mContext.dataArray[position])
                mContext.dataArray = newList.toTypedArray()

                db.child(userId).child("favourites").child(deleteElement).removeValue()

                this.notifyDataSetChanged()
            }

        }
    }

}