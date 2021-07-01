package com.example.firebase_chat

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-07-01
 * @desc
 */
class LoginActivity : AppCompatActivity() {
    private var user_id: EditText? = null
    private var user_pw: EditText? = null
    var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        user_id = findViewById(R.id.user_id)
        user_pw = findViewById(R.id.user_pw)
        val loginBtn: Button = findViewById(R.id.loginBtn)
        val signupBtn: Button = findViewById(R.id.signupBtn)

        loginBtn.setOnClickListener {
            if (validateForm()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    user_id!!.text.toString(),
                    user_pw!!.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sharedPreferences!!.edit()
                            .putString("user_id", user_id!!.text.toString()).apply()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Util9.showMessage(getApplicationContext(), task.exception!!.message)
                    }
                }
            }
        }


        signupBtn.setOnClickListener {
            if (validateForm()) {
                val id = user_id!!.text.toString()

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(id, user_pw!!.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            sharedPreferences!!.edit().putString("user_id", id).apply()
                            val uid = FirebaseAuth.getInstance().uid
                            val userModel = UserModel()
                            userModel.uid = (uid)
                            userModel.userid = (id)
                            userModel.usernm = (extractIDFromEmail(id))
                            userModel.usermsg = ("...")

                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(uid!!)
                                .set(userModel)
                                .addOnSuccessListener {
                                    val intent =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    Log.d("로그인 액티비티 버그 체크", "DocumentSnapshot added with ID: $uid")
                                }
                        } else {
                            Util9.showMessage(
                                applicationContext,
                                task.exception?.message!!
                            )
                        }
                    }
            }
        }

        sharedPreferences = getSharedPreferences("gujc", Activity.MODE_PRIVATE)
        val id = sharedPreferences!!.getString("user_id", "")
        if ("" != id) {
            user_id!!.setText(id)
        }
    }


    fun extractIDFromEmail(email: String): String {
        val parts = email.split("@").toTypedArray()
        return parts[0]
    }

    private fun validateForm(): Boolean {
        var valid = true
        val email = user_id!!.text.toString()
        if (TextUtils.isEmpty(email)) {
            user_id!!.error = "Required."
            valid = false
        } else {
            user_id!!.error = null
        }
        val password = user_pw!!.text.toString()
        if (TextUtils.isEmpty(password)) {
            user_pw!!.error = "Required."
            valid = false
        } else {
            user_pw!!.error = null
        }
        return valid
    }
}
