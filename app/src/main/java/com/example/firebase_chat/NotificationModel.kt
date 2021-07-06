package com.example.firebase_chat

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-07-06
 * @desc
 */
class NotificationModel {
    var to: String? = null
    var notification = Notification()
    var data = Data()

    class Notification {
        var title: String? = null
        var body: String? = null
    }

    class Data {
        var title: String? = null
        var body: String? = null
    }
}
