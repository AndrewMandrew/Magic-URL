package com.magicurl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HomeActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var myListView : ListView
    private lateinit var dataArray: Array<Pair<*, *>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        myListView = findViewById(R.id.list_shorted)

        home_search.setOnClickListener {

            startActivity(Intent(this@HomeActivity, SearchActivity::class.java))
        }
        home_star.setOnClickListener {

            startActivity(Intent(this@HomeActivity, FavouriteActivity::class.java))
        }
        home_profile.setOnClickListener {

            startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
        }



        btn_short.setOnClickListener {
            // Prendere info da textbox
            when {
                TextUtils.isEmpty(et_home_url.text.toString().trim { it <= ' ' }) or
                        !(et_home_url.text.contains(".")) -> {

                    Toast.makeText(
                        this@HomeActivity,
                        "Please enter an URL!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(et_home_name.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@HomeActivity,
                        "Please enter a name for your URL!",
                        Toast.LENGTH_SHORT
                    ).show()
                }


                else -> {
                    magicifyLink().start()

                }
            }
        }

        updateList()

    }

    fun updateList() {
        database = Firebase.database.reference

        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()


        database.child(userId).child("urls").get().addOnSuccessListener {
            if (it.exists()) {
                Log.i("firebase", "Got value ${it.value}")

                val map = it.value
                var array = (map as MutableMap<*, *>).toList().toTypedArray()

                this.dataArray = array.sortedWith(compareBy({ it.first.toString().substringBefore("-") }))
                    .reversed().takeLast(3).toTypedArray()

                myListView.adapter = MyCustomAdapter(this, database)
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun magicifyLink(): Thread {

        return Thread {
            val urlToShorten: String = et_home_url.text.toString().trim { it <= ' ' }

            var name: String = et_home_name.text.toString().trim() { it <= ' ' }
            val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

            name = System.currentTimeMillis().toString() + "-" + name


            val url = URL("https://api.tinyurl.com/create")
            val postData =
                "api_token=BssoQFHyATXqAqm9D78h2qhYOmSwWdWI9Jbdo3sZ4PosD8sexO3DvoDjKdHA&url=$urlToShorten"

            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.setRequestProperty("Content-Length", postData.length.toString())
            conn.useCaches = false

            DataOutputStream(conn.outputStream).use { it.writeBytes(postData) }
            BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    println(line)
                    val tiny_url = JSONObject(line).getJSONObject("data").get("tiny_url")

                    val database = Firebase.database
                    val tiny_url_name = database.getReference(userId).child("urls").child(name)
                    tiny_url_name.setValue(tiny_url)
                }
            }

            et_home_name.setText("")
            et_home_url.setText("")
            closeKeyboard(et_home_url)
            updateList()
        }

    }

    private fun closeKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private class MyCustomAdapter(
        context: HomeActivity,
        database: DatabaseReference
    ) : BaseAdapter() {

        private val mContext: HomeActivity
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
            val mainRow = LayoutInflater.inflate(R.layout.home_row, parent, false)
            val namePosition = mainRow.findViewById<TextView>(R.id.name_textView)
            val urlPosition = mainRow.findViewById<TextView>(R.id.link_textView)


            namePosition.text = mContext.dataArray.get(position).first.toString().substringAfter("-")
            urlPosition.text = mContext.dataArray.get(position).second.toString()

            setListeners(position, mainRow)

            return mainRow
        }

        private fun setListeners(position:Int, mainRow:View){
            val delete = mainRow.findViewById<TextView>(R.id.delete)

            delete.setOnClickListener {
                val deleteElement = mContext.dataArray.get(position).first.toString()
                val newList = mContext.dataArray.toMutableList()

                newList.remove(mContext.dataArray[position])
                mContext.dataArray = newList.toTypedArray()

                db.child(userId).child("urls").child(deleteElement).removeValue()

                this.notifyDataSetChanged()
            }
        }
    }
}