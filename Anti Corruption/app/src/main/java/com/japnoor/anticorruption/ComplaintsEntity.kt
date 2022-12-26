package com.japnoor.anticorruption

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "Complaints", foreignKeys = [
    ForeignKey(entity = SignUpEntity::class, parentColumns = ["id"], childColumns = ["uId"], onDelete = ForeignKey.CASCADE )
])
class ComplaintsEntity {
@PrimaryKey(autoGenerate = true)
@ColumnInfo(name="id") var id:Int = 0
@ColumnInfo(name="uId") var uId:Int = 0
@ColumnInfo(name="ComplaintSummary") var complaintSummary : String?=null
@ColumnInfo(name="Complaintagainst") var complaintAgainst : String?=null
@ColumnInfo(name="Complaintdetails") var complaintDetails : String?=null
@ColumnInfo(name="SelectDistrict") var selectDistrict : String?=null
@ColumnInfo(name="Date") var date : String ? =null
@ColumnInfo(name="status") var status : String ?= null
@ColumnInfo(name="audio") var audio : String ?= null
@ColumnInfo(name="video") var video : String ?= null




}