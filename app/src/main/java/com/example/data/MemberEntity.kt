package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val contactNumber: String,
    val profilePhotoUri: String, // Can be "avatar_1", "avatar_2", etc. or a custom URL, or empty
    val subCommunity: String,    // "navi_mumbai", "mumbai" or "jalor"
    val position: String = "Member"
)
