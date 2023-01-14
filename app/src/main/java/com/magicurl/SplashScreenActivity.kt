package com.magicurl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.magicurl.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var db : AppDatabase
    private lateinit var user : List<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        db = AppDatabase.getDatabase(this)
        user = listOf()

        lifecycleScope.launch(Dispatchers.IO){
            user = db.userDao().getAll()
        }.start()

        cappello.alpha = 0f
        cappello.animate().setDuration(1500).alpha(1f).withEndAction {
        if(user.isNotEmpty()){
            val email = user.first().email.toString()
            val password = user.first().password.toString()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                        val i = Intent(this, HomeActivity::class.java)
                        startActivity(i)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                }
        }else {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        }

    }

}