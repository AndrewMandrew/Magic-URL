package com.magicurl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
                val mListView = findViewById<ListView>(R.id.user_list)

                mListView.adapter = MyCustomAdapter(this, database)
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

    }

    private class MyCustomAdapter(
        context: SearchActivity,
        database: DatabaseReference
    ) : BaseAdapter() {

        private val mContext: SearchActivity
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
            val mainRow = LayoutInflater.inflate(R.layout.search_row, parent, false)
            val namePosition = mainRow.findViewById<TextView>(R.id.name_textView)

            var isFavourite = false

            namePosition.text = mContext.dataArray.get(position)





            setListeners(position, mainRow, isFavourite)

            return mainRow
        }

        private fun setListeners(position:Int, mainRow:View, isFavourite: Boolean){


                }
            }
        }
