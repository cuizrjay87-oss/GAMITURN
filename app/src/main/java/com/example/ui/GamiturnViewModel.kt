package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BorrowRequestEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.GamiturnDatabase
import com.example.data.local.ItemReviewEntity
import com.example.data.local.StudentProfileEntity
import com.example.data.local.SupplyItemEntity
import com.example.data.repository.GamiturnRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GamiturnFilterState(
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val availableOnly: Boolean = false,
    val nearMeOnly: Boolean = false,
    val activeChatChannelId: String = "org_group",
    val snackbarMessage: String? = null,
    val isInitialized: Boolean = false
)

data class ChatChannelSummary(
    val channelId: String,
    val title: String,
    val memberLabel: String,
    val lastMessage: String
)

data class GamiturnUiState(
    val isInitialized: Boolean = false,
    val profile: StudentProfileEntity? = null,
    val items: List<SupplyItemEntity> = emptyList(),
    val filteredItems: List<SupplyItemEntity> = emptyList(),
    val incomingRequests: List<BorrowRequestEntity> = emptyList(),
    val myRequests: List<BorrowRequestEntity> = emptyList(),
    val messages: List<ChatMessageEntity> = emptyList(),
    val chatChannels: List<ChatChannelSummary> = emptyList(),
    val activeChatChannelId: String = "org_group",
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val availableOnly: Boolean = false,
    val nearMeOnly: Boolean = false,
    val snackbarMessage: String? = null
) {
    val schoolSuppliesCount: Int
        get() = items.count { it.category == "School supplies" }

    val gadgetsCount: Int
        get() = items.count { it.category == "Gadgets" }

    val pendingIncomingCount: Int
        get() = incomingRequests.count { it.status == "Pending" }

    val activeMyRequestsCount: Int
        get() = myRequests.count { it.status == "Pending" || it.status == "Approved" || it.status == "Borrowed" }

    val completedMyRequestsCount: Int
        get() = myRequests.count { it.status == "Returned" }
}

class GamiturnViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GamiturnRepository = GamiturnRepository(
        GamiturnDatabase.getInstance(application).gamiturnDao()
    )

    private val localState = MutableStateFlow(GamiturnFilterState())

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            localState.update { it.copy(isInitialized = true) }
        }
    }

    val uiState: StateFlow<GamiturnUiState> = combine(
        repository.profileFlow,
        repository.itemsFlow,
        repository.requestsFlow,
        repository.messagesFlow,
        localState
    ) { profile, items, requests, messages, filter ->
        val q = filter.searchQuery.trim().lowercase()
        val filtered = items.filter { item ->
            val matchesQuery = q.isEmpty() ||
                item.title.lowercase().contains(q) ||
                item.modelOrSpec.lowercase().contains(q) ||
                item.category.lowercase().contains(q) ||
                item.ownerName.lowercase().contains(q) ||
                item.description.lowercase().contains(q)
            val matchesCategory = filter.selectedCategory == null || item.category == filter.selectedCategory
            val matchesAvail = !filter.availableOnly || item.isAvailable
            val matchesNear = !filter.nearMeOnly || item.distanceKm <= 0.3f
            matchesQuery && matchesCategory && matchesAvail && matchesNear
        }

        val incoming = requests.filter { it.isIncoming }
        val mine = requests.filter { !it.isIncoming }

        val groupedChannels = messages.groupBy { it.channelId }
        val channels = buildList {
            val orgMsgs = groupedChannels["org_group"].orEmpty()
            add(
                ChatChannelSummary(
                    channelId = "org_group",
                    title = "Org Group Chat",
                    memberLabel = "42 members · Campus Hub",
                    lastMessage = orgMsgs.lastOrNull()?.message ?: "Ask classmates for school supplies"
                )
            )
            groupedChannels.keys
                .filter { it != "org_group" }
                .forEach { chId ->
                    val msgs = groupedChannels[chId].orEmpty()
                    val title = msgs.firstOrNull()?.channelTitle ?: chId.removePrefix("dm_")
                    add(
                        ChatChannelSummary(
                            channelId = chId,
                            title = title,
                            memberLabel = "Direct message · Verified student",
                            lastMessage = msgs.lastOrNull()?.message ?: ""
                        )
                    )
                }
        }

        GamiturnUiState(
            isInitialized = filter.isInitialized,
            profile = profile,
            items = items,
            filteredItems = filtered,
            incomingRequests = incoming,
            myRequests = mine,
            messages = messages.filter { it.channelId == filter.activeChatChannelId },
            chatChannels = channels,
            activeChatChannelId = filter.activeChatChannelId,
            searchQuery = filter.searchQuery,
            selectedCategory = filter.selectedCategory,
            availableOnly = filter.availableOnly,
            nearMeOnly = filter.nearMeOnly,
            snackbarMessage = filter.snackbarMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GamiturnUiState()
    )

    fun observeItem(itemId: Int): Flow<SupplyItemEntity?> = repository.observeItem(itemId)

    fun observeReviews(itemId: Int): Flow<List<ItemReviewEntity>> = repository.observeReviews(itemId)

    fun saveStudentProfile(
        fullName: String,
        ageText: String,
        mobileNumber: String,
        address: String,
        schoolName: String,
        idPhotoUri: String?,
        isIdVerified: Boolean,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val age = ageText.toIntOrNull()?.coerceIn(10, 99) ?: 18
            repository.saveProfile(
                fullName = fullName,
                age = age,
                mobileNumber = mobileNumber,
                address = address,
                schoolName = schoolName,
                idPhotoUri = idPhotoUri,
                isIdVerified = isIdVerified
            )
            localState.update { it.copy(snackbarMessage = "Welcome to Gamiturn, ${fullName.trim()}!") }
            onComplete()
        }
    }

    fun quickDemoLogin(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.loginDemoStudent()
            localState.update { it.copy(snackbarMessage = "Signed in as Maya Reyes (Verified Student)") }
            onComplete()
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
        }
    }

    fun updateSearchQuery(query: String) {
        localState.update { it.copy(searchQuery = query) }
    }

    fun setCategoryFilter(category: String?) {
        localState.update { state ->
            state.copy(selectedCategory = if (state.selectedCategory == category) null else category)
        }
    }

    fun applyCategoryOnly(category: String) {
        localState.update {
            it.copy(
                searchQuery = "",
                selectedCategory = category,
                availableOnly = false,
                nearMeOnly = false
            )
        }
    }

    fun toggleAvailableOnly() {
        localState.update { it.copy(availableOnly = !it.availableOnly) }
    }

    fun toggleNearMeOnly() {
        localState.update { it.copy(nearMeOnly = !it.nearMeOnly) }
    }

    fun clearFilters() {
        localState.update {
            it.copy(
                searchQuery = "",
                selectedCategory = null,
                availableOnly = false,
                nearMeOnly = false
            )
        }
    }

    fun addSupplyItem(
        title: String,
        modelOrSpec: String,
        category: String,
        description: String,
        pickupLocation: String,
        photoUri: String?,
        iconType: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addSupplyItem(
                title = title,
                modelOrSpec = modelOrSpec,
                category = category,
                description = description,
                pickupLocation = pickupLocation,
                photoUri = photoUri,
                iconType = iconType
            )
            localState.update { it.copy(snackbarMessage = "\"${title.trim()}\" is now listed for students to borrow!") }
            onSuccess()
        }
    }

    fun submitBorrowRequest(
        item: SupplyItemEntity,
        durationLabel: String,
        expectedReturnLabel: String,
        note: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.submitBorrowRequest(
                item = item,
                durationLabel = durationLabel,
                expectedReturnLabel = expectedReturnLabel,
                note = note
            )
            localState.update { it.copy(snackbarMessage = "Borrow request sent to ${item.ownerName}!") }
            onSuccess()
        }
    }

    fun approveIncomingRequest(requestId: Int, borrowerName: String) {
        viewModelScope.launch {
            repository.approveIncomingRequest(requestId)
            localState.update { it.copy(snackbarMessage = "Approved request from $borrowerName") }
        }
    }

    fun declineIncomingRequest(requestId: Int, borrowerName: String) {
        viewModelScope.launch {
            repository.declineIncomingRequest(requestId)
            localState.update { it.copy(snackbarMessage = "Declined request from $borrowerName") }
        }
    }

    fun markRequestReturned(requestId: Int, rating: Int, comment: String) {
        viewModelScope.launch {
            repository.markRequestReturned(requestId, rating, comment)
            localState.update { it.copy(snackbarMessage = "Item marked as returned. Thank you!") }
        }
    }

    fun selectChatChannel(channelId: String) {
        localState.update { it.copy(activeChatChannelId = channelId) }
    }

    fun sendChatMessage(text: String) {
        viewModelScope.launch {
            val currentChannelId = localState.value.activeChatChannelId
            val channelTitle = uiState.value.chatChannels
                .firstOrNull { it.channelId == currentChannelId }?.title ?: "Org Group Chat"
            repository.sendChatMessage(currentChannelId, channelTitle, text)
        }
    }

    fun postNeedToGroupChat(neededItem: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val formatted = "Hi everyone! Does anyone have a ${neededItem.trim()} I can borrow?"
            repository.sendChatMessage("org_group", "Org Group Chat", formatted)
            localState.update {
                it.copy(
                    activeChatChannelId = "org_group",
                    snackbarMessage = "Posted your request in Org Group Chat"
                )
            }
            onComplete()
        }
    }

    fun openDirectChatWithOwner(ownerName: String, onReady: () -> Unit) {
        viewModelScope.launch {
            val cleanOwner = ownerName.ifBlank { "Owner" }
            val channelId = repository.ensureDirectThread(cleanOwner)
            localState.update { it.copy(activeChatChannelId = channelId) }
            onReady()
        }
    }

    fun consumeSnackbar() {
        localState.update { it.copy(snackbarMessage = null) }
    }
}
