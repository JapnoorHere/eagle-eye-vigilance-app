package com.japnoor.anticorruption

import androidx.room.Embedded
import androidx.room.Relation

class DisplayDemandEntity {
    @Embedded
    var demandEntity: DemandEntity?= null

    @Relation(parentColumn = "uId", entityColumn = "id")
    var signUpEntity: SignUpEntity?= null
}