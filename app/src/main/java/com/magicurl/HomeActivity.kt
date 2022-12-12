package com.magicurl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
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

                else -> {
                    magicifyLink().start()
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
                    println(line)
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