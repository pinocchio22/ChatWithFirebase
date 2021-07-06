package com.example.firebase_chat

import java.util.*

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-07-06
 * @desc
 */
class Message {
    internal var uid: String? = null
    internal var msg: String? = null
    internal var msgtype // 0: msg, 1: image, 2: file
            : String? = null
    internal var timestamp: Date? = null
    internal var readUsers: MutableList<String>? = null
    internal var filename: String? = null
    internal var filesize: String? = null
    fun getUid(): String? {
        return uid
    }

    fun setUid(uid: String?) {
        this.uid = uid
    }

    fun getMsg(): String? {
        return msg
    }

    fun setMsg(msg: String?) {
        this.msg = msg
    }

    fun getMsgtype(): String? {
        return msgtype
    }

    fun setMsgtype(msgtype: String?) {
        this.msgtype = msgtype
    }

    fun getTimestamp(): Date? {
        return timestamp
    }

    fun setTimestamp(timestamp: Date?) {
        this.timestamp = timestamp
    }

    fun getReadUsers(): MutableList<String> {
        return readUsers!!
    }

    fun setReadUsers(readUsers: MutableList<String>) {
        this.readUsers = readUsers
    }

    fun getFilename(): String? {
        return filename
    }

    fun setFilename(filename: String?) {
        this.filename = filename
    }

    fun getFilesize(): String? {
        return filesize
    }

    fun setFilesize(filesize: String?) {
        this.filesize = filesize
    }
}
