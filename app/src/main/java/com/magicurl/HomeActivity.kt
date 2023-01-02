package com.magicurl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.home_row.view.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.max

class HomeActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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
                TextUtils.isEmpty(et_home_url.text.toString().trim {it <= ' '}) -> {
                    Toast.makeText(
                        this@HomeActivity,
                        "Please enter an URL!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(et_home_name.text.toString().trim {it <= ' '}) -> {
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

    private fun updateList(){
        database = Firebase.database.reference

        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()


            database.child(userId).child("urls").get().addOnSuccessListener {
            if(it.exists()) {
                Log.i("firebase", "Got value ${it.value}")

                val map = it.value
                var array = (map as MutableMap<*, *>).toList().toTypedArray()

                array = array.sortedWith(compareBy({it.first.toString().substringBefore("-")})).takeLast(3).reversed().toTypedArray()

                // Create an ArrayAdapter to bind the items to the ListView
                //val adapter =
                    //ArrayAdapter(this@HomeActivity, android.R.layout.simple_list_item_1, array)

                // Set the adapter on the ListView
                list_shorted.adapter = MyCustomAdapter(this, array)

                println(array)
            }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

    }

    private fun magicifyLink(): Thread
    {

        return Thread {
            val urlToShorten: String = et_home_url.text.toString().trim { it <= ' ' }

            var name: String = et_home_name.text.toString().trim() { it <= ' ' }
            val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

            name = System.currentTimeMillis().toString() + "-" + name


            val url = URL("https://api.tinyurl.com/create")
            val postData = "api_token=BssoQFHyATXqAqm9D78h2qhYOmSwWdWI9Jbdo3sZ4PosD8sexO3DvoDjKdHA&url=$urlToShorten"

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
                    // {"data":{"domain":"tinyurl.com","alias":"2tadbyva","deleted":false,"archived":false,"analytics":{"enabled":true,"public":false},"tags":[],"created_at":"2022-12-21T19:38:06+00:00","expires_at":null,"tiny_url":"https:\/\/tinyurl.com\/2tadbyva","url":"http:\/\/lol.com"},"code":0,"errors":[]}
                    println(line)
                    val tiny_url = JSONObject(line).getJSONObject("data").get("tiny_url")

                    val database = Firebase.database
                    val tiny_url_name = database.getReference(userId).child("urls").child(name)
                    tiny_url_name.setValue(tiny_url)

                }

            }

            /*
            if(conn.responseCode == 200){
                val response = conn.inputStream
                println(response.toString())
            }
            else{
                println(conn.responseCode)
            }
             */

            et_home_name.setText("")
            et_home_url.setText("")
            closeKeyboard(et_home_url)
        }

    }

    private fun closeKeyboard(view: View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private class MyCustomAdapter(context: Context, array: Array<Pair<*,*>>): BaseAdapter() {

        private val mContext: Context
        private val linkArray: Array<Pair<*,*>>


        init {
            this.mContext = context
            this.linkArray = array
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

            namePosition.text = linkArray.get(position).first.toString().substringAfter("-")
            urlPosition.text = linkArray.get(position).second.toString()
            return mainRow
        }

    }
}