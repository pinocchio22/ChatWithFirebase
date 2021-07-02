package com.example.firebase_chat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*

class MainActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mViewPager: ViewPager? = null
    private var makeRoomBtn: FloatingActionButton = findViewById(R.id.makeRoomBtn)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        mViewPager?.adapter = mSectionsPagerAdapter
        val tabLayout: TabLayout = findViewById(R.id.tabs) as TabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener() {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.getPosition() == 1) {     // char room
                    makeRoomBtn.setVisibility(View.VISIBLE)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                makeRoomBtn.visibility = View.INVISIBLE
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        mViewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(mViewPager))
        sendRegistrationToServer()
        makeRoomBtn.visibility = View.INVISIBLE
        makeRoomBtn.setOnClickListener(View.OnClickListener { v ->
            startActivity(
                Intent(
                    v.context,
                    SelectUserActivity::class.java
                )
            )
        })
    }

    internal fun sendRegistrationToServer() {
        val uid =
            FirebaseAuth.getInstance().currentUser!!.uid
        val token =
            FirebaseInstanceId.getInstance().token
        val map = mutableMapOf<String, String?>()
        map["token"] = token
        FirebaseFirestore.getInstance().collection("users")
            .document(uid).set(map, SetOptions.merge())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


   class SectionsPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> UserListFragment()
                1 -> ChatRoomFragment()
                else -> UserFragment()
            }
        }

        override fun getCount(): Int{
            return 3
        }
    }
}
