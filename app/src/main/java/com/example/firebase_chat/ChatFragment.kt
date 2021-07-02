package com.example.firebase_chat

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-07-02
 * @desc
 */
class ChatFragment : Fragment() {

    val PICK_FROM_ALBUM = 1
    val PICK_FROM_FILE = 2
    val rootPath : String = helpers.rootPath + "/DirectTalk9"
    val sendBtn : Button? = null
    val msg_input : EditText?= null
    val recyclerView: RecyclerView? = null
    val mAdapter : SelectUserActivity.RecyclerViewAdapter? = null
    @SuppressLint("SimpleDataFormat")   val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    @SuppressLint("SimpleDataFormat")   val dateFormatDay = SimpleDateFormat("yyyy-MM-dd ")
    @SuppressLint("SimpleDataFormat")   val dateFormatHour = SimpleDateFormat("aa HH:mm")
    var roomID: String? = null
    var myUid: String? = null
    var toUid: String? = null
    val userList: HashMap<String, UserModel> = hashMapOf<String, UserModel>()
    val listenerRegistration: ListenerRegistration? = null
    val firestore: FirebaseFirestore? = null
    val storageReference: StorageReference? = null
    var linearLayoutManager: LinearLayoutManager? = null
    val progressDialog: ProgressDialog? = null
    val userCount = 0

   companion object{
       fun getunstance(toUid:String?, roomID:String?) : ChatFragment{
           val thisFragment = ChatFragment()
           val bdl = Bundle()
           bdl.putString("toUid", toUid)
           bdl.putString("roomID", roomID)
           thisFragment.arguments = bdl
           return thisFragment
       }
   }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_chat, container, false)

        linearLayoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = linearLayoutManager

        sendBtn.setOnClickListener{
            val msg = msg_input.text.toString()
            sendMessage(msg, "0", null)
            msg_input.setText("")
        }
        imageBtn
    }
}