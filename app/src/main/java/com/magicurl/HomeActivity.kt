package com.magicurl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class HomeActivity : AppCompatActivity() {
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
                    et_home_name.setText("")
                    et_home_url.setText("")

                }
            }

            // Chiamare API

            // Mostrare link short

        }


    }

    private fun magicifyLink(): Thread
    {

        return Thread {
            val urlToShorten: String = et_home_url.text.toString().trim { it <= ' ' }

            val name: String = et_home_name.text.toString().trim() { it <= ' ' }
            val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()


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
                    val tiny_url_name = database.getReference("urls").child(userId).child(name)
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

        }

    }
}