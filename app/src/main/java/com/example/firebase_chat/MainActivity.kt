package com.example.firebase_chat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.firebase_chat.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mViewPager: ViewPager? = null
    private var makeRoomBtn: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)


        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container) as ViewPager
        binding.container.adapter = mSectionsPagerAdapter
        val tabLayout = binding.tabs
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 1) {     // char room
                    binding.makeRoomBtn.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                makeRoomBtn!!.visibility = View.INVISIBLE
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        mViewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(mViewPager))
        sendRegistrationToServer()
        makeRoomBtn = findViewById(R.id.makeRoomBtn)
        makeRoomBtn!!.visibility = View.INVISIBLE
        makeRoomBtn!!.setOnClickListener{ v ->
            startActivity(Intent(v.context, SelectUserActivity::class.java))
        }
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


   class SectionsPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
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
