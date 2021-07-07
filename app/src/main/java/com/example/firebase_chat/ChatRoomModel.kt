package com.example.firebase_chat

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-07-07
 * @desc
 */
class ChatRoomModel {
    internal var roomID: String? = null
    internal var title: String? = null
    internal var photo: String? = null
    internal var lastMsg: String? = null
    internal var lastDatetime: String? = null
    internal var userCount: Int? = null
    internal var unreadCount: Int? = null
    fun getRoomID(): String? {
        return roomID
    }

    fun setRoomID(roomID: String?) {
        this.roomID = roomID
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getPhoto(): String? {
        return photo
    }

    fun setPhoto(photo: String?) {
        this.photo = photo
    }

    fun getLastMsg(): String? {
        return lastMsg
    }

    fun setLastMsg(lastMsg: String?) {
        this.lastMsg = lastMsg
    }

    fun getLastDatetime(): String? {
        return lastDatetime
    }

    fun setLastDatetime(lastDatetime: String?) {
        this.lastDatetime = lastDatetime
    }

    fun getUserCount(): Int? {
        return userCount
    }

    fun setUserCount(userCount: Int?) {
        this.userCount = userCount
    }

    fun getUnreadCount(): Int? {
        return unreadCount
    }

    fun setUnreadCount(unreadCount: Int?) {
        this.unreadCount = unreadCount
    }
}
