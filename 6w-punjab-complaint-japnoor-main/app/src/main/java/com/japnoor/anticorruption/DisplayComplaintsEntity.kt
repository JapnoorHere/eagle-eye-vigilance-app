package com.japnoor.anticorruption

import androidx.room.Embedded
import androidx.room.Relation

class DisplayComplaintsEntity {
    @Embedded
    var complaintsEntity: ComplaintsEntity?= null

    @Relation(parentColumn = "uId", entityColumn = "id")
    var userEntity: SignUpEntity?= null
}