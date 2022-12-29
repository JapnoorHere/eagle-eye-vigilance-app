package com.japnoor.anticorruption

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "Demand Letter", foreignKeys = [
    ForeignKey(entity = SignUpEntity::class, parentColumns = ["id"], childColumns = ["uId"], onDelete = ForeignKey.CASCADE )
])
class DemandEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id") var id:Int = 0
    @ColumnInfo(name="uId") var uId:Int = 0
    @ColumnInfo(name="Demand Summary") var demandSummary : String?=null
    @ColumnInfo(name="Demand Details") var demandDetails : String?=null
    @ColumnInfo(name="District") var districtDemand : String?=null
    @ColumnInfo(name="Date") var dateDemand : String?=null
    @ColumnInfo(name="Status") var status : String?=null
    @ColumnInfo(name="Image") var image : String?=null

}