package com.example.firebase_chat

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-07-02
 * @desc
 */
class ChatFragment : Fragment() {

    val rootPath = Util9.getRootPath() + "/DirectTalk9/"
    val PICK_FROM_ALBUM = 1
    val PICK_FROM_FILE = 2
    val sendBtn : Button? = null
    val imageBtn : Button? = null
    val fileBtn : Button? = null
    val msg_input : EditText?= null
    val recyclerView: RecyclerView? = null
    var mAdapter : SelectUserActivity.RecyclerViewAdapter? = null
    @SuppressLint("SimpleDataFormat")   val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    @SuppressLint("SimpleDataFormat")   val dateFormatDay = SimpleDateFormat("yyyy-MM-dd ")
    @SuppressLint("SimpleDataFormat")   val dateFormatHour = SimpleDateFormat("aa HH:mm")
    var roomID: String? = null
    var myUid: String? = null
    var toUid: String? = null
    val userList: HashMap<String, UserModel> = hashMapOf<String, UserModel>()
    val listenerRegistration: ListenerRegistration? = null
    var firestore: FirebaseFirestore? = null
    var storageReference: StorageReference? = null
    var linearLayoutManager: LinearLayoutManager? = null
    val progressDialog: ProgressDialog? = null
    var userCount = 0

   companion object{
       fun getinstance(toUid: String?, roomID: String?) : ChatFragment{
           val thisFragment = ChatFragment()
           val bdl = Bundle()
           bdl.putString("toUid", toUid)
           bdl.putString("roomID", roomID)
           thisFragment.arguments = bdl
           return thisFragment
       }
   }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_chat, container, false)

        linearLayoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = linearLayoutManager

        sendBtn?.setOnClickListener{
            val msg = msg_input?.text.toString()
            sendMessage(msg, "0", null)
            msg_input?.setText("")
        }
        imageBtn?.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Audio.Media.CONTENT_TYPE
            startActivityForResult(intent, PICK_FROM_ALBUM)
        }
        fileBtn?.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_FILE)
        }
        msg_input?.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus)
                Util9.hideKeyboard(requireActivity())
        }
        if(arguments != null) {
            roomID = arguments?.getString("roomID")
            toUid = arguments?.getString("toUid")
        }
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        dateFormatDay.timeZone = TimeZone.getTimeZone("Asia/seoul")
        dateFormatHour.timeZone = TimeZone.getTimeZone("Asia/seoul")

        myUid = FirebaseAuth.getInstance().currentUser?.uid

        if("" != toUid && toUid != null) {
            findChatRoom(toUid!!)
        } else if ("" != roomID && roomID != null) {
            setChatRoom(roomID!!)
        }
        if (roomID == null) {

            getUserInfoFromServer(myUid)
            getUserInfoFromServer(toUid)
            userCount = 2
        }

        recyclerView?.addOnLayoutChangeListener{ _, _, _, bottom, _, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                val lastAdapterItem = mAdapter!!.itemCount -1
                recyclerView?.post{
                    var recyclerViewPointOffset = -1000000
                    val bottomView = linearLayoutManager?.findViewByPosition(lastAdapterItem)
                    if (bottomView != null) recyclerViewPointOffset = 0 - bottomView.height
                    linearLayoutManager?.scrollToPositionWithOffset(lastAdapterItem, recyclerViewPointOffset)
                }
            }
        }
        return view
    }

    private fun sendMessage(msg: String, msgtype: String, fileinfo: ChatModel.FileInfo?) {
        sendBtn!!.isEnabled = false
        if (roomID == null) {             // create chatting room for two user
            roomID = firestore!!.collection("rooms").document().id
            CreateChattingRoom(firestore!!.collection("rooms").document(roomID!!))
        }
        val messages = hashMapOf<String, Any?>()
        messages["uid"] = myUid
        messages["msg"] = msg
        messages["msgtype"] = msgtype
        messages["timestamp"] = FieldValue.serverTimestamp()
        if (fileinfo != null) {
            messages["filename"] = fileinfo.filename
            messages["filesize"] = fileinfo.filesize
        }
        val docRef = firestore!!.collection("rooms").document(roomID!!)

        docRef.get().addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
                val batch = firestore!!.batch()
                // save last message
                batch[docRef, messages] = SetOptions.merge()
                // save message
                val readUsers: MutableList<String?> = mutableListOf()
                readUsers.add(myUid)
                messages["readUsers"] = readUsers //new String[]{myUid} );
                batch.set(docRef.collection("messages").document(), messages)

                // inc unread message count
                val document = it.result
                val users = document!!["users"] as MutableMap<String, Long>?
                for (key in users!!.keys) {
                    if (myUid != key) users[key] = users[key]!! + 1
                }
                document.reference.update("users", users)
                batch.commit().addOnCompleteListener { it2 ->
                    if (it2.isSuccessful) {
                        //sendGCM();
                        sendBtn.isEnabled = true
                    }
                }
            }
        }
    }

    fun CreateChattingRoom(room: DocumentReference) {
        val users = hashMapOf<String, Int>()
        val title = ""
        for (key in userList.keys) users[key] = 0
        val data = hashMapOf<String, Any?>()
        data["title"] = null
        data["users"] = users
        room.set(data).addOnCompleteListener {
            if (it.isSuccessful) {
                mAdapter = RecyclerViewAdapter()
                recyclerView!!.adapter = mAdapter
            }
        }
    }

    fun findChatRoom(toUid: String) {
        firestore!!.collection("rooms").whereGreaterThanOrEqualTo("users.$myUid", 0).get()
                .addOnCompleteListener{
                    if (!it.isSuccessful) {
                        return@addOnCompleteListener
                    }
                    for (document in it.result!!) {
                        val users = document.get("users") as Map<String, Long>?
                        if (users?.size == 2 && users[toUid]?.toInt() != null) {
                            setChatRoom(document.id)
                            break
                        }
                    }
                }
    }

    fun setChatRoom(rid: String) {
        roomID = rid
        firestore!!.collection("rooms").document(roomID!!).get().addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            val document = it.result
            val users = document!!["users"] as Map<String, Long>?
            for (key in users!!.keys) {
                getUserInfoFromServer(key)
            }
            userCount = users.size
            //users.put(myUid, (long) 0);
            //document.getReference().update("users", users);
        }
    }

    fun getUserInfoFromServer(id: String?) {
        firestore!!.collection("users").document(id!!).get().addOnSuccessListener {
            val userModel = it.toObject(
                    UserModel::class.java
            )
            userList[userModel?.uid!!] = userModel
            if (roomID != null!! && userCount == userList.size) {
                mAdapter = RecyclerViewAdapter()
                recyclerView!!.adapter = mAdapter
            }
        }
    }
}