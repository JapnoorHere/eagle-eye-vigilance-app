package com.japnoor.anticorruption

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "SignUp")
class SignUpEntity: Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")var id=0
    @ColumnInfo(name = "First name")var firstName :String?=null
    @ColumnInfo(name = "Last name")var lastName :String?=null
    @ColumnInfo(name = "Email")var email :String?=null
    @ColumnInfo(name = "Phone Number")var phoneN :String?=null
    @ColumnInfo(name = "Password")var pass:String?=null
    @ColumnInfo(name = "Category") var category : String? =null
    @ColumnInfo(name = "Status") var status : String? =null

 }