package com.example.firebase_chat

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-07-08
 * @desc
 */
class UserPWActivity : AppCompatActivity() {
    private var user_pw1: EditText? = null
    private var user_pw2: EditText? = null
    private var saveBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userpw)

        user_pw1 = findViewById(R.id.user_pw1)
        user_pw2 = findViewById(R.id.user_pw2)


        saveBtn!!.setOnClickListener {

            val pw1 = user_pw1!!.text.toString().trim { it <= ' ' }
            if (pw1.length < 8) {
                Util9.showMessage(applicationContext, "Please enter at least eight characters.")
                return@setOnClickListener
            }
            if (pw1 != user_pw2!!.text.toString().trim { it <= ' ' }) {
                Util9.showMessage(
                    applicationContext,
                    "Password does not match the confirm password."
                )
                return@setOnClickListener
            }
            val user = FirebaseAuth.getInstance().currentUser
            user!!.updatePassword(pw1).addOnCompleteListener {
                Util9.showMessage(applicationContext, "Password changed")
                val imm =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(user_pw2!!.windowToken, 0)
                onBackPressed()
            }
        }
    }
}
