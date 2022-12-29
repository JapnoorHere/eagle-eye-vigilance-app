package com.japnoor.anticorruption

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Security Question", foreignKeys = [
    ForeignKey(entity = SignUpEntity::class, parentColumns = ["id"], childColumns = ["uId"], onDelete = ForeignKey.CASCADE )
])
class SecurityQuestionEntity: Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")var id=0
    @ColumnInfo(name="uId") var uId:Int = 0
    @ColumnInfo(name = "Question")var question :String?=null
    @ColumnInfo(name = "Answer")var answer :String?=null
}