package com.japnoor.anticorruption

data class Notification(
    var notificationId: String = "",
    var complaintAgainst: String = "",
    var notificationTime : String = "",
    var userId: String = "",
    var complaintId: String = "",
    var userName: String = "",
    var complaintStatus: String = "",
    var complaintNumber: String = "",
    var notificationType : String=""
) {
}