package com.example.firebase_chat

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.firebase_chat.databinding.ActivityViewPagerBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*
import com.github.chrisbanes.photoview.PhotoView


/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-07-06
 * @desc
 */
class ViewPagerActivity : AppCompatActivity() {

    companion object {
        lateinit var roomID: String
        lateinit var realname: String
        lateinit var viewPager: ViewPager
        lateinit var imgList : ArrayList<Message>
    }

    private val rootPath = Util9.getRootPath() + "/DirectTalk9/"

    private lateinit var binding: ActivityViewPagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPagerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar: Toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        roomID = getIntent().getStringExtra("roomID")!!
        realname = getIntent().getStringExtra("realname")!!
        viewPager = findViewById(R.id.view_pager)
        viewPager.setAdapter(SamplePagerAdapter())

       binding.downloadBtn.setOnClickListener{
           if (!Util9.isPermissionGranted((view.context as Activity), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
               return@setOnClickListener
               val message = imgList[viewPager.getCurrentItem()]
               /// showProgressDialog("Downloading File.");
               val localFile = File(rootPath, message.getFilename())

               // realname == message.msg
               FirebaseStorage.getInstance().reference.child("files/" + message.getMsg()).getFile(localFile).addOnSuccessListener { // hideProgressDialog();
                   Util9.showMessage(view.context, "Downloaded file")
                   Log.e("DirectTalk9 ", "local file created $localFile")
               }.addOnFailureListener{
                   Log.e("DirectTalk9 ", "local file not created  $it") }
       }

        val actionBar = supportActionBar
        //actionBar.setIcon(R.drawable.back);
        actionBar?.setTitle("PhotoView")
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeButtonEnabled(true)
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    }

    inner class SamplePagerAdapter : PagerAdapter() {
        private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        private var inx = -1

        init {
            FirebaseFirestore.getInstance().collection("rooms").document(roomID!!).collection("messages").whereEqualTo("msgtype", "1")
                    .get()
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }
                        for (document in task.result!!) {
                            val message = document.toObject(Message::class.java)
                            imgList.add(message)
                            if (realname == message.getMsg()) {
                                inx = imgList.size - 1
                            }
                        }
                        notifyDataSetChanged()
                        if (inx > -1) {
                            viewPager.setCurrentItem(inx)
                        }
                    })
        }
        override fun getCount(): Int {
            return imgList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)
            Glide.with(container.context)
                    .load(storageReference.child("filesmall/" + imgList[position].getMsg()))
                    .into(photoView)
            container.addView(photoView, MATCH_PARENT, MATCH_PARENT)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View?)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }
}
