package com.magicurl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_register.*

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        btn_update_pass.setOnClickListener {
            when {
                TextUtils.isEmpty(et_update_mail.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    val email: String = et_register_email.text.toString().trim { it <= ' ' }

                    FirebaseAuth.getInstance().setLanguageCode("en")
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Email sent",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                }
            }

        }


        tv_login2.setOnClickListener {

            onBackPressed()
        }
    }
}