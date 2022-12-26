package com.japnoor.anticorruption

import androidx.room.*

@Dao
interface Daao {

    @Insert
    fun addComplaint(vararg complaintsEntity: ComplaintsEntity)

    @Insert
    fun addAccount(signUpEntity: SignUpEntity) : Long


    @Query("select * from SignUp where Email=:email AND Password=:password ")
    fun getUser(email: String, password: String): SignUpEntity

    @Query("select * from `Security Question` where uId=:uId")
    fun getQue(uId: Int?): SecurityQuestionEntity


    @Query(" select * from Complaints where uId = :uid")
    fun getUserComplaints(uid: Int): List<ComplaintsEntity>

    @Query(" select * from Complaints where uId = :uid and status='res'")
    fun getUserResComplaints(uid: Int): List<ComplaintsEntity>

    @Query(" select * from Complaints where uId = :uid and status='rej'")
    fun getUserRejComplaints(uid: Int): List<ComplaintsEntity>

    @Query(" select * from Complaints where uId = :uid and status='acc'")
    fun getUserAccComplaints(uid: Int): List<ComplaintsEntity>

    @Query("select * from SignUp where Category='a'")
    fun checkAdmin(): SignUpEntity

    @Update
    fun changeStatus(vararg complaintsEntity:ComplaintsEntity)


    @Query("Select * from Complaints where status='res'")
    fun getResDisplayComplaints() : List<DisplayComplaintsEntity>


    @Query("Select * from Complaints where status='acc'")
    fun getAccDisplayComplaints() : List<DisplayComplaintsEntity>


    @Query("Select * from Complaints where status='rej'")
    fun getRejDisplayComplaints() : List<DisplayComplaintsEntity>


    @Query("Select * from Complaints")
    fun getAllDisplayComplaints() : List<DisplayComplaintsEntity>

    @Update
    fun updateComplaint(vararg complaintsEntity: ComplaintsEntity)

    @Query("Select COUNT(*) from Complaints where uId = :uid and status='res'")
    fun getResolvedComplaintsCount(uid: Int?): Int

    @Query("Select COUNT(*) from Complaints where uId = :uid and status='rej'")
    fun getRejectedComplaintsCount(uid: Int?): Int

    @Query("Select COUNT(*) from Complaints where uId = :uid and status='acc'")
    fun getAcceptedComplaintsCount(uid: Int?): Int

    @Query("Select COUNT(*) from Complaints where uId = :uid")
    fun getComplaintsCount(uid: Int?): Int

    @Query("Select COUNT(*) from Complaints")
    fun adminComplaintsCount(): Int

    @Query("Select COUNT(*) from Complaints where status='res'")
    fun adminResComplaintsCount(): Int

    @Query("Select COUNT(*) from Complaints where status='rej'")
    fun adminRejComplaintsCount(): Int

    @Query("Select COUNT(*) from Complaints where status='acc'")
    fun adminAccComplaintsCount(): Int

    @Insert
    fun addQuestion(vararg securityQuestionEntity: SecurityQuestionEntity)

    @Query("select * from SignUp where Category='u'")
    fun getUserList() : List<SignUpEntity>

    @Delete
    fun deleteUser(signUpEntity: SignUpEntity)

    @Delete
    fun deleteComplaint(complaintsEntity: ComplaintsEntity)


    @Insert
    fun addDemand(vararg demandEntity: DemandEntity)

    @Query("Select * from `demand letter` where status='res'")
    fun getResDisplayDemand() : List<DisplayDemandEntity>



    @Query("Select * from `demand letter` where status='acc'")
    fun getAccDisplayDemand() : List<DisplayDemandEntity>



    @Query("Select * from `Demand Letter` where status='rej'")
    fun getRejDisplayDemand() : List<DisplayDemandEntity>


    @Query("Select * from `Demand Letter`")
    fun getAllDisplayDemand() : List<DisplayDemandEntity>


    @Update
    fun changeStatusDemand(vararg demandEntity: DemandEntity)

    @Query("Select COUNT(*) from `demand letter`")
    fun adminDemandCount(): Int

    @Query("Select COUNT(*) from `demand letter` where status='res'")
    fun adminResDemandCount(): Int

    @Query("Select COUNT(*) from `demand letter` where status='rej'")
    fun adminRejDemandCount(): Int

    @Query("Select COUNT(*) from `demand letter` where status='acc'")
    fun adminAccDemandCount(): Int

    @Query(" select * from `demand letter` where uId = :uid")
    fun getUserDemands(uid: Int): List<DemandEntity>

    @Query(" select * from `demand letter` where uId = :uid and status='res'")
    fun getUserResDemand(uid: Int): List<DemandEntity>

    @Query(" select * from `demand letter` where uId = :uid and status='rej'")
    fun getUserRejDemand(uid: Int): List<DemandEntity>

    @Query(" select * from `demand letter` where uId = :uid and status='acc'")
    fun getUserAccDemand(uid: Int): List<DemandEntity>

    @Delete
    fun deleteDemand(vararg demandEntity: DemandEntity)

    @Update
    fun updateDemand(vararg demandEntity: DemandEntity)

    @Update
    fun updateProfile(vararg signUpEntity: SignUpEntity)

    @Update()
    fun updateQuestion(vararg securityQuestionEntity : SecurityQuestionEntity)

    @Query("Select COUNT(*) from `demand letter` where uId = :uid and status='res'")
    fun getResolvedDemandCount(uid: Int?): Int

    @Query("Select COUNT(*) from `demand letter` where uId = :uid and status='rej'")
    fun getRejectedDemandCount(uid: Int?): Int

    @Query("Select COUNT(*) from `demand letter` where uId = :uid and status='acc'")
    fun getAcceptedDemandCount(uid: Int?): Int

    @Query("Select COUNT(*) from `demand letter` where uId = :uid")
    fun getDemandCount(uid: Int?): Int

    @Query("Select * from SignUp where Email = :email ")
    fun getEmail(email : String) : SignUpEntity

    @Query("Select Answer from `Security Question` where uId = :uid ")
    fun getAnswer(uid : Int?) : String

    @Query("Select Question from `Security Question` where uId = :uid ")
    fun getQuestion(uid : Int?) : String

    @Query("UPDATE SignUp SET Password = :password WHERE id = :uid")
    fun updatePassword(password: String, uid: Int?)


    @Query("UPDATE `Security Question` SET Question = :que  WHERE id = :uid")
    fun updateQuestion(que: String, uid: Int?)

    @Query("UPDATE `Security Question` SET Answer = :ans  WHERE id = :uid")
    fun updateAnswer(ans: String, uid: Int?)

    @Query("UPDATE SignUp SET Status = :status  WHERE id = :uid")
    fun addStatus(status: String, uid: Int?)

    @Query("Select * from SignUp where Status=1 ")
    fun getStatus() : SignUpEntity

    @Query("Select Email from SignUp where Email=:email")
    fun checkEmail(email : String) : String

    @Query("Select `Phone Number` from SignUp where `Phone Number`=:phn")
    fun checkPhonen(phn : String) : String

    @Query("Select * from SignUp where Email=:email")
    fun checkEmailprofile(email : String) : SignUpEntity

    @Query("Select * from SignUp where `Phone Number`=:phn")
    fun checkPhonenprofile(phn : String) : SignUpEntity


}