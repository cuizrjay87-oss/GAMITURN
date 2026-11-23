package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GamiturnDao {
    // Profile
    @Query("SELECT * FROM student_profile WHERE id = 1")
    fun observeProfile(): Flow<StudentProfileEntity?>

    @Query("SELECT * FROM student_profile WHERE id = 1")
    suspend fun getProfileOnce(): StudentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: StudentProfileEntity)

    // Supply Items
    @Query("SELECT * FROM supply_items ORDER BY createdAt DESC")
    fun observeAllItems(): Flow<List<SupplyItemEntity>>

    @Query("SELECT * FROM supply_items WHERE id = :itemId")
    fun observeItemById(itemId: Int): Flow<SupplyItemEntity?>

    @Query("SELECT * FROM supply_items WHERE id = :itemId")
    suspend fun getItemByIdOnce(itemId: Int): SupplyItemEntity?

    @Query("SELECT COUNT(*) FROM supply_items")
    suspend fun countItems(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: SupplyItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<SupplyItemEntity>)

    @Query("UPDATE supply_items SET isAvailable = :isAvailable WHERE id = :itemId")
    suspend fun updateItemAvailability(itemId: Int, isAvailable: Boolean)

    // Reviews
    @Query("SELECT * FROM item_reviews WHERE itemId = :itemId ORDER BY id DESC")
    fun observeReviewsForItem(itemId: Int): Flow<List<ItemReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ItemReviewEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ItemReviewEntity)

    // Borrow Requests
    @Query("SELECT * FROM borrow_requests ORDER BY createdAt DESC")
    fun observeAllRequests(): Flow<List<BorrowRequestEntity>>

    @Query("SELECT * FROM borrow_requests WHERE id = :requestId")
    suspend fun getRequestByIdOnce(requestId: Int): BorrowRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<BorrowRequestEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: BorrowRequestEntity): Long

    @Query("UPDATE borrow_requests SET status = :status WHERE id = :requestId")
    suspend fun updateRequestStatus(requestId: Int, status: String)

    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY createdAt ASC")
    fun observeAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("SELECT COUNT(*) FROM chat_messages WHERE channelId = :channelId")
    suspend fun countMessagesInChannel(channelId: String): Int
}
