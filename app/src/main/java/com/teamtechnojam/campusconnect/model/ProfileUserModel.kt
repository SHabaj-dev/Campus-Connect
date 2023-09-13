package com.teamtechnojam.campusconnect.model

data class ProfileUserModel(
    val userName: String?,
    val about: String?,
    val courseName: String?,
    val phoneNumber: String?,
    val profileImage: String?,
    val skills: String?,
    val university: String?,
) {
    constructor() : this(null, null, null, null, null, null, null)
}
