package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.GamiturnUiState
import com.example.ui.GamiturnViewModel
import com.example.ui.components.GamiturnWaveBackground
import com.example.ui.screens.AccountSetupScreen
import com.example.ui.screens.BorrowRequestScreen
import com.example.ui.screens.BrowseScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ItemDetailScreen
import com.example.ui.screens.LendItemSheet
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RequestsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.GamiturnTheme

private const val ROUTE_BROWSE = "browse"
private const val ROUTE_SEARCH = "search"
private const val ROUTE_REQUESTS = "requests"
private const val ROUTE_CHAT = "chat"
private const val ROUTE_ITEM_DETAIL = "item_detail"
private const val ROUTE_BORROW_REQUEST = "borrow_request"
private const val ROUTE_PROFILE = "profile"
private const val ROUTE_EDIT_PROFILE = "edit_profile"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GamiturnTheme {
                GamiturnApp()
            }
        }
    }
}

@Composable
fun GamiturnApp(viewModel: GamiturnViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.consumeSnackbar()
        }
    }

    if (!uiState.isInitialized) {
        GamiturnWaveBackground(showWatermark = true) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GamiturnTheme.palette.indigoAction)
            }
        }
        return
    }

    val isLoggedIn = uiState.profile?.isLoggedIn == true
    if (!isLoggedIn) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                AccountSetupScreen(
                    existingProfile = uiState.profile,
                    isEditingExisting = false,
                    onCreateAccount = { fullName, age, mobile, address, school, idUri, verified ->
                        viewModel.saveStudentProfile(
                            fullName = fullName,
                            ageText = age,
                            mobileNumber = mobile,
                            address = address,
                            schoolName = school,
                            idPhotoUri = idUri,
                            isIdVerified = verified,
                            onComplete = {}
                        )
                    },
                    onQuickDemoLogin = { viewModel.quickDemoLogin {} }
                )
            }
        }
        return
    }

    GamiturnMainShell(
        uiState = uiState,
        viewModel = viewModel,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun GamiturnMainShell(
    uiState: GamiturnUiState,
    viewModel: GamiturnViewModel,
    snackbarHostState: SnackbarHostState
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    var showLendSheet by rememberSaveable { mutableStateOf(false) }
    var requestsInitialTab by rememberSaveable { mutableIntStateOf(0) }

    val topLevelRoutes = remember { setOf(ROUTE_BROWSE, ROUTE_SEARCH, ROUTE_REQUESTS, ROUTE_CHAT) }
    val showNavigationChrome = currentRoute in topLevelRoutes

    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    GamiturnWaveBackground(showWatermark = true) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useRail = maxWidth >= 700.dp

            Row(modifier = Modifier.fillMaxSize()) {
                if (useRail && showNavigationChrome) {
                    GamiturnNavigationRail(
                        currentRoute = currentRoute,
                        pendingIncomingCount = uiState.pendingIncomingCount,
                        onNavigate = { route -> navigateTopLevel(route) },
                        onOpenLend = { showLendSheet = true }
                    )
                }

                Scaffold(
                    modifier = Modifier.weight(1f),
                    containerColor = Color.Transparent,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    contentWindowInsets = WindowInsets.statusBars,
                    bottomBar = {
                        if (!useRail && showNavigationChrome) {
                            GamiturnBottomNavBar(
                                currentRoute = currentRoute,
                                pendingIncomingCount = uiState.pendingIncomingCount,
                                onNavigate = { route -> navigateTopLevel(route) },
                                onOpenLend = { showLendSheet = true }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = ROUTE_BROWSE,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        composable(ROUTE_BROWSE) {
                            BrowseScreen(
                                uiState = uiState,
                                onOpenSearch = {
                                    viewModel.clearFilters()
                                    navigateTopLevel(ROUTE_SEARCH)
                                },
                                onSelectCategory = { category ->
                                    viewModel.applyCategoryOnly(category)
                                    navigateTopLevel(ROUTE_SEARCH)
                                },
                                onSelectItem = { itemId ->
                                    navController.navigate("$ROUTE_ITEM_DETAIL/$itemId")
                                },
                                onOpenProfile = {
                                    navController.navigate(ROUTE_PROFILE)
                                }
                            )
                        }

                        composable(ROUTE_SEARCH) {
                            SearchScreen(
                                uiState = uiState,
                                onQueryChange = viewModel::updateSearchQuery,
                                onToggleCategory = viewModel::setCategoryFilter,
                                onToggleAvailableOnly = viewModel::toggleAvailableOnly,
                                onToggleNearMeOnly = viewModel::toggleNearMeOnly,
                                onClearFilters = viewModel::clearFilters,
                                onSelectItem = { itemId ->
                                    navController.navigate("$ROUTE_ITEM_DETAIL/$itemId")
                                },
                                onPostNeedToChat = { neededText ->
                                    viewModel.postNeedToGroupChat(neededText) {
                                        navigateTopLevel(ROUTE_CHAT)
                                    }
                                }
                            )
                        }

                        composable(ROUTE_REQUESTS) {
                            RequestsScreen(
                                uiState = uiState,
                                initialTab = requestsInitialTab,
                                onApproveIncoming = viewModel::approveIncomingRequest,
                                onDeclineIncoming = viewModel::declineIncomingRequest,
                                onMarkReturned = viewModel::markRequestReturned,
                                onOpenItem = { itemId ->
                                    navController.navigate("$ROUTE_ITEM_DETAIL/$itemId")
                                }
                            )
                        }

                        composable(ROUTE_CHAT) {
                            ChatScreen(
                                uiState = uiState,
                                onSelectChannel = viewModel::selectChatChannel,
                                onSendMessage = viewModel::sendChatMessage
                            )
                        }

                        composable(
                            route = "$ROUTE_ITEM_DETAIL/{itemId}",
                            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
                        ) { entry ->
                            val itemId = entry.arguments?.getInt("itemId") ?: 0
                            val item by viewModel.observeItem(itemId).collectAsStateWithLifecycle(initialValue = null)
                            val reviews by viewModel.observeReviews(itemId).collectAsStateWithLifecycle(initialValue = emptyList())

                            ItemDetailScreen(
                                item = item,
                                reviews = reviews,
                                onBack = { navController.popBackStack() },
                                onRequestBorrow = { id ->
                                    navController.navigate("$ROUTE_BORROW_REQUEST/$id")
                                },
                                onMessageOwner = { ownerName ->
                                    viewModel.openDirectChatWithOwner(ownerName) {
                                        navigateTopLevel(ROUTE_CHAT)
                                    }
                                }
                            )
                        }

                        composable(
                            route = "$ROUTE_BORROW_REQUEST/{itemId}",
                            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
                        ) { entry ->
                            val itemId = entry.arguments?.getInt("itemId") ?: 0
                            val item by viewModel.observeItem(itemId).collectAsStateWithLifecycle(initialValue = null)

                            BorrowRequestScreen(
                                item = item,
                                onBack = { navController.popBackStack() },
                                onSendRequest = { targetItem, duration, expectedReturn, note ->
                                    viewModel.submitBorrowRequest(
                                        item = targetItem,
                                        durationLabel = duration,
                                        expectedReturnLabel = expectedReturn,
                                        note = note
                                    ) {
                                        requestsInitialTab = 1 // Jump to "My requests"
                                        navigateTopLevel(ROUTE_REQUESTS)
                                    }
                                }
                            )
                        }

                        composable(ROUTE_PROFILE) {
                            ProfileScreen(
                                profile = uiState.profile,
                                onBack = { navController.popBackStack() },
                                onEditProfile = { navController.navigate(ROUTE_EDIT_PROFILE) },
                                onSignOut = { viewModel.signOut() }
                            )
                        }

                        composable(ROUTE_EDIT_PROFILE) {
                            AccountSetupScreen(
                                existingProfile = uiState.profile,
                                isEditingExisting = true,
                                onCreateAccount = { fullName, age, mobile, address, school, idUri, verified ->
                                    viewModel.saveStudentProfile(
                                        fullName = fullName,
                                        ageText = age,
                                        mobileNumber = mobile,
                                        address = address,
                                        schoolName = school,
                                        idPhotoUri = idUri,
                                        isIdVerified = verified,
                                        onComplete = { navController.popBackStack() }
                                    )
                                },
                                onQuickDemoLogin = {},
                                onCancelEdit = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showLendSheet) {
        LendItemSheet(
            onDismiss = { showLendSheet = false },
            onSubmitItem = { title, model, category, desc, pickup, photoUri, iconType ->
                viewModel.addSupplyItem(
                    title = title,
                    modelOrSpec = model,
                    category = category,
                    description = desc,
                    pickupLocation = pickup,
                    photoUri = photoUri,
                    iconType = iconType
                ) {
                    showLendSheet = false
                    navigateTopLevel(ROUTE_BROWSE)
                }
            }
        )
    }
}

@Composable
private fun GamiturnBottomNavBar(
    currentRoute: String?,
    pendingIncomingCount: Int,
    onNavigate: (String) -> Unit,
    onOpenLend: () -> Unit
) {
    val palette = GamiturnTheme.palette
    Surface(
        color = palette.cardFrost,
        border = BorderStroke(1.dp, palette.cardBorder),
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = stringResource(R.string.nav_browse),
                selected = currentRoute == ROUTE_BROWSE,
                selectedIcon = Icons.Rounded.Home,
                unselectedIcon = Icons.Outlined.Home,
                onClick = { onNavigate(ROUTE_BROWSE) },
                testTag = "nav_browse"
            )
            BottomNavItem(
                label = stringResource(R.string.nav_search),
                selected = currentRoute == ROUTE_SEARCH,
                selectedIcon = Icons.Rounded.Search,
                unselectedIcon = Icons.Outlined.Search,
                onClick = { onNavigate(ROUTE_SEARCH) },
                testTag = "nav_search"
            )

            // Prominent center '+' Lend button matching PDF screens 2, 3, 6, 7
            FloatingActionButton(
                onClick = onOpenLend,
                shape = CircleShape,
                containerColor = palette.indigoAction,
                contentColor = Color.White,
                modifier = Modifier
                    .size(50.dp)
                    .testTag("nav_lend_fab")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.nav_lend)
                )
            }

            BottomNavItem(
                label = stringResource(R.string.nav_requests),
                selected = currentRoute == ROUTE_REQUESTS,
                selectedIcon = Icons.Rounded.Inbox,
                unselectedIcon = Icons.Outlined.Inbox,
                badgeCount = pendingIncomingCount,
                onClick = { onNavigate(ROUTE_REQUESTS) },
                testTag = "nav_requests"
            )
            BottomNavItem(
                label = stringResource(R.string.nav_chat),
                selected = currentRoute == ROUTE_CHAT,
                selectedIcon = Icons.Rounded.ChatBubble,
                unselectedIcon = Icons.Outlined.ChatBubbleOutline,
                onClick = { onNavigate(ROUTE_CHAT) },
                testTag = "nav_chat"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    testTag: String
) {
    val palette = GamiturnTheme.palette
    val tint = if (selected) palette.indigoAction else palette.textMuted

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .width(64.dp)
            .height(52.dp)
            .testTag(testTag)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BadgedBox(
                badge = {
                    if (badgeCount > 0) {
                        Badge(containerColor = palette.indigoAction) {
                            Text(text = badgeCount.toString(), color = Color.White)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (selected) selectedIcon else unselectedIcon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = tint
            )
        }
    }
}

@Composable
private fun GamiturnNavigationRail(
    currentRoute: String?,
    pendingIncomingCount: Int,
    onNavigate: (String) -> Unit,
    onOpenLend: () -> Unit
) {
    val palette = GamiturnTheme.palette
    NavigationRail(
        containerColor = palette.cardFrost,
        modifier = Modifier
            .fillMaxHeight()
            .windowInsetsPadding(WindowInsets.statusBars),
        header = {
            Spacer(modifier = Modifier.height(12.dp))
            FloatingActionButton(
                onClick = onOpenLend,
                containerColor = palette.indigoAction,
                contentColor = Color.White,
                modifier = Modifier.testTag("rail_lend_fab")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.nav_lend)
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        NavigationRailItem(
            selected = currentRoute == ROUTE_BROWSE,
            onClick = { onNavigate(ROUTE_BROWSE) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ROUTE_BROWSE) Icons.Rounded.Home else Icons.Outlined.Home,
                    contentDescription = stringResource(R.string.nav_browse)
                )
            },
            label = { Text(stringResource(R.string.nav_browse)) }
        )
        NavigationRailItem(
            selected = currentRoute == ROUTE_SEARCH,
            onClick = { onNavigate(ROUTE_SEARCH) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ROUTE_SEARCH) Icons.Rounded.Search else Icons.Outlined.Search,
                    contentDescription = stringResource(R.string.nav_search)
                )
            },
            label = { Text(stringResource(R.string.nav_search)) }
        )
        NavigationRailItem(
            selected = currentRoute == ROUTE_REQUESTS,
            onClick = { onNavigate(ROUTE_REQUESTS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (pendingIncomingCount > 0) {
                            Badge(containerColor = palette.indigoAction) {
                                Text(pendingIncomingCount.toString(), color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentRoute == ROUTE_REQUESTS) Icons.Rounded.Inbox else Icons.Outlined.Inbox,
                        contentDescription = stringResource(R.string.nav_requests)
                    )
                }
            },
            label = { Text(stringResource(R.string.nav_requests)) }
        )
        NavigationRailItem(
            selected = currentRoute == ROUTE_CHAT,
            onClick = { onNavigate(ROUTE_CHAT) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ROUTE_CHAT) Icons.Rounded.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                    contentDescription = stringResource(R.string.nav_chat)
                )
            },
            label = { Text(stringResource(R.string.nav_chat)) }
        )
    }
}
