package com.example.data.repository

import com.example.data.local.BorrowRequestEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.GamiturnDao
import com.example.data.local.ItemReviewEntity
import com.example.data.local.StudentProfileEntity
import com.example.data.local.SupplyItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GamiturnRepository(private val dao: GamiturnDao) {
    private val seedMutex = Mutex()

    val profileFlow: Flow<StudentProfileEntity?> = dao.observeProfile()
    val itemsFlow: Flow<List<SupplyItemEntity>> = dao.observeAllItems()
    val requestsFlow: Flow<List<BorrowRequestEntity>> = dao.observeAllRequests()
    val messagesFlow: Flow<List<ChatMessageEntity>> = dao.observeAllMessages()

    fun observeItem(itemId: Int): Flow<SupplyItemEntity?> = dao.observeItemById(itemId)
    fun observeReviews(itemId: Int): Flow<List<ItemReviewEntity>> = dao.observeReviewsForItem(itemId)

    suspend fun ensureSeeded() {
        seedMutex.withLock {
            if (dao.countItems() > 0) return

            val now = System.currentTimeMillis()
            val sampleItems = listOf(
                SupplyItemEntity(
                    id = 1,
                    title = "Scientific Calculator",
                    modelOrSpec = "FX-991EX",
                    category = "School supplies",
                    description = "Exam-approved model, slide cover included. Fresh battery, screen unmarked.",
                    pickupLocation = "Pickup at the main library",
                    ownerName = "Ana Cruz",
                    ownerRating = 4.9f,
                    ownerReviewCount = 14,
                    isAvailable = true,
                    distanceKm = 0.2f,
                    distanceLabel = "Main Library · 200m",
                    iconType = "calculator",
                    isOwnedByCurrentUser = false,
                    createdAt = now - 10_000L
                ),
                SupplyItemEntity(
                    id = 2,
                    title = "Bluetooth Speaker",
                    modelOrSpec = "JBL Go 3",
                    category = "Gadgets",
                    description = "Compact wireless speaker great for group presentations, PE dance practice, and study reviews.",
                    pickupLocation = "Student Hub Table 4",
                    ownerName = "Leo Santos",
                    ownerRating = 4.7f,
                    ownerReviewCount = 9,
                    isAvailable = true,
                    distanceKm = 0.3f,
                    distanceLabel = "Student Hub · 300m",
                    iconType = "speaker",
                    isOwnedByCurrentUser = false,
                    createdAt = now - 20_000L
                ),
                SupplyItemEntity(
                    id = 3,
                    title = "Projector — 3000 lm",
                    modelOrSpec = "Epson EB-X06",
                    category = "Gadgets",
                    description = "Bright HDMI classroom projector with carrying case and extension cord.",
                    pickupLocation = "AV Room Desk",
                    ownerName = "Jies Abergas",
                    ownerRating = 4.8f,
                    ownerReviewCount = 11,
                    isAvailable = false,
                    distanceKm = 0.4f,
                    distanceLabel = "Science Bldg · 400m",
                    iconType = "projector",
                    isOwnedByCurrentUser = false,
                    createdAt = now - 30_000L
                ),
                SupplyItemEntity(
                    id = 4,
                    title = "Graphing Calculator TI-84",
                    modelOrSpec = "TI-84 Plus CE",
                    category = "School supplies",
                    description = "Color screen graphing calculator with USB charging cable for calculus and physics.",
                    pickupLocation = "Math Dept Hallway",
                    ownerName = "Ana Cruz",
                    ownerRating = 4.9f,
                    ownerReviewCount = 14,
                    isAvailable = true,
                    distanceKm = 0.2f,
                    distanceLabel = "Math Wing · 250m",
                    iconType = "calculator",
                    isOwnedByCurrentUser = false,
                    createdAt = now - 40_000L
                ),
                SupplyItemEntity(
                    id = 5,
                    title = "Engineering Calculator Set",
                    modelOrSpec = "Casio + Precision Compass",
                    category = "School supplies",
                    description = "Complete drafting & engineering kit with metal compass, triangles, and scientific calc.",
                    pickupLocation = "TLE Workshop",
                    ownerName = "Leo Santos",
                    ownerRating = 4.7f,
                    ownerReviewCount = 9,
                    isAvailable = false,
                    distanceKm = 0.5f,
                    distanceLabel = "TLE Lab · 500m",
                    iconType = "compass",
                    isOwnedByCurrentUser = false,
                    createdAt = now - 50_000L
                ),
                SupplyItemEntity(
                    id = 6,
                    title = "LED Ring Light 10\"",
                    modelOrSpec = "Tripod + Phone Mount",
                    category = "Gadgets",
                    description = "Adjustable warm/cool studio light for video reports and group vlogs.",
                    pickupLocation = "Campus Gazebo",
                    ownerName = "You (Student Lender)",
                    ownerRating = 4.8f,
                    ownerReviewCount = 8,
                    isAvailable = true,
                    distanceKm = 0.1f,
                    distanceLabel = "Near you",
                    iconType = "ring_light",
                    isOwnedByCurrentUser = true,
                    createdAt = now - 60_000L
                ),
                SupplyItemEntity(
                    id = 7,
                    title = "USB-C Multiport Hub",
                    modelOrSpec = "HDMI 4K + 3x USB 3.0",
                    category = "Gadgets",
                    description = "Plug-and-play adapter for laptops to connect classroom HDMI projectors.",
                    pickupLocation = "Computer Lab 2",
                    ownerName = "You (Student Lender)",
                    ownerRating = 4.8f,
                    ownerReviewCount = 8,
                    isAvailable = true,
                    distanceKm = 0.1f,
                    distanceLabel = "Near you",
                    iconType = "hub",
                    isOwnedByCurrentUser = true,
                    createdAt = now - 70_000L
                ),
                SupplyItemEntity(
                    id = 8,
                    title = "Power Bank 20,000mAh",
                    modelOrSpec = "Anker 22.5W Fast Charge",
                    category = "Gadgets",
                    description = "Fully charged dual-output power bank with built-in USB-C cable.",
                    pickupLocation = "Covered Court Bench",
                    ownerName = "Erl Montemayor",
                    ownerRating = 4.9f,
                    ownerReviewCount = 12,
                    isAvailable = true,
                    distanceKm = 0.3f,
                    distanceLabel = "Covered Court · 300m",
                    iconType = "power_bank",
                    isOwnedByCurrentUser = false,
                    createdAt = now - 80_000L
                ),
                SupplyItemEntity(
                    id = 9,
                    title = "Digital Drawing Tablet",
                    modelOrSpec = "Wacom Intuos Small",
                    category = "Gadgets",
                    description = "Pressure-sensitive pen tablet for digital arts, diagrams, and math notes.",
                    pickupLocation = "ICT Room 1",
                    ownerName = "Elaiza Adelante",
                    ownerRating = 5.0f,
                    ownerReviewCount = 7,
                    isAvailable = true,
                    distanceKm = 0.35f,
                    distanceLabel = "ICT Building · 350m",
                    iconType = "tablet",
                    isOwnedByCurrentUser = false,
                    createdAt = now - 90_000L
                ),
                SupplyItemEntity(
                    id = 10,
                    title = "Science Lab Coat & Goggles",
                    modelOrSpec = "Size M · Sanitized",
                    category = "School supplies",
                    description = "Freshly laundered cotton laboratory coat with anti-fog safety goggles.",
                    pickupLocation = "Chemistry Lab Locker",
                    ownerName = "Ivan Tampus",
                    ownerRating = 4.8f,
                    ownerReviewCount = 6,
                    isAvailable = true,
                    distanceKm = 0.45f,
                    distanceLabel = "Chem Lab · 450m",
                    iconType = "lab_coat",
                    isOwnedByCurrentUser = false,
                    createdAt = now - 100_000L
                ),
                SupplyItemEntity(
                    id = 11,
                    title = "Technical Drafting Set",
                    modelOrSpec = "T-Square + Scale Ruler",
                    category = "School supplies",
                    description = "24-inch T-square, 30/60 & 45 triangles, and triangular architectural scale.",
                    pickupLocation = "Main Canteen",
                    ownerName = "Sanchez Fernando",
                    ownerRating = 4.7f,
                    ownerReviewCount = 5,
                    isAvailable = true,
                    distanceKm = 0.25f,
                    distanceLabel = "Canteen · 250m",
                    iconType = "compass",
                    isOwnedByCurrentUser = false,
                    createdAt = now - 110_000L
                ),
                SupplyItemEntity(
                    id = 12,
                    title = "Study Focus Headphones",
                    modelOrSpec = "Sony WH-CH520",
                    category = "Gadgets",
                    description = "Lightweight wireless headphones with 40-hour battery for library review sessions.",
                    pickupLocation = "2nd Floor Reading Nook",
                    ownerName = "Rjay Cuizon",
                    ownerRating = 4.9f,
                    ownerReviewCount = 10,
                    isAvailable = true,
                    distanceKm = 0.2f,
                    distanceLabel = "Main Library · 200m",
                    iconType = "headphones",
                    isOwnedByCurrentUser = false,
                    createdAt = now - 120_000L
                )
            )
            dao.insertItems(sampleItems)

            dao.insertReviews(
                listOf(
                    ItemReviewEntity(
                        itemId = 1,
                        reviewerName = "Ana C.",
                        comment = "Saved my exam week. Returned next day.",
                        timeAgo = "2 weeks ago",
                        rating = 5
                    ),
                    ItemReviewEntity(
                        itemId = 1,
                        reviewerName = "Josh L.",
                        comment = "Super clean and battery works great!",
                        timeAgo = "1 month ago",
                        rating = 5
                    ),
                    ItemReviewEntity(
                        itemId = 2,
                        reviewerName = "Mia T.",
                        comment = "Loud and clear audio for our classroom presentation.",
                        timeAgo = "5 days ago",
                        rating = 5
                    ),
                    ItemReviewEntity(
                        itemId = 8,
                        reviewerName = "Ivan T.",
                        comment = "Charged my phone twice during field trip.",
                        timeAgo = "1 week ago",
                        rating = 5
                    )
                )
            )

            dao.insertRequests(
                listOf(
                    // Incoming requests (from other students to you)
                    BorrowRequestEntity(
                        id = 1,
                        itemId = 6,
                        itemTitle = "LED Ring Light 10\"",
                        itemIconType = "ring_light",
                        ownerName = "You",
                        borrowerName = "Josh Lim",
                        borrowerAddress = "Blk 2 Lot 8, Poblacion",
                        borrowerMobile = "+63 918 554 1920",
                        borrowerVerified = true,
                        durationLabel = "3 days",
                        expectedReturnLabel = "26 Nov 2026 · 5:00 PM",
                        timeAgoLabel = "Requested 3h ago",
                        note = "Need it for our Science video presentation shoot.",
                        status = "Pending",
                        isIncoming = true,
                        createdAt = now - 10_800_000L
                    ),
                    BorrowRequestEntity(
                        id = 2,
                        itemId = 7,
                        itemTitle = "USB-C Multiport Hub",
                        itemIconType = "hub",
                        ownerName = "You",
                        borrowerName = "Ana Cruz",
                        borrowerAddress = "Purok 3, San Roque",
                        borrowerMobile = "+63 917 882 4103",
                        borrowerVerified = true,
                        durationLabel = "1 day",
                        expectedReturnLabel = "24 Nov 2026 · 4:00 PM",
                        timeAgoLabel = "Requested 1d ago",
                        note = "Our classroom projector only takes HDMI.",
                        status = "Pending",
                        isIncoming = true,
                        createdAt = now - 86_400_000L
                    ),
                    BorrowRequestEntity(
                        id = 3,
                        itemId = 6,
                        itemTitle = "LED Ring Light 10\"",
                        itemIconType = "ring_light",
                        ownerName = "You",
                        borrowerName = "Mia Tan",
                        borrowerAddress = "Gabi, Cordova",
                        borrowerMobile = "+63 926 701 3349",
                        borrowerVerified = true,
                        durationLabel = "6 hours",
                        expectedReturnLabel = "23 Nov 2026 · 6:00 PM",
                        timeAgoLabel = "Requested 2d ago",
                        note = "For English speech recording after class.",
                        status = "Pending",
                        isIncoming = true,
                        createdAt = now - 172_800_000L
                    ),
                    // My outgoing requests
                    BorrowRequestEntity(
                        id = 4,
                        itemId = 4,
                        itemTitle = "Graphing Calculator TI-84",
                        itemIconType = "calculator",
                        ownerName = "Ana Cruz",
                        borrowerName = "Maya Reyes",
                        borrowerAddress = "Blk 4 Lot 12, San Roque",
                        borrowerMobile = "+63 917 440 1222",
                        borrowerVerified = true,
                        durationLabel = "3 days",
                        expectedReturnLabel = "1d left",
                        timeAgoLabel = "2 days ago",
                        note = "Pre-calculus quiz prep.",
                        status = "Borrowed",
                        isIncoming = false,
                        createdAt = now - 150_000_000L
                    ),
                    BorrowRequestEntity(
                        id = 5,
                        itemId = 2,
                        itemTitle = "Bluetooth Speaker",
                        itemIconType = "speaker",
                        ownerName = "Leo Santos",
                        borrowerName = "Maya Reyes",
                        borrowerAddress = "Blk 4 Lot 12, San Roque",
                        borrowerMobile = "+63 917 440 1222",
                        borrowerVerified = true,
                        durationLabel = "1 day",
                        expectedReturnLabel = "starts Fri",
                        timeAgoLabel = "3 days ago",
                        note = "PE group routine rehearsal.",
                        status = "Approved",
                        isIncoming = false,
                        createdAt = now - 220_000_000L
                    ),
                    BorrowRequestEntity(
                        id = 6,
                        itemId = 3,
                        itemTitle = "Projector — 3000 lm",
                        itemIconType = "projector",
                        ownerName = "Jies Abergas",
                        borrowerName = "Maya Reyes",
                        borrowerAddress = "Blk 4 Lot 12, San Roque",
                        borrowerMobile = "+63 917 440 1222",
                        borrowerVerified = true,
                        durationLabel = "6 hours",
                        expectedReturnLabel = "7 Nov",
                        timeAgoLabel = "2 weeks ago",
                        note = "Research defense practice.",
                        status = "Returned",
                        isIncoming = false,
                        createdAt = now - 1_200_000_000L
                    ),
                    BorrowRequestEntity(
                        id = 7,
                        itemId = 11,
                        itemTitle = "Technical Drafting Set",
                        itemIconType = "compass",
                        ownerName = "Sanchez Fernando",
                        borrowerName = "Maya Reyes",
                        borrowerAddress = "Blk 4 Lot 12, San Roque",
                        borrowerMobile = "+63 917 440 1222",
                        borrowerVerified = true,
                        durationLabel = "1 day",
                        expectedReturnLabel = "6 Nov",
                        timeAgoLabel = "2 weeks ago",
                        note = "Plate #4 submission.",
                        status = "Rejected",
                        isIncoming = false,
                        createdAt = now - 1_300_000_000L
                    )
                )
            )

            dao.insertMessages(
                listOf(
                    ChatMessageEntity(
                        channelId = "org_group",
                        channelTitle = "Org Group Chat",
                        senderName = "Ana Cruz",
                        message = "does anyone have a calculator i can borrow for my exam tomorrow?",
                        isFromMe = false,
                        timestampLabel = "9:15 AM",
                        createdAt = now - 3_600_000L
                    ),
                    ChatMessageEntity(
                        channelId = "org_group",
                        channelTitle = "Org Group Chat",
                        senderName = "Leo Santos",
                        message = "josh has one i think",
                        isFromMe = false,
                        timestampLabel = "9:17 AM",
                        createdAt = now - 3_400_000L
                    ),
                    ChatMessageEntity(
                        channelId = "org_group",
                        channelTitle = "Org Group Chat",
                        senderName = "Ana Cruz",
                        message = "which josh?",
                        isFromMe = false,
                        timestampLabel = "9:18 AM",
                        createdAt = now - 3_300_000L
                    ),
                    ChatMessageEntity(
                        channelId = "org_group",
                        channelTitle = "Org Group Chat",
                        senderName = "Mia Tan",
                        message = "check the Browse tab on Gamiturn — saw an FX-991EX listed near Main Library!",
                        isFromMe = false,
                        timestampLabel = "9:21 AM",
                        createdAt = now - 3_100_000L
                    )
                )
            )
        }
    }

    suspend fun saveProfile(
        fullName: String,
        age: Int,
        mobileNumber: String,
        address: String,
        schoolName: String,
        idPhotoUri: String?,
        isIdVerified: Boolean
    ) {
        val existing = dao.getProfileOnce()
        dao.upsertProfile(
            StudentProfileEntity(
                id = 1,
                fullName = fullName.trim(),
                age = age,
                mobileNumber = mobileNumber.trim(),
                address = address.trim(),
                schoolName = schoolName.trim().ifBlank { "Cordova National High School" },
                idPhotoUri = idPhotoUri,
                isIdVerified = isIdVerified,
                rating = existing?.rating ?: 4.8f,
                completedBorrowings = existing?.completedBorrowings ?: 8,
                isLoggedIn = true
            )
        )
    }

    suspend fun loginDemoStudent() {
        val existing = dao.getProfileOnce()
        if (existing != null) {
            dao.upsertProfile(existing.copy(isLoggedIn = true))
        } else {
            dao.upsertProfile(
                StudentProfileEntity(
                    id = 1,
                    fullName = "Maya Reyes",
                    age = 17,
                    mobileNumber = "+63 917 440 1222",
                    address = "Blk 4 Lot 12, San Roque",
                    schoolName = "Cordova National High School",
                    idPhotoUri = null,
                    isIdVerified = true,
                    rating = 4.8f,
                    completedBorrowings = 8,
                    isLoggedIn = true
                )
            )
        }
    }

    suspend fun signOut() {
        val existing = dao.getProfileOnce() ?: return
        dao.upsertProfile(existing.copy(isLoggedIn = false))
    }

    suspend fun addSupplyItem(
        title: String,
        modelOrSpec: String,
        category: String,
        description: String,
        pickupLocation: String,
        photoUri: String?,
        iconType: String
    ) {
        val profile = dao.getProfileOnce()
        val ownerName = profile?.fullName ?: "Student Lender"
        dao.insertItem(
            SupplyItemEntity(
                title = title.trim(),
                modelOrSpec = modelOrSpec.trim().ifBlank { "Student Shared" },
                category = category,
                description = description.trim(),
                pickupLocation = pickupLocation.trim().ifBlank { "Main Campus Gate" },
                ownerName = ownerName,
                ownerRating = profile?.rating ?: 4.8f,
                ownerReviewCount = profile?.completedBorrowings ?: 1,
                isAvailable = true,
                distanceKm = 0.1f,
                distanceLabel = "Near you",
                photoUri = photoUri,
                iconType = iconType,
                isOwnedByCurrentUser = true,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun submitBorrowRequest(
        item: SupplyItemEntity,
        durationLabel: String,
        expectedReturnLabel: String,
        note: String
    ) {
        val profile = dao.getProfileOnce()
        dao.insertRequest(
            BorrowRequestEntity(
                itemId = item.id,
                itemTitle = item.title,
                itemIconType = item.iconType,
                ownerName = item.ownerName,
                borrowerName = profile?.fullName ?: "Maya Reyes",
                borrowerAddress = profile?.address ?: "San Roque, Cordova",
                borrowerMobile = profile?.mobileNumber ?: "+63 917 000 0000",
                borrowerVerified = profile?.isIdVerified ?: true,
                durationLabel = durationLabel,
                expectedReturnLabel = expectedReturnLabel,
                timeAgoLabel = "Just now",
                note = note.trim(),
                status = "Pending",
                isIncoming = false,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun approveIncomingRequest(requestId: Int) {
        val req = dao.getRequestByIdOnce(requestId) ?: return
        dao.updateRequestStatus(requestId, "Approved")
        dao.updateItemAvailability(req.itemId, false)
    }

    suspend fun declineIncomingRequest(requestId: Int) {
        dao.updateRequestStatus(requestId, "Rejected")
    }

    suspend fun markRequestReturned(requestId: Int, rating: Int, reviewComment: String) {
        val req = dao.getRequestByIdOnce(requestId) ?: return
        dao.updateRequestStatus(requestId, "Returned")
        dao.updateItemAvailability(req.itemId, true)

        val profile = dao.getProfileOnce()
        if (profile != null) {
            dao.upsertProfile(profile.copy(completedBorrowings = profile.completedBorrowings + 1))
        }

        if (reviewComment.isNotBlank()) {
            dao.insertReview(
                ItemReviewEntity(
                    itemId = req.itemId,
                    reviewerName = profile?.fullName ?: "Student Borrower",
                    comment = reviewComment.trim(),
                    timeAgo = "Just now",
                    rating = rating.coerceIn(1, 5)
                )
            )
        }
    }

    suspend fun sendChatMessage(channelId: String, channelTitle: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val profile = dao.getProfileOnce()
        val sender = profile?.fullName ?: "Maya Reyes"
        val timeFmt = SimpleDateFormat("h:mm a", Locale.getDefault())
        dao.insertMessage(
            ChatMessageEntity(
                channelId = channelId,
                channelTitle = channelTitle,
                senderName = sender,
                message = trimmed,
                isFromMe = true,
                timestampLabel = timeFmt.format(Date()),
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun ensureDirectThread(ownerName: String): String {
        val channelId = "dm_${ownerName.lowercase(Locale.ROOT).replace(" ", "_")}"
        if (dao.countMessagesInChannel(channelId) == 0) {
            val timeFmt = SimpleDateFormat("h:mm a", Locale.getDefault())
            dao.insertMessage(
                ChatMessageEntity(
                    channelId = channelId,
                    channelTitle = ownerName,
                    senderName = ownerName,
                    message = "Hi! Feel free to ask about pickup timing or item condition.",
                    isFromMe = false,
                    timestampLabel = timeFmt.format(Date()),
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        return channelId
    }
}
