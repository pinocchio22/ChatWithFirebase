package com.example.firebase_chat

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-07-06
 * @desc
 */
class ChatModel {

    var users : Map<String, String> = HashMap()
    var message : Map<String, String> = HashMap()

    class FileInfo{
        var filename : String? = null
        var filesize : String? = null
    }
}