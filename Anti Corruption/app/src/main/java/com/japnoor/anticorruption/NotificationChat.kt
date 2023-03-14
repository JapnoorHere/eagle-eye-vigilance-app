package com.japnoor.anticorruption

data class NotificationChat(
    var notificationId: String = "",
    var complaintAgainst: String = "",
    var notificationTime : String = "",
    var userId: String = "",
    var complaintId: String = "",
    var userName: String = "",
    var complaintStatus: String = "",
    var complaintNumber: String = "",
    var notificationType : String="",
    var notificationMsg : String=""
) {
}