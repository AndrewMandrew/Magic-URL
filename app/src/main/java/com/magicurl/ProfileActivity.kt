package com.magicurl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile.*



class ProfileActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var myListView : ListView
    private lateinit var dataArray: Array<Pair<*, *>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        myListView = findViewById(R.id.Url_list)

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

                this.dataArray = array.sortedWith(compareBy({ it.first.toString().substringBefore("-") }))
                    .reversed().toTypedArray()

                // Set the adapter on the ListView
                myListView.adapter = MyCustomAdapter(this, database)

                println(map)
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
    }

    private class MyCustomAdapter(
        context: ProfileActivity,
        database: DatabaseReference
    ) : BaseAdapter() {

        private val mContext: ProfileActivity
        private val db: DatabaseReference
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()


        init {
            this.mContext = context
            this.db = database

        }

        fun updateList() {
            this.notifyDataSetChanged()
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
            val mainRow = LayoutInflater.inflate(R.layout.home_row, parent, false)
            val namePosition = mainRow.findViewById<TextView>(R.id.name_textView)
            val urlPosition = mainRow.findViewById<TextView>(R.id.link_other_textView)


            namePosition.text = mContext.dataArray.get(position).first.toString().substringAfter("-")
            urlPosition.text = mContext.dataArray.get(position).second.toString()

            setListeners(position, mainRow)

            return mainRow
        }

        private fun setListeners(position:Int, mainRow:View){
            val delete = mainRow.findViewById<TextView>(R.id.delete)
            val modify = mainRow.findViewById<TextView>(R.id.modify)

            delete.setOnClickListener {
                val builder = AlertDialog.Builder(mContext)


                with(builder){
                    setTitle("Are you sure you want to delete this URL?")
                    setPositiveButton("Ok"){ dialog, which->

                        val deleteElement = mContext.dataArray.get(position).first.toString()
                        val newList = mContext.dataArray.toMutableList()

                        newList.remove(mContext.dataArray[position])
                        mContext.dataArray = newList.toTypedArray()

                        db.child(userId).child("urls").child(deleteElement).removeValue()
                        updateList()
                    }
                    setNegativeButton("Cancel"){dialog, which ->
                        Log.d("Main", "Negative button clicked.")
                    }
                    show()

                }

            }

            modify.setOnClickListener {
                var modifyElement = mContext.dataArray.get(position)

                val builder = AlertDialog.Builder(mContext)
                val LayoutInflater = LayoutInflater.from(mContext)
                val dialogLayout = LayoutInflater.inflate(R.layout.popup_edit_text, null)
                val editText = dialogLayout.findViewById<EditText>(R.id.edit_url)

                var modifiedName:String


                with(builder){
                    setTitle("Insert new name")
                    setPositiveButton("Ok"){ dialog, which->
                        modifiedName = editText.text.toString()
                        modifiedName = modifyElement.first.toString().substringBefore("-") + "-" + modifiedName

                        db.child(userId).child("urls").child(modifyElement.first.toString()).removeValue()

                        modifyElement = Pair(modifiedName, modifyElement.second.toString())


                        val tiny_url_name = db.child(userId).child("urls").child(modifyElement.first.toString())
                        tiny_url_name.setValue(modifyElement.second.toString())

                        val modifiedList = mContext.dataArray.toMutableList()

                        modifiedList.remove(mContext.dataArray[position])
                        modifiedList.add(modifyElement)

                        mContext.dataArray = modifiedList.sortedWith(compareBy({ it.first.toString().substringBefore("-") }))
                            .reversed().takeLast(3).toTypedArray()

                        updateList()


                    }
                    setNegativeButton("Cancel"){dialog, which ->
                        Log.d("Main", "Negative button clicked.")
                    }
                    setView(dialogLayout)
                    show()

                }
            }


        }
    }
}