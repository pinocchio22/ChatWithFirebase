package com.example.firebase_chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.firebase_chat.databinding.FragmentUserBinding
import com.example.firebase_chat.databinding.FragmentUserlistinroomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-07-08
 * @desc
 */

@Suppress("DEPRECATION")
class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val PICK_FROM_ALBUM = 1
    }

    private var user_photo: ImageView? = null
    private var user_id: EditText? = null
    private var user_name: EditText? = null
    private var user_msg: EditText? = null
    private var userModel: UserModel? = null
    private var userPhotoUri: Uri? = null

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root

        user_id?.isEnabled = false
        getUserInfoFromServer()
        return view
    }

    private fun getUserInfoFromServer() {

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        Log.d("log", FirebaseAuth.getInstance().currentUser!!.uid)

        val docRef = FirebaseFirestore.getInstance().collection("users").document(uid)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            userModel = documentSnapshot.toObject<UserModel>(UserModel::class.java)
            user_id!!.setText(userModel?.userid)
            user_name!!.setText(userModel?.usernm)
            user_msg!!.setText(userModel?.usermsg)
            if (userModel?.userphoto != null && "" != userModel?.userphoto) {
                Glide.with(requireActivity()).load(FirebaseStorage.getInstance()
                    .getReference("userPhoto/" + userModel?.userphoto)).into(user_photo!!)
            }
        }
    }

//    override var userPhotoIVClickListener = View.OnClickListener {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = MediaStore.Images.Media.CONTENT_TYPE
//        startActivityForResult(intent, PICK_FROM_ALBUM)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            user_photo!!.setImageURI(data?.data)
            userPhotoUri = data?.data
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val userName = user_name!!.text.toString()
        if (TextUtils.isEmpty(userName)) {
            user_name!!.error = "Required."
            valid = false
        } else {
            user_name!!.error = null
        }
        val userMsg = user_msg!!.text.toString()
        if (TextUtils.isEmpty(userMsg)) {
            user_msg!!.error = "Required."
            valid = false
        } else {
            user_msg!!.error = null
        }
        Util9.hideKeyboard(requireActivity())
        return valid
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        user_id = view.findViewById(R.id.user_id)
        user_name = view.findViewById(R.id.user_name)
        user_msg = view.findViewById(R.id.user_msg)

        super.onViewCreated(view, savedInstanceState)

        val saveBtn = view.findViewById<Button>(R.id.saveBtn)
        val changePWBtn = view.findViewById<Button>(R.id.changePWBtn)

        user_photo?.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, PICK_FROM_ALBUM)
        }
        saveBtn.setOnClickListener {
            if (!validateForm()) return@setOnClickListener

            userModel?.usernm = user_name!!.text.toString()
            userModel?.usermsg = user_msg!!.text.toString()
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val db = FirebaseFirestore.getInstance()

            if (userPhotoUri != null) userModel!!.userphoto = uid

            db.collection("users").document(uid)
                .set(userModel!!)
                .addOnSuccessListener {
                    if (userPhotoUri == null) {
                        Util9.showMessage(requireActivity(), "Success to Save.")
                    } else {
                        // small image
                        Glide.with(requireContext())
                            .asBitmap().load(userPhotoUri).apply(RequestOptions().override(150,
                                150))
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(
                                    bitmap: Bitmap,
                                    transition: Transition<in Bitmap>?,
                                ) {
                                    val baos = ByteArrayOutputStream()
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                    val data = baos.toByteArray()
                                    FirebaseStorage.getInstance().reference.child("userPhoto/$uid")
                                        .putBytes(data)
                                    Util9.showMessage(activity!!, "Success to Save.")
                                }
                            })
                    }
                }
        }
        changePWBtn.setOnClickListener{
            startActivity(Intent(activity, UserPWActivity::class.java))
        }
    }
}
