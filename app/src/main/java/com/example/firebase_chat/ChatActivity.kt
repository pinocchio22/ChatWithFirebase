package com.example.firebase_chat

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-07-02
 * @desc
 */
class ChatActivity : AppCompatActivity() {
    private var drawerLayout: DrawerLayout? = null
    private var chatFragment: ChatFragment? = null
    private var userListInRoomFragment: UserListInRoomFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)
        val toUid: String = intent.getStringExtra("toUid")
        val roomID: String = intent.getStringExtra("roomID")
        val roomTitle: String = intent.getStringExtra("roomTitle")
        actionBar.setTitle(roomTitle)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        rightMenuBtn.setOnClickListener(View.OnClickListener {
            if (drawerLayout!!.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout!!.closeDrawer(Gravity.RIGHT)
            } else {
                if (userListInRoomFragment == null) {
                    userListInRoomFragment =
                        UserListInRoomFragment.getInstance(roomID, chatFragment.getUserList())
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.drawerFragment, userListInRoomFragment)
                        .commit()
                }
                drawerLayout!!.openDrawer(Gravity.RIGHT)
            }
        })
        // chatting area
        chatFragment = ChatFragment.getInstance(toUid, roomID)
        supportFragmentManager.beginTransaction().replace(R.id.mainFragment, chatFragment).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        chatFragment.backPressed()
        finish()
    }
}
