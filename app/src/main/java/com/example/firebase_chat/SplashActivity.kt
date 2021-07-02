package com.example.firebase_chat

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
//            window.setFlags(
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }

        //FirebaseAuth.getInstance().signOut();
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder().build()
        firestore.firestoreSettings = settings

        Handler(Looper.getMainLooper()).postDelayed({   // handler -> looper.getmainlooper
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