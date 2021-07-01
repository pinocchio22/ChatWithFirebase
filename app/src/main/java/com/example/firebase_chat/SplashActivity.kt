package com.example.firebase_chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-07-01
 * @desc
 */
class SplashActivity : Activity() {
    private val SPLASH_DISPLAY_LENGTH = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //FirebaseAuth.getInstance().signOut();
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder().build()
        firestore.firestoreSettings = settings

        Handler().postDelayed({
            var mainIntent: Intent? = null
            mainIntent = if (FirebaseAuth.getInstance().currentUser == null) {
                Intent(this@SplashActivity, LoginActivity::class.java)
            } else {
                Intent(this@SplashActivity, MainActivity::class.java)
            }
            this@SplashActivity.startActivity(mainIntent)
            this@SplashActivity.finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}