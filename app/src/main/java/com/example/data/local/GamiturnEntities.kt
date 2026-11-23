package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String,
    val age: Int,
    val mobileNumber: String,
    val address: String,
    val schoolName: String,
    val idPhotoUri: String?,
    val isIdVerified: Boolean,
    val rating: Float,
    val completedBorrowings: Int,
    val isLoggedIn: Boolean
)

@Entity(tableName = "supply_items")
data class SupplyItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val modelOrSpec: String,
    val category: String, // "School supplies" or "Gadgets"
    val description: String,
    val pickupLocation: String,
    val ownerName: String,
    val ownerRating: Float,
    val ownerReviewCount: Int,
    val isAvailable: Boolean,
    val distanceKm: Float,
    val distanceLabel: String,
    val photoUri: String? = null,
    val iconType: String = "calculator",
    val isOwnedByCurrentUser: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "item_reviews")
data class ItemReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemId: Int,
    val reviewerName: String,
    val comment: String,
    val timeAgo: String,
    val rating: Int
)

@Entity(tableName = "borrow_requests")
data class BorrowRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemId: Int,
    val itemTitle: String,
    val itemIconType: String,
    val ownerName: String,
    val borrowerName: String,
    val borrowerAddress: String,
    val borrowerMobile: String,
    val borrowerVerified: Boolean,
    val durationLabel: String,
    val expectedReturnLabel: String,
    val timeAgoLabel: String,
    val note: String,
    val status: String, // "Pending", "Approved", "Borrowed", "Returned", "Rejected"
    val isIncoming: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val channelId: String, // "org_group" or "dm_<ownerName>"
    val channelTitle: String,
    val senderName: String,
    val message: String,
    val isFromMe: Boolean,
    val timestampLabel: String,
    val createdAt: Long = System.currentTimeMillis()
)
