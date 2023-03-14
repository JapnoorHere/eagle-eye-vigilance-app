package com.japnoor.anticorruption

interface UserComplaintClick {
    fun onClick(complaints: Complaints)
}
interface UserDemandClick {
    fun onClick(demandLetter: DemandLetter)
}
interface NotificationClick {
    fun onClick(notifications: Notification, type : String,status : String = "")
}
