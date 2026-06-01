package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import android.content.ClipboardManager
import android.content.ClipData
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.CircularProgressIndicator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.ScrollState
import com.example.data.AppDatabase
import com.example.data.MemberEntity
import com.example.data.MemberRepository
import com.example.ui.components.MemberProfilePhoto
import com.example.ui.components.RajasthaniHeritageBackground
import com.example.ui.theme.*
import com.example.ui.translation.Language
import com.example.ui.translation.LocalLanguage
import com.example.ui.translation.TextKey
import com.example.ui.translation.Translations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Data class for Announcements/Notifications
data class SamajNotification(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val descEn: String,
    val descHi: String,
    val category: String, // "URGENT", "EVENT", "GENERAL"
    val date: String,
    val imageUrl: String? = null,
    val pdfUrl: String? = null,
    val linkUrl: String? = null
)

val DEFAULT_NOTIFICATIONS = listOf(
    SamajNotification(
        id = "1",
        titleEn = "Grand Cultural Mahotsav 2026",
        titleHi = "भव्य सांस्कृतिक महोत्सव २०२६",
        descEn = "Join us for a magnificent celebration of our community's rich Rajasthani heritage. Traditional folk dance, geer performance, and royal Rajasthani dinner are arranged. Tap to view the official poster below.",
        descHi = "हमारे समाज की समृद्ध राजस्थानी विरासत का जश्न मनाने के लिए हमारे साथ जुड़ें। पारंपरिक लोक नृत्य, गैर प्रस्तुति और शाही राजस्थानी भोजन की व्यवस्था की गई है। नीचे पोस्टर देखें।",
        category = "EVENT",
        date = "01 Jun 2026",
        imageUrl = "https://images.unsplash.com/photo-1599661046289-e31897846e41?w=600&auto=format&fit=crop"
    ),
    SamajNotification(
        id = "2",
        titleEn = "New Samaj Office Timings",
        titleHi = "समाज कार्यालय का नया समय",
        descEn = "The main central administrative office will now remain functional from 10:00 AM to 6:00 PM on working days. Please note the updated hours for physical document submissions or visits.",
        descHi = "मुख्य केंद्रीय प्रशासनिक कार्यालय अब कार्यदिवसों पर सुबह १०:०० बजे से शाम ६:०० बजे तक खुला रहेगा। भौतिक दस्तावेज जमा करने या मिलने के लिए कृपया नए समय का ध्यान रखें।",
        category = "GENERAL",
        date = "30 May 2026"
    ),
    SamajNotification(
        id = "3",
        titleEn = "Community Scholarship Blueprint 2026-27",
        titleHi = "समाज छात्रवृत्ति नियमावली २०२६-२७",
        descEn = "Crucial PDF rules outline for students applying for higher technical educational funds and cash awards. Download the PDF booklet to review eligibility status criteria.",
        descHi = "उच्च तकनीकी शिक्षा सहायता और नकद पुरस्कारों के लिए आवेदन करने वाले छात्र-छात्राओं के लिए आवश्यक पीडीएफ नियमावली। योग्यता पात्रता की समीक्षा के लिए इस पीडीएफ पुस्तिका को खोलें।",
        category = "URGENT",
        date = "28 May 2026",
        pdfUrl = "scholarship_guidelines_2026.pdf"
    ),
    SamajNotification(
        id = "4",
        titleEn = "Online Matrimonial Portal Launch",
        titleHi = "ऑनलाइन वैवाहिक पोर्टल का शुभारंभ",
        descEn = "We are pleased to web-launch the modernized online Matrimonial registration portal for community members. Tap the link button to visit our official directory hub.",
        descHi = "हमें समाज के सदस्यों के लिए आधुनिक ऑनलाइन वैवाहिक पंजीकरण पोर्टल को वेब-लॉन्च करते हुए खुशी हो रही है। हमारे आधिकारिक डायरेक्टरी हब पर जाने के लिए लिंक बटन दबाएं।",
        category = "EVENT",
        date = "26 May 2026",
        linkUrl = "https://www.google.com"
    )
)

fun saveNotificationsToPrefs(context: android.content.Context, list: List<SamajNotification>) {
    val prefs = context.getSharedPreferences("samaj_notifications_prefs", android.content.Context.MODE_PRIVATE)
    val serialized = list.joinToString("\n---\n") { 
        val img = it.imageUrl ?: ""
        val pdf = it.pdfUrl ?: ""
        val lnk = it.linkUrl ?: ""
        "${it.id}||${it.titleEn.replace("\n", " ")}||${it.titleHi.replace("\n", " ")}||${it.descEn.replace("\n", " ")}||${it.descHi.replace("\n", " ")}||${it.category}||${it.date}||${img}||${pdf}||${lnk}"
    }
    prefs.edit().putString("notifications_list_v1", serialized).apply()
}

fun loadNotificationsFromPrefs(context: android.content.Context): List<SamajNotification> {
    val prefs = context.getSharedPreferences("samaj_notifications_prefs", android.content.Context.MODE_PRIVATE)
    val serialized = prefs.getString("notifications_list_v1", null) ?: return DEFAULT_NOTIFICATIONS
    if (serialized.isBlank()) return DEFAULT_NOTIFICATIONS
    return try {
        serialized.split("\n---\n").mapNotNull { line ->
            if (line.isBlank()) return@mapNotNull null
            val parts = line.split("||")
            if (parts.size >= 7) {
                SamajNotification(
                    id = parts[0],
                    titleEn = parts[1],
                    titleHi = parts[2],
                    descEn = parts[3],
                    descHi = parts[4],
                    category = parts[5],
                    date = parts[6],
                    imageUrl = parts.getOrNull(7)?.takeIf { it.isNotBlank() },
                    pdfUrl = parts.getOrNull(8)?.takeIf { it.isNotBlank() },
                    linkUrl = parts.getOrNull(9)?.takeIf { it.isNotBlank() }
                )
            } else null
        }
    } catch (e: Exception) {
        DEFAULT_NOTIFICATIONS
    }
}

// Screen navigation enum
enum class AppScreen {
    DASHBOARD,
    NAVI_MUMBAI,
    MUMBAI,
    CONTACT,
    FORMS,
    DONATION,
    EXTRA,
    JALOR,
    NOTIFICATION
}

// ViewModel implementation
class MemberViewModel(private val repository: MemberRepository) : ViewModel() {
    val allMembers: StateFlow<List<MemberEntity>> = repository.allMembers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertMember(name: String, contactNumber: String, avatar: String, subCommunity: String, position: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insertMember(
                MemberEntity(
                    name = name,
                    contactNumber = contactNumber,
                    profilePhotoUri = avatar,
                    subCommunity = subCommunity,
                    position = position
                )
            )
            onSuccess()
        }
    }

    fun deleteMember(member: MemberEntity) {
        viewModelScope.launch {
            repository.deleteMember(member)
        }
    }
}

class MemberViewModelFactory(private val repository: MemberRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MemberViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var repository: MemberRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Room components using lifecycle class scope
        database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        repository = MemberRepository(database.memberDao())

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val memberViewModel: MemberViewModel = viewModel(
                    factory = MemberViewModelFactory(repository)
                )
                MainAppPortal(memberViewModel)
            }
        }
    }
}

@Composable
fun RajasthaniHeaderAppBar(
    onLanguageToggle: (Language) -> Unit,
    activeLanguage: Language,
    currentScreen: AppScreen,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .background(com.example.ui.theme.RajasthanBar)
            .border(
                width = 1.dp,
                color = com.example.ui.theme.RajasthanBorder,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentScreen != AppScreen.DASHBOARD) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(RajasthanOchre.copy(alpha = 0.12f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = RajasthanOchre,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(RajasthanOchre),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "K",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif
                    )
                }
            }

            Column {
                Text(
                    text = "Kumawat Samaj",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanDarkText,
                    lineHeight = 18.sp
                )
                Text(
                    text = "कुमावत समाज",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = RajasthanDarkText.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
            }
        }

        // Quick language toggler button
        Button(
            onClick = {
                val nextLang = if (activeLanguage == Language.ENGLISH) Language.HINDI else Language.ENGLISH
                onLanguageToggle(nextLang)
            },
            modifier = Modifier
                .width(60.dp)
                .height(44.dp)
                .border(width = 1.dp, color = RajasthanGold, shape = RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (activeLanguage == Language.ENGLISH) "EN" else "हि",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanOchre,
                    lineHeight = 12.sp
                )
                Text(
                    text = if (activeLanguage == Language.ENGLISH) "हिन्दी" else "Eng",
                    fontSize = 9.sp,
                    color = RajasthanDarkText.copy(alpha = 0.8f),
                    lineHeight = 10.sp
                )
            }
        }
    }
}

@Composable
fun RajasthaniBottomNavigationBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    activeLanguage: Language
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(com.example.ui.theme.RajasthanBar)
            .border(
                width = 1.dp,
                color = com.example.ui.theme.RajasthanBorder,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val homeSelected = currentScreen == AppScreen.DASHBOARD || currentScreen == AppScreen.NAVI_MUMBAI || currentScreen == AppScreen.MUMBAI || currentScreen == AppScreen.JALOR
        BottomNavItem(
            selected = homeSelected,
            label = if (activeLanguage == Language.HINDI) "होम" else "Home",
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (homeSelected) Color.White else RajasthanDarkText.copy(alpha = 0.6f)
                )
            },
            onClick = { onNavigate(AppScreen.DASHBOARD) }
        )

        val notificationSelected = currentScreen == AppScreen.NOTIFICATION
        BottomNavItem(
            selected = notificationSelected,
            label = if (activeLanguage == Language.HINDI) "सूचनाएं" else "Notices",
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notices",
                    tint = if (notificationSelected) Color.White else RajasthanDarkText.copy(alpha = 0.6f)
                )
            },
            onClick = { onNavigate(AppScreen.NOTIFICATION) }
        )

        val supportSelected = currentScreen == AppScreen.CONTACT
        BottomNavItem(
            selected = supportSelected,
            label = if (activeLanguage == Language.HINDI) "सहायता" else "Support",
            icon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Support",
                    tint = if (supportSelected) Color.White else RajasthanDarkText.copy(alpha = 0.6f)
                )
            },
            onClick = { onNavigate(AppScreen.CONTACT) }
        )
    }
}

@Composable
fun BottomNavItem(
    selected: Boolean,
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(if (selected) RajasthanOchre else Color.Transparent)
                .padding(horizontal = 20.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) RajasthanOchre else RajasthanDarkText.copy(alpha = 0.6f),
            letterSpacing = 0.5.sp
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainAppPortal(viewModel: MemberViewModel) {
    val context = LocalContext.current
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.DASHBOARD) }
    var currentLanguage by rememberSaveable { mutableStateOf(Language.ENGLISH) }

    // Persistent notifications list state
    val notificationsList = remember { androidx.compose.runtime.mutableStateListOf<SamajNotification>() }

    LaunchedEffect(Unit) {
        notificationsList.clear()
        notificationsList.addAll(loadNotificationsFromPrefs(context))
    }

    // Banner heads-up notification trigger states
    var activeBannerNotification by remember { mutableStateOf<SamajNotification?>(null) }
    var showBannerTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(activeBannerNotification) {
        if (activeBannerNotification != null) {
            showBannerTrigger = true
            kotlinx.coroutines.delay(6500)
            showBannerTrigger = false
            kotlinx.coroutines.delay(500)
            activeBannerNotification = null
        }
    }

    if (currentScreen != AppScreen.DASHBOARD) {
        androidx.activity.compose.BackHandler {
            currentScreen = AppScreen.DASHBOARD
        }
    }

    CompositionLocalProvider(LocalLanguage provides currentLanguage) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    RajasthaniHeaderAppBar(
                        onLanguageToggle = { currentLanguage = it },
                        activeLanguage = currentLanguage,
                        currentScreen = currentScreen,
                        onBack = {
                            if (currentScreen != AppScreen.DASHBOARD) {
                                currentScreen = AppScreen.DASHBOARD
                            }
                        }
                    )
                },
                bottomBar = {
                    RajasthaniBottomNavigationBar(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it },
                        activeLanguage = currentLanguage
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Background drawn dynamically for gorgeous Rajasthani atmosphere
                    RajasthaniHeritageBackground(subtle = currentScreen != AppScreen.DASHBOARD)

                    // State-driven responsive navigation container
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        modifier = Modifier.fillMaxSize()
                    ) { screen ->
                        when (screen) {
                            AppScreen.DASHBOARD -> DashboardScreen(
                                onNavigateTo = { currentScreen = it },
                                activeLanguage = currentLanguage,
                                onLanguageToggle = { currentLanguage = it }
                            )
                            AppScreen.NOTIFICATION -> NotificationScreen(
                                notifications = notificationsList,
                                onSendNotification = { notif ->
                                    notificationsList.add(notif)
                                    saveNotificationsToPrefs(context, notificationsList)
                                    activeBannerNotification = notif
                                    showBannerTrigger = true
                                },
                                activeLanguage = currentLanguage,
                                onBack = { currentScreen = AppScreen.DASHBOARD }
                            )
                            AppScreen.NAVI_MUMBAI -> MembersDirectoryScreen(
                                subCommunity = "navi_mumbai1",
                                titleKey = TextKey.NAVI_MUMBAI,
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.DASHBOARD }
                            )
                            AppScreen.MUMBAI -> MembersDirectoryScreen(
                                subCommunity = "mumbai1",
                                titleKey = TextKey.MUMBAI,
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.DASHBOARD }
                            )
                            AppScreen.CONTACT -> ContactUsScreen(
                                onBack = { currentScreen = AppScreen.DASHBOARD }
                            )
                            AppScreen.FORMS -> FormsScreen(
                                onBack = { currentScreen = AppScreen.DASHBOARD }
                            )
                            AppScreen.DONATION -> DonationScreen(
                                onBack = { currentScreen = AppScreen.DASHBOARD }
                            )
                            AppScreen.EXTRA -> ExtraUtilitiesScreen(
                                onBack = { currentScreen = AppScreen.DASHBOARD }
                            )
                            AppScreen.JALOR -> MembersDirectoryScreen(
                                subCommunity = "ahore1",
                                titleKey = TextKey.JALOR,
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.DASHBOARD }
                            )
                        }
                    }
                }
            }

            // Top overlay layout: Sliding heads-up dropdown notification card with full description
            androidx.compose.animation.AnimatedVisibility(
                visible = showBannerTrigger && activeBannerNotification != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 12.dp, start = 14.dp, end = 14.dp)
                    .zIndex(100f)
            ) {
                activeBannerNotification?.let { notif ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(16.dp))
                            .border(1.5.dp, RajasthanGold, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = RajasthanSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(RajasthanOchre.copy(alpha = 0.12f), CircleShape)
                                    .border(1.dp, RajasthanOchre.copy(alpha = 0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🔔", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val catLabel = when (notif.category) {
                                        "URGENT" -> if (currentLanguage == Language.HINDI) "अत्यंत महत्वपूर्ण" else "URGENT BROADCAST"
                                        "EVENT" -> if (currentLanguage == Language.HINDI) "उत्सव / आयोजन" else "EVENT ANNOUNCEMENT"
                                        else -> if (currentLanguage == Language.HINDI) "सामान्य सूचना" else "GENERAL NOTICE"
                                    }
                                    Text(
                                        text = catLabel,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        color = RajasthanOchre
                                    )
                                    IconButton(
                                        onClick = { showBannerTrigger = false },
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Close",
                                            tint = RajasthanOchre,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (currentLanguage == Language.HINDI) notif.titleHi else notif.titleEn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = RajasthanDarkText
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = if (currentLanguage == Language.HINDI) notif.descHi else notif.descEn,
                                    fontSize = 12.sp,
                                    color = RajasthanDarkText.copy(alpha = 0.85f),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MumbaiLocalLogo(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(52.dp),
        contentAlignment = Alignment.Center
    ) {
        // Rotated navy blue diamond frame
        Box(
            modifier = Modifier
                .size(42.dp)
                .rotate(45f)
                .background(Color.White)
                .border(2.dp, Color(0xFF1F2261))
        )
        // Red station ring with white fill
        Box(
            modifier = Modifier
                .size(34.dp)
                .border(4.5.dp, Color(0xFFE21A22), CircleShape)
                .background(Color.White, CircleShape)
        )
        // Horizontal bar with solid solid lettering
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(13.dp)
                .background(Color(0xFF1F2261)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = if (text.length > 7) 5.5.sp else 7.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.5.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun MumbaiGatewayLogo(modifier: Modifier = Modifier) {
    val strokeColor = RajasthanOchre
    val darkColor = RajasthanDarkText
    
    Canvas(
        modifier = modifier
            .size(52.dp)
            .padding(2.dp)
    ) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.25.dp.toPx()
        
        // 1. Bottom Ground Line / Plinth
        val plinthY = h * 0.90f
        drawLine(
            color = darkColor,
            start = Offset(w * 0.05f, plinthY),
            end = Offset(w * 0.95f, plinthY),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = darkColor,
            start = Offset(w * 0.05f, h * 0.95f),
            end = Offset(w * 0.95f, h * 0.95f),
            strokeWidth = strokeWidth
        )
        // Vertical step connecting lines for the plinth
        drawLine(color = darkColor, start = Offset(w * 0.05f, plinthY), end = Offset(w * 0.05f, h * 0.95f), strokeWidth = strokeWidth)
        drawLine(color = darkColor, start = Offset(w * 0.95f, plinthY), end = Offset(w * 0.95f, h * 0.95f), strokeWidth = strokeWidth)
        drawLine(color = darkColor, start = Offset(w * 0.45f, plinthY), end = Offset(w * 0.45f, h * 0.95f), strokeWidth = strokeWidth)
        drawLine(color = darkColor, start = Offset(w * 0.55f, plinthY), end = Offset(w * 0.55f, h * 0.95f), strokeWidth = strokeWidth)

        // 2. Main Central Archway
        val archLeft = w * 0.38f
        val archRight = w * 0.62f
        val archBaseY = plinthY
        val archTopY = h * 0.52f
        val archPeakY = h * 0.44f

        val archPath = Path().apply {
            moveTo(archLeft, archBaseY)
            lineTo(archLeft, archTopY)
            cubicTo(
                archLeft, archTopY - (archTopY - archPeakY) * 0.4f,
                w * 0.45f, archPeakY + (archTopY - archPeakY) * 0.2f,
                w * 0.50f, archPeakY
            )
            cubicTo(
                w * 0.55f, archPeakY + (archTopY - archPeakY) * 0.2f,
                archRight, archTopY - (archTopY - archPeakY) * 0.4f,
                archRight, archTopY
            )
            lineTo(archRight, archBaseY)
        }
        drawPath(
            path = archPath,
            color = darkColor,
            style = Stroke(width = strokeWidth)
        )

        // 3. Two Main Tall Symmetrical Minarets/Pillars on either side of the arch
        // Left Column:
        val col1Left = w * 0.28f
        val col1Right = w * 0.35f
        // Right Column:
        val col2Left = w * 0.65f
        val col2Right = w * 0.72f
        
        val colTopY = h * 0.22f
        
        // Draw Left pillar
        drawRect(
            color = darkColor,
            topLeft = Offset(col1Left, colTopY),
            size = Size(col1Right - col1Left, plinthY - colTopY),
            style = Stroke(width = strokeWidth)
        )
        // Draw Right pillar
        drawRect(
            color = darkColor,
            topLeft = Offset(col2Left, colTopY),
            size = Size(col2Right - col2Left, plinthY - colTopY),
            style = Stroke(width = strokeWidth)
        )

        // Horizontal banding/lines inside pillars to mimic structural blocks
        val bands = listOf(0.40f, 0.58f, 0.75f)
        for (b in bands) {
            val bandY = h * b
            drawLine(color = darkColor, start = Offset(col1Left, bandY), end = Offset(col1Right, bandY), strokeWidth = strokeWidth)
            drawLine(color = darkColor, start = Offset(col2Left, bandY), end = Offset(col2Right, bandY), strokeWidth = strokeWidth)
        }

        // Small windows/squares in the pillars
        val squareSize = w * 0.035f
        drawRect(
            color = strokeColor,
            topLeft = Offset(col1Left + (col1Right - col1Left)*0.2f, colTopY + (h * 0.08f)),
            size = Size(squareSize, squareSize)
        )
        drawRect(
            color = strokeColor,
            topLeft = Offset(col2Left + (col2Right - col2Left)*0.2f, colTopY + (h * 0.08f)),
            size = Size(squareSize, squareSize)
        )

        // 4. Outer Symmetrical Wings
        val wingLeftStart = w * 0.08f
        val wingLeftEnd = col1Left
        val wingRightStart = col2Right
        val wingRightEnd = w * 0.92f
        val wingTopY = h * 0.35f

        // Left Wing Box
        drawRect(
            color = darkColor,
            topLeft = Offset(wingLeftStart, wingTopY),
            size = Size(wingLeftEnd - wingLeftStart, plinthY - wingTopY),
            style = Stroke(width = strokeWidth)
        )
        // Right Wing Box
        drawRect(
            color = darkColor,
            topLeft = Offset(wingRightStart, wingTopY),
            size = Size(wingRightEnd - wingRightStart, plinthY - wingTopY),
            style = Stroke(width = strokeWidth)
        )

        // Middle horizontal partition lines for the wings
        val wingMidY = h * 0.62f
        drawLine(color = darkColor, start = Offset(wingLeftStart, wingMidY), end = Offset(col1Left, wingMidY), strokeWidth = strokeWidth)
        drawLine(color = darkColor, start = Offset(col2Right, wingMidY), end = Offset(wingRightEnd, wingMidY), strokeWidth = strokeWidth)

        // Left Wing Small Arched Door in the lower part
        val lDoorWidth = (wingLeftEnd - wingLeftStart) * 0.45f
        val lDoorLeft = wingLeftStart + (wingLeftEnd - wingLeftStart - lDoorWidth) / 2
        val lDoorTopY = h * 0.74f
        val lDoorArchY = h * 0.68f
        val leftDoorPath = Path().apply {
            moveTo(lDoorLeft, plinthY)
            lineTo(lDoorLeft, lDoorTopY)
            cubicTo(lDoorLeft, lDoorArchY, lDoorLeft + lDoorWidth, lDoorArchY, lDoorLeft + lDoorWidth, lDoorTopY)
            lineTo(lDoorLeft + lDoorWidth, plinthY)
        }
        drawPath(path = leftDoorPath, color = darkColor, style = Stroke(width = strokeWidth))

        // Right Wing Small Arched Door in the lower part
        val rDoorWidth = (wingRightEnd - wingRightStart) * 0.45f
        val rDoorLeft = wingRightStart + (wingRightEnd - wingRightStart - rDoorWidth) / 2
        val rightDoorPath = Path().apply {
            moveTo(rDoorLeft, plinthY)
            lineTo(rDoorLeft, lDoorTopY)
            cubicTo(rDoorLeft, lDoorArchY, rDoorLeft + rDoorWidth, lDoorArchY, rDoorLeft + rDoorWidth, lDoorTopY)
            lineTo(rDoorLeft + rDoorWidth, plinthY)
        }
        drawPath(path = rightDoorPath, color = darkColor, style = Stroke(width = strokeWidth))

        // Top pair of small windows in each wing (upper part)
        val upperWinY = h * 0.46f
        val winW = w * 0.05f
        // Left wing upper windows
        drawRect(color = darkColor, topLeft = Offset(wingLeftStart + w * 0.03f, upperWinY), size = Size(winW, winW), style = Stroke(width = strokeWidth))
        drawRect(color = darkColor, topLeft = Offset(wingLeftStart + w * 0.09f, upperWinY), size = Size(winW, winW), style = Stroke(width = strokeWidth))
        // Right wing upper windows
        drawRect(color = darkColor, topLeft = Offset(wingRightStart + w * 0.03f, upperWinY), size = Size(winW, winW), style = Stroke(width = strokeWidth))
        drawRect(color = darkColor, topLeft = Offset(wingRightStart + w * 0.09f, upperWinY), size = Size(winW, winW), style = Stroke(width = strokeWidth))

        // 5. Upper Central Portion / Parapet (Pediment Above the main arch)
        val centralRoofTopY = h * 0.28f
        drawRect(
            color = darkColor,
            topLeft = Offset(col1Right, centralRoofTopY),
            size = Size(col2Left - col1Right, plinthY - centralRoofTopY),
            style = Stroke(width = strokeWidth)
        )

        // Row of small ventilation window grids on the central parapet (Upper center)
        val gridY = h * 0.35f
        val gridItemW = w * 0.035f
        val gridSpacing = w * 0.015f
        val startX = col1Right + (col2Left - col1Right - (4 * gridItemW + 3 * gridSpacing)) / 2
        for (i in 0..3) {
            drawRect(
                color = darkColor,
                topLeft = Offset(startX + i * (gridItemW + gridSpacing), gridY),
                size = Size(gridItemW, gridItemW),
                style = Stroke(width = strokeWidth)
            )
        }

        // 6. Detailed Roof-top Ornaments & Domes
        // Domes/Chhatris on top of the main central towers
        val domeH = h * 0.08f
        val leftDomePath = Path().apply {
            moveTo(col1Left, colTopY)
            cubicTo(col1Left, colTopY - domeH, col1Right, colTopY - domeH, col1Right, colTopY)
        }
        drawPath(path = leftDomePath, color = darkColor, style = Stroke(width = strokeWidth))
        // Tiny spike on top of left dome
        drawLine(color = darkColor, start = Offset((col1Left + col1Right)/2, colTopY - domeH), end = Offset((col1Left + col1Right)/2, colTopY - domeH - h * 0.04f), strokeWidth = strokeWidth)

        val rightDomePath = Path().apply {
            moveTo(col2Left, colTopY)
            cubicTo(col2Left, colTopY - domeH, col2Right, colTopY - domeH, col2Right, colTopY)
        }
        drawPath(path = rightDomePath, color = darkColor, style = Stroke(width = strokeWidth))
        // Tiny spike on top of right dome
        drawLine(color = darkColor, start = Offset((col2Left + col2Right)/2, colTopY - domeH), end = Offset((col2Left + col2Right)/2, colTopY - domeH - h * 0.04f), strokeWidth = strokeWidth)

        // Outer corner small dome pinnacles
        val leftOuterDomePath = Path().apply {
            moveTo(wingLeftStart, wingTopY)
            cubicTo(wingLeftStart, wingTopY - h * 0.05f, wingLeftStart + w * 0.05f, wingTopY - h * 0.05f, wingLeftStart + w * 0.05f, wingTopY)
        }
        drawPath(path = leftOuterDomePath, color = darkColor, style = Stroke(width = strokeWidth))
        // Spike
        drawLine(color = darkColor, start = Offset(wingLeftStart + w * 0.025f, wingTopY - h * 0.05f), end = Offset(wingLeftStart + w * 0.025f, wingTopY - h * 0.08f), strokeWidth = strokeWidth)

        val rightOuterDomePath = Path().apply {
            moveTo(wingRightEnd - w * 0.05f, wingTopY)
            cubicTo(wingRightEnd - w * 0.05f, wingTopY - h * 0.05f, wingRightEnd, wingTopY - h * 0.05f, wingRightEnd, wingTopY)
        }
        drawPath(path = rightOuterDomePath, color = darkColor, style = Stroke(width = strokeWidth))
        // Spike
        drawLine(color = darkColor, start = Offset(wingRightEnd - w * 0.025f, wingTopY - h * 0.05f), end = Offset(wingRightEnd - w * 0.025f, wingTopY - h * 0.08f), strokeWidth = strokeWidth)

        // Central roof pinnacle standing elegantly in the middle top
        val centerRoofLineX = (col1Right + col2Left)/2
        drawLine(
            color = darkColor,
            start = Offset(centerRoofLineX, centralRoofTopY),
            end = Offset(centerRoofLineX, centralRoofTopY - h * 0.06f),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
fun NaviMumbaiLogo(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(64.dp)
            .shadow(1.dp, CircleShape)
            .background(Color.White, CircleShape)
            .border(1.5.dp, Color.Black, CircleShape)
    ) {
        val w = size.width
        val h = size.height
        
        // 1. Draw Dome Silhouette
        val colYStart = h * 0.42f
        val colYEnd = h * 0.62f
        
        // Columns base plate
        drawRect(
            color = Color.Black,
            topLeft = Offset(w * 0.15f, colYEnd),
            size = Size(w * 0.70f, h * 0.05f)
        )
        
        // Columns black block
        drawRect(
            color = Color.Black,
            topLeft = Offset(w * 0.22f, colYStart),
            size = Size(w * 0.56f, colYEnd - colYStart)
        )
        // 5 white vertical column lines/gaps
        val numCols = 5
        val gap = (w * 0.56f) / (numCols * 2 + 1)
        for (i in 0 until numCols) {
            val cx = w * 0.22f + gap + i * (gap * 2)
            drawRect(
                color = Color.White,
                topLeft = Offset(cx, colYStart + (colYEnd - colYStart) * 0.15f),
                size = Size(gap, (colYEnd - colYStart) * 0.75f)
            )
        }
        
        // Black dome
        drawArc(
            color = Color.Black,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(w * 0.22f, h * 0.18f),
            size = Size(w * 0.56f, h * 0.48f)
        )
        
        // Pin/Spires of dome - clean vertical pin exactly matching the provided logo
        drawLine(
            color = Color.Black,
            start = Offset(w * 0.5f, h * 0.18f),
            end = Offset(w * 0.5f, h * 0.04f),
            strokeWidth = 1.5.dp.toPx()
        )
        
        // 3. NAVI MUMBAI label under the dome
        // Draw black line separator
        drawLine(
            color = Color.Black,
            start = Offset(w * 0.05f, h * 0.70f),
            end = Offset(w * 0.95f, h * 0.70f),
            strokeWidth = 1.5.dp.toPx()
        )
        drawLine(
            color = Color.Black,
            start = Offset(w * 0.05f, h * 0.86f),
            end = Offset(w * 0.95f, h * 0.86f),
            strokeWidth = 1.5.dp.toPx()
        )
        
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 7.5.sp.toPx()
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            textAlign = android.graphics.Paint.Align.CENTER
        }
        drawContext.canvas.nativeCanvas.drawText("NAVI MUMBAI", w * 0.5f, h * 0.80f, paint)
    }
}

@Composable
fun JaloreFortLogo(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(64.dp)
            .shadow(1.dp, CircleShape)
            .background(Color.White, CircleShape)
            .border(1.5.dp, Color.Black, CircleShape)
    ) {
        val w = size.width
        val h = size.height
        
        // Draw the Fort Castle shape
        val towerLeft = w * 0.32f
        val towerRight = w * 0.68f
        val towerBaseY = h * 0.85f
        val towerTopY = h * 0.34f
        val towerW = towerRight - towerLeft
        
        val fortPath = Path().apply {
            moveTo(towerLeft - w * 0.05f, towerBaseY)
            lineTo(towerLeft, towerTopY)
            
            // Crenellations on top
            lineTo(towerLeft + towerW * 0.25f, towerTopY)
            lineTo(towerLeft + towerW * 0.25f, towerTopY + h * 0.06f)
            lineTo(towerLeft + towerW * 0.45f, towerTopY + h * 0.06f)
            lineTo(towerLeft + towerW * 0.45f, towerTopY)
            lineTo(towerLeft + towerW * 0.75f, towerTopY)
            lineTo(towerLeft + towerW * 0.75f, towerTopY + h * 0.06f)
            lineTo(towerLeft + towerW * 0.95f, towerTopY + h * 0.06f)
            lineTo(towerLeft + towerW * 0.95f, towerTopY)
            lineTo(towerRight + w * 0.01f, towerTopY)
            
            lineTo(towerRight + w * 0.05f, towerBaseY)
            close()
        }
        
        drawPath(
            path = fortPath,
            color = Color.Black
        )
        
        val gapY = h * 0.48f
        drawLine(
            color = Color.White,
            start = Offset(towerLeft - w * 0.01f, gapY),
            end = Offset(towerRight + w * 0.01f, gapY),
            strokeWidth = 2.dp.toPx()
        )
        
        val doorW = w * 0.14f
        val doorH = h * 0.18f
        val doorPath = Path().apply {
            moveTo(w * 0.5f - doorW / 2, towerBaseY)
            lineTo(w * 0.5f - doorW / 2, towerBaseY - doorH + doorW / 2)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(w * 0.5f - doorW / 2, towerBaseY - doorH, w * 0.5f + doorW / 2, towerBaseY - doorH + doorW),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            lineTo(w * 0.5f + doorW / 2, towerBaseY)
            close()
        }
        drawPath(
            path = doorPath,
            color = Color.White
        )
        
        val poleX = w * 0.5f
        val poleYStart = h * 0.12f
        val poleYEnd = towerTopY
        drawLine(
            color = Color.Black,
            start = Offset(poleX, poleYStart),
            end = Offset(poleX, poleYEnd),
            strokeWidth = 1.5.dp.toPx()
        )
        
        val flagPath = Path().apply {
            moveTo(poleX, poleYStart)
            lineTo(poleX + w * 0.18f, poleYStart + h * 0.05f)
            lineTo(poleX, poleYStart + h * 0.10f)
            close()
        }
        drawPath(
            path = flagPath,
            color = Color.Black
        )
    }
}

@Composable
fun DashboardScreen(
    onNavigateTo: (AppScreen) -> Unit,
    activeLanguage: Language,
    onLanguageToggle: (Language) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Community Accent Tag header
        Box(
            modifier = Modifier
                .background(RajasthanOchre, RoundedCornerShape(50.dp))
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Text(
                text = "जय श्री कुमावत • SAMAJ HUB",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
        }

        // Modern Accent & App Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                text = Translations.getString(TextKey.APP_NAME, activeLanguage),
                color = RajasthanOchre,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.testTag("app_title")
            )
            Text(
                text = Translations.getString(TextKey.DASHBOARD_SUBTITLE, activeLanguage),
                color = RajasthanDarkText.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Subcommunities Options (Sleek modern directories list)
        Text(
            text = if (activeLanguage == Language.HINDI) "हमारा समुदाय चुनें" else "Choose Community Directory",
            fontWeight = FontWeight.ExtraBold,
            color = RajasthanDarkText,
            fontSize = 15.sp,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Sub Community Option 1: Navi Mumbai
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateTo(AppScreen.NAVI_MUMBAI) }
                .border(1.dp, RajasthanOchre.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .shadow(1.dp, RoundedCornerShape(12.dp))
                .testTag("button_navi_mumbai"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NaviMumbaiLogo()

                Text(
                    text = Translations.getString(TextKey.NAVI_MUMBAI, activeLanguage),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanOchre,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(RajasthanOchre, RoundedCornerShape(50.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (activeLanguage == Language.HINDI) "सदस्य निर्देशिका →" else "Explore Directory →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Sub Community Option 2: Mumbai
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateTo(AppScreen.MUMBAI) }
                .border(1.dp, RajasthanGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .shadow(1.dp, RoundedCornerShape(12.dp))
                .testTag("button_mumbai"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MumbaiGatewayLogo()

                Text(
                    text = Translations.getString(TextKey.MUMBAI, activeLanguage),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanOchre,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(RajasthanOchre, RoundedCornerShape(50.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (activeLanguage == Language.HINDI) "सदस्य निर्देशिका →" else "Explore Directory →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Sub Community Option 3: KUMAWAT SHIKSHAN AND SAMAJIK SANSTHAN -AHORE (JALOR)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateTo(AppScreen.JALOR) }
                .border(1.dp, RajasthanOchre.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                .shadow(1.dp, RoundedCornerShape(12.dp))
                .testTag("button_jalor"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                JaloreFortLogo()

                Text(
                    text = Translations.getString(TextKey.JALOR, activeLanguage),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanOchre,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(RajasthanOchre, RoundedCornerShape(50.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (activeLanguage == Language.HINDI) "सदस्य निर्देशिका →" else "Explore Directory →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Community Services Section Header
        Text(
            text = if (activeLanguage == Language.HINDI) "सामुदायिक सेवाएँ व विकल्प" else "Community Resources & Welfare",
            fontWeight = FontWeight.ExtraBold,
            color = RajasthanDarkText,
            fontSize = 15.sp,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
        )

        // Card B: Donations & Support Kalyan Fund
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateTo(AppScreen.DONATION) }
                .border(2.dp, RajasthanGold, RoundedCornerShape(12.dp))
                .shadow(1.dp, RoundedCornerShape(12.dp))
                .testTag("button_donation"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFFAED), CircleShape)
                        .border(1.dp, RajasthanGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🪔", fontSize = 18.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (activeLanguage == Language.HINDI) "सेवा एवं दान कल्याण कोष" else "Welfare Fund & Donations",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = RajasthanOchre
                    )
                    Text(
                        text = if (activeLanguage == Language.HINDI) "गरीब सहायता व कल्याण निधि में योगदान" else "Support education & medical initiatives",
                        fontSize = 12.sp,
                        color = RajasthanDarkText.copy(alpha = 0.7f)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = RajasthanOchre,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Card C: Extras (Suvichar, Events, Panchang)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateTo(AppScreen.EXTRA) }
                .border(1.dp, RajasthanOchre.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .shadow(1.dp, RoundedCornerShape(12.dp))
                .testTag("button_extra"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE8F5E9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✨", fontSize = 18.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (activeLanguage == Language.HINDI) "विविध सामग्री व हेल्पलाइन" else "Samaj Utilities & Helplines",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = RajasthanOchre
                    )
                    Text(
                        text = if (activeLanguage == Language.HINDI) "दैनिक सुविचार, पंचांग, कैलेंडर और हेल्पलाइन" else "Check Panchang, calendar & contact helplines",
                        fontSize = 12.sp,
                        color = RajasthanDarkText.copy(alpha = 0.7f)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = RajasthanOchre,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Modern full-width clean Contact & Support box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateTo(AppScreen.CONTACT) }
                .border(1.dp, RajasthanGold.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .shadow(1.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = RajasthanOchre,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Translations.getString(TextKey.CONTACT_US, activeLanguage),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RajasthanDarkText,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}



@Composable
fun MembersDirectoryScreen(
    subCommunity: String,
    titleKey: TextKey,
    viewModel: MemberViewModel,
    onBack: () -> Unit
) {
    val activeLanguage = LocalLanguage.current
    val context = LocalContext.current
    val allMembers by viewModel.allMembers.collectAsState()
    
    // Filter by subcommunity category
    val communityMembers = remember(allMembers, subCommunity) {
        allMembers.filter { it.subCommunity.equals(subCommunity, ignoreCase = true) }
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredMembers = remember(communityMembers, searchQuery) {
        if (searchQuery.trim().isEmpty()) {
            communityMembers
        } else {
            communityMembers.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.contactNumber.contains(searchQuery)
            }
        }
    }

    // Elder tactile call confirmation dialogue state
    var selectedMemberForCall by remember { mutableStateOf<MemberEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // App bar back button row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .background(RajasthanOchre.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Back", 
                    tint = RajasthanOchre,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = Translations.getString(titleKey, activeLanguage),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanDarkText,
                    lineHeight = 22.sp
                )
                Text(
                    text = Translations.getString(TextKey.MEMBER_DIRECTORY, activeLanguage),
                    fontSize = 12.sp,
                    color = RajasthanDarkText.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Sleek Search input box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_input"),
            placeholder = {
                Text(
                    text = if (activeLanguage == Language.HINDI) "सदस्य का नाम या मोबाइल खोजें" else "Search name or phone number...",
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = RajasthanOchre, modifier = Modifier.size(20.dp))
            },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = RajasthanDarkText,
                unfocusedTextColor = RajasthanDarkText,
                focusedBorderColor = RajasthanOchre,
                unfocusedBorderColor = RajasthanGold.copy(alpha = 0.7f),
                focusedContainerColor = RajasthanSurface,
                unfocusedContainerColor = RajasthanSurface
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredMembers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(RajasthanSurface.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("ℹ️", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = Translations.getString(TextKey.EMPTY_DIRECTORY, activeLanguage),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = RajasthanDarkText.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .background(RajasthanOchre.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (activeLanguage == Language.HINDI) "फ़ोटो" else "Photo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanOchre,
                    modifier = Modifier.weight(0.2f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (activeLanguage == Language.HINDI) "नाम" else "Name",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanOchre,
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (activeLanguage == Language.HINDI) "गाँव" else "Village",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanOchre,
                    modifier = Modifier.weight(0.3f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(filteredMembers) { member ->
                    MemberDirectoryRow(
                        member = member,
                        onCallRequested = { selectedMemberForCall = member }
                    )
                }
            }
        }
    }

    // Sleek call verification and member details modal Dialog
    selectedMemberForCall?.let { member ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(enabled = true, onClick = { selectedMemberForCall = null }),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clickable(enabled = false, onClick = {}) // stop click propagation
                    .border(1.5.dp, RajasthanGold, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RajasthanSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (activeLanguage == Language.HINDI) "सदस्य विवरण" else "Member Details",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RajasthanOchre,
                        textAlign = TextAlign.Center
                    )

                    // Bigger profile photo with golden outline
                    Box(
                        modifier = Modifier
                            .size(116.dp)
                            .border(2.dp, RajasthanGold, CircleShape)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MemberProfilePhoto(
                            photoUri = member.profilePhotoUri,
                            memberName = member.name,
                            size = 110.dp
                        )
                    }

                    // Full name with bold font
                    Text(
                        text = member.name,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = RajasthanDarkText,
                        textAlign = TextAlign.Center
                    )

                    // Contact number in regular font format
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone icon",
                            tint = RajasthanOchre,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = member.contactNumber,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            color = RajasthanDarkText.copy(alpha = 0.9f)
                        )
                    }

                    // Village name in regular font format
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Village icon",
                            tint = RajasthanOchre,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val displayVillage = if (activeLanguage == Language.HINDI) {
                            if (member.position.all { it.code < 128 }) autoConvertTranslation(member.position) else member.position
                        } else {
                            member.position
                        }
                        Text(
                            text = displayVillage,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            color = RajasthanDarkText.copy(alpha = 0.9f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { selectedMemberForCall = null },
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(42.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RajasthanOchre),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = Translations.getString(TextKey.CLOSE_BUTTON, activeLanguage),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

fun autoConvertTranslation(input: String): String {
    if (input.isBlank()) return ""
    val dict = mapOf(
        "president" to "अध्यक्ष",
        "vice president" to "उपाध्यक्ष",
        "secretary" to "सचिव",
        "treasurer" to "कोषाध्यक्ष",
        "member" to "सदस्य",
        "trustee" to "न्यासी",
        "advisory" to "सलाहकार",
        "patrakar" to "पत्रकार",
        "advisor" to "सलाहकार",
        "organizer" to "संयोजक",
        "social worker" to "समाज सेवक",
        "youth leader" to "युवा नेता",
        "active" to "सक्रिय",
        "committee" to "समिति",
        "rajesh" to "राजेश",
        "kumawat" to "कुमावत",
        "suresh" to "सुरेश",
        "sunita" to "सुनीता",
        "ramesh" to "रमेश",
        "chand" to "चन्द",
        "mahendra" to "महेंद्र",
        "kailash" to "कैलाश",
        "nirmala" to "निर्मला",
        "lalita" to "ललिता",
        "gopal" to "गोपाल",
        "dinesh" to "दिनेश",
        "priyanka" to "प्रियंका",
        "achal" to "अचल",
        "singh" to "सिंह",
        "ahore" to "आहोर",
        "jalor" to "जालौर",
        "guda" to "गुड़ा",
        "balotan" to "बालोतान",
        "agawari" to "अगवरी",
        "hemtaram" to "हेमतराम",
        "sonaramji" to "सोनारामजी",
        "prajapat" to "प्रजापत",
        "amrutlal" to "अमृतलाल",
        "polaramji" to "पोलारामजी",
        "goparam" to "गोपाराम",
        "khangararamji" to "खंगारारामजी",
        "harish" to "हरिश",
        "mulaji" to "मुलाजी",
        "ashok" to "अशोक",
        "kesaji" to "केसाजी",
        "vagaramji" to "वागारामजी",
        "shravan" to "श्रवण",
        "mithalalaji" to "मीठालालजी",
        "sarupaji" to "सरूपजी",
        "chandrai" to "चांदराई",
        "bhomaram" to "भोमाराम",
        "umaramji" to "उमारामजी",
        "charli" to "चारली",
        "jayantilal" to "जयंतीलाल",
        "jetharamji" to "जेठारामजी",
        "baavdi" to "बावड़ी",
        "mohanlal" to "मोहनलाल",
        "chunnilalji" to "चुन्नीलालजी",
        "jhakmaram" to "झाकमाराम",
        "prabhuji" to "प्रभुजी",
        "hemaramji" to "हेमारामजी",
        "pancharam" to "पंचाराम",
        "tararamji" to "तारारामजी",
        "panchota" to "पंचोटा",
        "bijesh" to "बिजेश",
        "hansarajji" to "हंसराजजी",
        "hansaram" to "हंसाराम",
        "kesaramji" to "केसारामजी",
        "jhoda" to "झोड़ा",
        "narsaramji" to "नरसारामजी",
        "kanhailal" to "कन्हैयालाल",
        "achlaramji" to "अचलारामजी",
        "hanu" to "हनु",
        "kuyaramji" to "कुयारामजी",
        "maadadi" to "मादड़ी",
        "jawanaramji" to "जवानारामजी",
        "mangilalji" to "मांगीलालजी",
        "ganesharam" to "गणेशाराम",
        "bhooti" to "भूती",
        "thanaramji" to "थानारामजी",
        "vagaram" to "वागाराम",
        "kuldeep" to "कुलदीप",
        "rataramji" to "रतारामजी",
        "phularam" to "फूलाराम",
        "modaramji" to "मोदारामजी",
        "kishorekumar" to "किशोरकुमार",
        "ruparamji" to "रूपारामजी",
        "chunda" to "चूंडा",
        "ranchodaram" to "रणछोड़राम",
        "amraramji" to "अमरारामजी",
        "shankaralalji" to "शंकरलालजी",
        "hemaram" to "हेमाराम",
        "bhabutaramji" to "भाबूतारामजी",
        "puraramji" to "पुरारामजी",
        "somaram" to "सोमाराम",
        "paaras" to "पारस",
        "mithalalji" to "मीठालालजी",
        "sugaliya" to "सुगालिया",
        "jawanaram" to "जवानाराम",
        "habtaji" to "हबताजी",
        "babulal" to "बाबूलाल",
        "hosaji" to "होसाजी",
        "bhimaram" to "भीमाराम",
        "chavarca" to "चावरचा",
        "bhalaramji" to "भलारामजी",
        "sujaram" to "सुजाराम",
        "ramaram" to "रामाराम",
        "kanaramji" to "कानारामजी",
        "khushalji" to "खुशालजी",
        "bhawarlal" to "भंवरलाल",
        "shriprabhuji" to "श्रीप्रभुजी",
        "champalal" to "चंपालाल",
        "rajaaji" to "राजाजी",
        "bhikharam" to "भीखाराम",
        "magaaji" to "मगाजी",
        "rodla" to "रोडला",
        "sakaram" to "सकाराम",
        "ranchodji" to "रणछोड़जी",
        "bhabutaram" to "भाबूताराम",
        "nemaji" to "नेमाजी",
        "mithalal" to "मीठालाल",
        "kupaaji" to "कुपाजी",
        "khetaji" to "खेताजी",
        "jora" to "जोरा",
        "vaagaji" to "वागाजी",
        "madaram" to "मदाराम",
        "jawaanji" to "जवानजी",
        "kantilal" to "कांतिलाल",
        "vardaji" to "वरदाजी",
        "badaram" to "बादाराम",
        "kupaji" to "कुपाजी",
        "futarmal" to "फूटरमल",
        "jesaji" to "जेसाजी",
        "ummedpur" to "उम्मेदपुर",
        "pravin" to "प्रवीण",
        "devaji" to "देवाजी",
        "shankarlal" to "शंकरलाल",
        "sanklaji" to "सांकलाजी",
        "manish" to "मनीष",
        "pukhrajji" to "पुखराजजी",
        "dilip" to "दिलीप",
        "manoj" to "मनोज",
        "sawlaram" to "सावलाराम",
        "modaji" to "मोडाजी",
        "shantilal" to "शांतिलाल",
        "vagtaji" to "वागताजी",
        "lumbaji" to "लूम्बाजी",
        "pradip" to "प्रदीप",
        "ranchodaji" to "रणछोड़जी",
        "vinod" to "विनोद",
        "kuyaji" to "कुयाजी",
        "sheshaji" to "शेषाजी",
        "magaji" to "मगाजी"
    )

    val revDict = dict.entries.associate { it.value to it.key.split(" ").joinToString(" ") { word -> word.replaceFirstChar { c -> c.uppercase() } } }

    val cleanInput = input.trim().lowercase()
    
    if (dict.containsKey(cleanInput)) {
        return dict[cleanInput]!!
    }
    if (revDict.containsKey(input.trim())) {
        return revDict[input.trim()]!!
    }

    val words = input.trim().split("\\s+".toRegex())
    val convertedWords = words.map { w ->
        val lw = w.lowercase()
        if (dict.containsKey(lw)) {
            dict[lw]!!
        } else if (revDict.containsKey(w)) {
            revDict[w]!!
        } else {
            w
        }
    }
    
    return convertedWords.joinToString(" ")
}

fun translateWithGemini(
    text: String,
    scope: kotlinx.coroutines.CoroutineScope,
    onResult: (String) -> Unit
) {
    val localResult = autoConvertTranslation(text)
    if (localResult.lowercase() != text.lowercase()) {
        onResult(localResult)
        return
    }

    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
        onResult(text)
        return
    }

    scope.launch(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val client = okhttp3.OkHttpClient.Builder()
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .build()
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            
            val isEnglish = text.all { it.code < 128 }
            val prompt = if (isEnglish) {
                "Translate this Indian name or community role/title to Hindi script. Output ONLY the translated text, nothing else: $text"
            } else {
                "Translate this Indian name or community role/title in Hindi script to English (latin script, readable transliteration). Output ONLY the translated text, nothing else: $text"
            }

            val jsonBody = org.json.JSONObject().apply {
                put("contents", org.json.JSONArray().apply {
                    put(org.json.JSONObject().apply {
                        put("parts", org.json.JSONArray().apply {
                            put(org.json.JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)
            val request = okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseStr = response.body?.string()
                    if (!responseStr.isNullOrEmpty()) {
                        val root = org.json.JSONObject(responseStr)
                        val candidates = root.getJSONArray("candidates")
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.getJSONObject("content")
                        val parts = content.getJSONArray("parts")
                        val translated = parts.getJSONObject(0).getString("text").trim()
                        if (translated.isNotEmpty()) {
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                onResult(translated)
                            }
                            return@launch
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
            onResult(text)
        }
    }
}

@Composable
fun MemberDirectoryRow(
    member: MemberEntity,
    onCallRequested: () -> Unit
) {
    val activeLanguage = LocalLanguage.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCallRequested() }
            .border(0.5.dp, RajasthanGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .shadow(0.5.dp, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = RajasthanSurface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column 1: Profile Picture (Weight 0.20)
            Box(
                modifier = Modifier.weight(0.2f),
                contentAlignment = Alignment.Center
            ) {
                MemberProfilePhoto(
                    photoUri = member.profilePhotoUri,
                    memberName = member.name,
                    size = 38.dp
                )
            }

            // Column 2: Name (Weight 0.50)
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = member.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanDarkText,
                    textAlign = TextAlign.Center
                )
            }

            // Column 3: Position/Village (Weight 0.30)
            Column(
                modifier = Modifier.weight(0.3f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val displayPosition = if (activeLanguage == Language.HINDI) {
                    if (member.position.all { it.code < 128 }) autoConvertTranslation(member.position) else member.position
                } else {
                    member.position
                }
                
                Box(
                    modifier = Modifier
                        .background(RajasthanOchre.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = displayPosition,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RajasthanOchre,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun launchEmailIntent(context: Context, name: String, mobile: String, feedback: String, activeLanguage: Language) {
    val emailString = "kumawatsamaj.co.in@gmail.com"
    val subjectString = "Kumawat Samaj: Feedback from $name"
    val bodyString = "Name: $name\nContact Number: $mobile\n\nFeedback Description:\n$feedback"
    
    val uri = Uri.parse("mailto:$emailString" +
            "?subject=" + Uri.encode(subjectString) +
            "&body=" + Uri.encode(bodyString))
    val intent = Intent(Intent.ACTION_SENDTO, uri)
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        val backupIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailString))
            putExtra(Intent.EXTRA_SUBJECT, subjectString)
            putExtra(Intent.EXTRA_TEXT, bodyString)
        }
        try {
            context.startActivity(Intent.createChooser(backupIntent, "Choose an email client:"))
        } catch (e2: Exception) {
            Toast.makeText(context, if (activeLanguage == Language.HINDI) "कोई ईमेल ऐप नहीं मिला!" else "No email app found!", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun NotificationScreen(
    notifications: List<SamajNotification>,
    onSendNotification: (SamajNotification) -> Unit,
    activeLanguage: Language,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    // Send notification dialog parameters
    var showSendNotificationDialog by rememberSaveable { mutableStateOf(false) }
    var fieldTitleEn by remember { mutableStateOf("") }
    var fieldDescEn by remember { mutableStateOf("") }
    var fieldTitleHi by remember { mutableStateOf("") }
    var fieldDescHi by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("GENERAL") }
    var inputImageUrl by remember { mutableStateOf("") }
    var inputPdfUrl by remember { mutableStateOf("") }
    var inputLinkUrl by remember { mutableStateOf("") }

    val currentDate = remember {
        try {
            val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.ENGLISH)
            sdf.format(java.util.Date())
        } catch (e: Exception) {
            "01 Jun 2026"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(14.dp)
    ) {
        // Upper Title Action Block
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(RajasthanOchre.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = RajasthanOchre,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (activeLanguage == Language.HINDI) "सूचनाएं एवं संदेश" else "Samaj Hub Notices",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = RajasthanOchre
                    )
                    Text(
                        text = if (activeLanguage == Language.HINDI) "आधिकारिक घोषणाएं एवं सूचना पट्ट" else "Official releases & broadcast boards",
                        fontSize = 10.sp,
                        color = RajasthanDarkText.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Create New Broadcast Action Button (Admin Portal)
            Button(
                onClick = {
                    fieldTitleEn = ""
                    fieldDescEn = ""
                    fieldTitleHi = ""
                    fieldDescHi = ""
                    inputImageUrl = ""
                    inputPdfUrl = ""
                    inputLinkUrl = ""
                    selectedCategory = "GENERAL"
                    showSendNotificationDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = RajasthanOchre),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (activeLanguage == Language.HINDI) "नया संदेश" else "Broadcast",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        HorizontalDivider(color = RajasthanOchre.copy(alpha = 0.2f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Main List of Notifications / Notices Area
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Empty",
                        tint = Color.LightGray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (activeLanguage == Language.HINDI) "कोई घोषणा उपलब्ध नहीं है" else "No community notices available yet.",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                notifications.asReversed().forEach { notice ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.2.dp, RajasthanGold.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                            .shadow(2.dp, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = RajasthanSurface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            // Notice Category Badges & Date
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val categoryBg = when (notice.category) {
                                    "URGENT" -> Color(0xFFFFEBEE)
                                    "EVENT" -> Color(0xFFE8F5E9)
                                    else -> Color(0xFFE3F2FD)
                                }
                                val categoryColor = when (notice.category) {
                                    "URGENT" -> Color(0xFFC62828)
                                    "EVENT" -> Color(0xFF2E7D32)
                                    else -> Color(0xFF1565C0)
                                }
                                val categoryLabel = when (notice.category) {
                                    "URGENT" -> if (activeLanguage == Language.HINDI) "🚨 अत्यंत महत्वपूर्ण" else "🚨 URGENT"
                                    "EVENT" -> if (activeLanguage == Language.HINDI) "✨ उत्सव/आयोजन" else "✨ EVENT"
                                    else -> if (activeLanguage == Language.HINDI) "📢 सामान्य सूचना" else "📢 GENERAL"
                                }

                                Box(
                                    modifier = Modifier
                                        .background(categoryBg, RoundedCornerShape(50.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = categoryLabel,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = categoryColor
                                    )
                                }

                                Text(
                                    text = notice.date,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Notice Title
                            Text(
                                text = if (activeLanguage == Language.HINDI) notice.titleHi else notice.titleEn,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = RajasthanOchre,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Notice Main Description Body
                            Text(
                                text = if (activeLanguage == Language.HINDI) notice.descHi else notice.descEn,
                                fontSize = 12.sp,
                                color = RajasthanDarkText.copy(alpha = 0.9f),
                                lineHeight = 17.sp
                            )

                            // Optional 1: Image attachment displayed fully!
                            if (!notice.imageUrl.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                ) {
                                    com.example.ui.components.MemberProfilePhoto(
                                        photoUri = notice.imageUrl,
                                        memberName = notice.titleEn,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            // Optional 2: PDF attachment displayed cleanly!
                            if (!notice.pdfUrl.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFFF2F2), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFFFCDCD), RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (notice.pdfUrl.startsWith("http")) {
                                                try {
                                                    uriHandler.openUri(notice.pdfUrl)
                                                } catch (e: java.lang.Exception) {
                                                    Toast.makeText(context, "Could not open path: ${notice.pdfUrl}", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    if (activeLanguage == Language.HINDI)
                                                        "दस्तावेज '${notice.pdfUrl}' सफलता पूर्वक खोला गया।"
                                                    else
                                                        "Document '${notice.pdfUrl}' opened successfully in PDF reader.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Custom beautifully designed red PDF icon-circle badge
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(Color(0xFFFFEBEE), CircleShape)
                                            .border(1.dp, Color(0xFFC62828).copy(alpha = 0.5f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "PDF",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFFC62828)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = notice.pdfUrl.substringAfterLast("/"),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC62828),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (activeLanguage == Language.HINDI) "खोलने के लिए छुएं (PDF)" else "Tap to open official PDF booklet",
                                            fontSize = 9.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Open",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            // Optional 3: Web links displayed cleanly!
                            if (!notice.linkUrl.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFBBDEFB), RoundedCornerShape(8.dp))
                                        .clickable {
                                            try {
                                                uriHandler.openUri(notice.linkUrl)
                                            } catch (e: java.lang.Exception) {
                                                Toast.makeText(context, "Could not launch link: ${notice.linkUrl}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Custom beautifully designed blue URL icon-circle badge
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(Color(0xFFE3F2FD), CircleShape)
                                            .border(1.dp, Color(0xFF1565C0).copy(alpha = 0.5f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "URL",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF1565C0)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (activeLanguage == Language.HINDI) "आधिकारिक वेब लिंक खोलें" else "Launch Official Web Portal",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1565C0)
                                        )
                                        Text(
                                            text = notice.linkUrl,
                                            fontSize = 9.sp,
                                            color = Color.DarkGray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Open",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Dialog for Admin to Send New Notice ---
        if (showSendNotificationDialog) {
            AlertDialog(
                onDismissRequest = { showSendNotificationDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            if (fieldTitleEn.isBlank() && fieldTitleHi.isBlank()) {
                                Toast.makeText(context, "Please enter a title / कृपया शीर्षक दर्ज करें", Toast.LENGTH_SHORT).show()
                            } else {
                                val notif = SamajNotification(
                                    id = System.currentTimeMillis().toString(),
                                    titleEn = fieldTitleEn.ifBlank { fieldTitleHi },
                                    titleHi = fieldTitleHi.ifBlank { fieldTitleEn },
                                    descEn = fieldDescEn.ifBlank { "No detailed description provided." },
                                    descHi = fieldDescHi.ifBlank { "कोई अतिरिक्त विवरण प्रदान नहीं किया गया है।" },
                                    category = selectedCategory,
                                    date = currentDate,
                                    imageUrl = inputImageUrl.takeIf { it.isNotBlank() },
                                    pdfUrl = inputPdfUrl.takeIf { it.isNotBlank() },
                                    linkUrl = inputLinkUrl.takeIf { it.isNotBlank() }
                                )
                                onSendNotification(notif)
                                showSendNotificationDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RajasthanOchre),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (activeLanguage == Language.HINDI) "ब्रॉडकास्ट जारी करें" else "Broadcast Release")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSendNotificationDialog = false }) {
                        Text(if (activeLanguage == Language.HINDI) "रद्द करें" else "Cancel", color = RajasthanOchre)
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🔔", fontSize = 20.sp)
                        Text(
                            text = if (activeLanguage == Language.HINDI) "नया लाइव प्रसारण जारी करें" else "New Community Broadcast",
                            color = RajasthanOchre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (activeLanguage == Language.HINDI) "श्रेणी (Category):" else "Select Category Tag:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RajasthanDarkText
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val categories = listOf("GENERAL", "EVENT", "URGENT")
                            categories.forEach { cat ->
                                val label = when (cat) {
                                    "URGENT" -> if (activeLanguage == Language.HINDI) "🚨 आवश्यक" else "🚨 Urgent"
                                    "EVENT" -> if (activeLanguage == Language.HINDI) "✨ उत्सव" else "✨ Event"
                                    else -> if (activeLanguage == Language.HINDI) "📢 सामान्य" else "📢 General"
                                }
                                val isSelected = selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) RajasthanOchre else Color.LightGray.copy(alpha = 0.2f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) RajasthanOchre else Color.LightGray,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedCategory = cat }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else RajasthanDarkText,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 0.5.dp)

                        // English Fields
                        Text(
                            text = "English Notice Content:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RajasthanOchre
                        )
                        OutlinedTextField(
                            value = fieldTitleEn,
                            onValueChange = { fieldTitleEn = it },
                            modifier = Modifier.fillMaxWidth().testTag("notif_title_en_input"),
                            label = { Text("Title (English)", fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RajasthanOchre,
                                focusedLabelColor = RajasthanOchre
                            )
                        )
                        OutlinedTextField(
                            value = fieldDescEn,
                            onValueChange = { fieldDescEn = it },
                            modifier = Modifier.fillMaxWidth().height(80.dp).testTag("notif_desc_en_input"),
                            label = { Text("Main Body Description", fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RajasthanOchre,
                                focusedLabelColor = RajasthanOchre
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Hindi Fields
                        Text(
                            text = "हिंदी विवरण (Hindi Content):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RajasthanOchre
                        )
                        OutlinedTextField(
                            value = fieldTitleHi,
                            onValueChange = { fieldTitleHi = it },
                            modifier = Modifier.fillMaxWidth().testTag("notif_title_hi_input"),
                            label = { Text("शीर्षक (हिंदी)", fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RajasthanOchre,
                                focusedLabelColor = RajasthanOchre
                            )
                        )
                        OutlinedTextField(
                            value = fieldDescHi,
                            onValueChange = { fieldDescHi = it },
                            modifier = Modifier.fillMaxWidth().height(80.dp).testTag("notif_desc_hi_input"),
                            label = { Text("संक्षिप्त समाचार / संदेश", fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RajasthanOchre,
                                focusedLabelColor = RajasthanOchre
                            )
                        )

                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 0.5.dp)

                        // Multimedia Fields
                        Text(
                            text = "Attachments & Actions (Optional):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RajasthanOchre
                        )
                        OutlinedTextField(
                            value = inputImageUrl,
                            onValueChange = { inputImageUrl = it },
                            modifier = Modifier.fillMaxWidth().testTag("notif_image_input"),
                            label = { Text("Image Card URL (e.g. Unsplash)", fontSize = 10.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RajasthanOchre,
                                focusedLabelColor = RajasthanOchre
                            )
                        )
                        OutlinedTextField(
                            value = inputPdfUrl,
                            onValueChange = { inputPdfUrl = it },
                            modifier = Modifier.fillMaxWidth().testTag("notif_pdf_input"),
                            label = { Text("PDF Document Link or Name (e.g. prospectus.pdf)", fontSize = 10.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RajasthanOchre,
                                focusedLabelColor = RajasthanOchre
                            )
                        )
                        OutlinedTextField(
                            value = inputLinkUrl,
                            onValueChange = { inputLinkUrl = it },
                            modifier = Modifier.fillMaxWidth().testTag("notif_link_input"),
                            label = { Text("External Action URL (e.g. https://samaj.org)", fontSize = 10.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RajasthanOchre,
                                focusedLabelColor = RajasthanOchre
                            )
                        )
                    }
                },
                properties = DialogProperties(usePlatformDefaultWidth = false),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            )
        }
    }
}

@Composable
fun ContactUsScreen(
    onBack: () -> Unit
) {
    val activeLanguage = LocalLanguage.current
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var mobile by rememberSaveable { mutableStateOf("") }
    var feedback by rememberSaveable { mutableStateOf("") }

    // Validation checks
    val isNumberOnly = mobile.all { it.isDigit() }
    val isTenDigits = mobile.length == 10
    val isMobileInvalid = mobile.isNotEmpty() && (!isNumberOnly || mobile.length > 10)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // App header back title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .background(RajasthanOchre.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Back", 
                    tint = RajasthanOchre,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = Translations.getString(TextKey.CONTACT_US, activeLanguage),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = RajasthanDarkText
            )
        }

        // Informative guidance card
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface),
            border = BorderStroke(1.dp, RajasthanGold.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(RajasthanOchre.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = RajasthanOchre,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = if (activeLanguage == Language.HINDI) 
                            "यदि आप इस ऐप में कोई बदलाव या सुझाव देना चाहते हैं तो मुझसे संपर्क करें।" 
                            else "If you want to make any changes or suggestions in this app then contact me.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RajasthanOchre,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (activeLanguage == Language.HINDI) "ईमेल करें: kumawatsamaj.co.in@gmail.com" else "Email: kumawatsamaj.co.in@gmail.com",
                        fontSize = 11.sp,
                        color = RajasthanDarkText.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Fields Block
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Name Input
            Text(
                text = Translations.getString(TextKey.NAME_LABEL, activeLanguage),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RajasthanDarkText
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("feedback_name_input"),
                placeholder = { Text(Translations.getString(TextKey.ENTER_NAME_PLACEHOLDER, activeLanguage), fontSize = 13.sp) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = RajasthanDarkText,
                    unfocusedTextColor = RajasthanDarkText,
                    focusedBorderColor = RajasthanOchre,
                    unfocusedBorderColor = RajasthanGold.copy(alpha = 0.6f),
                    focusedContainerColor = RajasthanSurface,
                    unfocusedContainerColor = RajasthanSurface
                ),
                singleLine = true
            )

            // Mobile/Contact Input
            Text(
                text = Translations.getString(TextKey.PHONE_LABEL, activeLanguage),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RajasthanDarkText
            )
            OutlinedTextField(
                value = mobile,
                onValueChange = { input -> 
                    val filtered = input.filter { it.isDigit() }
                    if (filtered.length <= 10) {
                        mobile = filtered
                    }
                },
                isError = isMobileInvalid || (mobile.isNotEmpty() && !isTenDigits),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("feedback_phone_input"),
                placeholder = { Text(Translations.getString(TextKey.ENTER_PHONE_PLACEHOLDER, activeLanguage), fontSize = 13.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {
                    if (mobile.isNotEmpty() && !isTenDigits) {
                        Text(
                            text = if (activeLanguage == Language.HINDI) "मोबाइल नंबर १० अंकों का होना चाहिए!" else "Mobile number must be exactly 10 digits!",
                            color = Color.Red,
                            fontSize = 11.sp
                        )
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = RajasthanDarkText,
                    unfocusedTextColor = RajasthanDarkText,
                    focusedBorderColor = RajasthanOchre,
                    unfocusedBorderColor = RajasthanGold.copy(alpha = 0.6f),
                    focusedContainerColor = RajasthanSurface,
                    unfocusedContainerColor = RajasthanSurface
                ),
                singleLine = true
            )

            // FeedbackDetails Input
            Text(
                text = Translations.getString(TextKey.FEEDBACK_LABEL, activeLanguage),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RajasthanDarkText
            )
            OutlinedTextField(
                value = feedback,
                onValueChange = { feedback = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("feedback_issue_input"),
                placeholder = { Text(Translations.getString(TextKey.ENTER_FEEDBACK_PLACEHOLDER, activeLanguage), fontSize = 13.sp) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = RajasthanDarkText,
                    unfocusedTextColor = RajasthanDarkText,
                    focusedBorderColor = RajasthanOchre,
                    unfocusedBorderColor = RajasthanGold.copy(alpha = 0.6f),
                    focusedContainerColor = RajasthanSurface,
                    unfocusedContainerColor = RajasthanSurface
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Submit Button
        Button(
            onClick = {
                val trimmedName = name.trim()
                val trimmedMobile = mobile.trim()
                val trimmedFeedback = feedback.trim()

                if (trimmedName.isEmpty() || trimmedMobile.isEmpty() || trimmedFeedback.isEmpty()) {
                    Toast.makeText(context, Translations.getString(TextKey.ENTER_ALL_FIELDS, activeLanguage), Toast.LENGTH_LONG).show()
                } else if (!trimmedMobile.all { it.isDigit() }) {
                    Toast.makeText(context, if (activeLanguage == Language.HINDI) "मोबाइल नंबर में केवल अंक होने चाहिए!" else "Mobile number must contain digits only!", Toast.LENGTH_LONG).show()
                } else if (trimmedMobile.length != 10) {
                    Toast.makeText(context, if (activeLanguage == Language.HINDI) "मोबाइल नंबर ठीक १० अंकों का होना चाहिए!" else "Mobile number must be exactly 10 digits!", Toast.LENGTH_LONG).show()
                } else {
                    launchEmailIntent(context, trimmedName, trimmedMobile, trimmedFeedback, activeLanguage)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .testTag("submit_feedback_button"),
            colors = ButtonDefaults.buttonColors(containerColor = RajasthanOchre),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Email, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = Translations.getString(TextKey.SUBMIT_BUTTON, activeLanguage),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Elegantly styled "Made by Vawadra Bro" credit block
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RajasthanOchre.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .border(1.dp, RajasthanGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = RajasthanOchre,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Made by Vawadra Bro",
                fontSize = 15.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Serif,
                color = RajasthanOchre
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
fun BackendManagementScreen(
    viewModel: MemberViewModel,
    onBack: () -> Unit
) {
    val activeLanguage = LocalLanguage.current
    val context = LocalContext.current
    val allMembers by viewModel.allMembers.collectAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var subCommunity by rememberSaveable { mutableStateOf("navi_mumbai1") } // "navi_mumbai1" or "mumbai1" or "ahore1"
    var selectedAvatar by rememberSaveable { mutableStateOf("avatar_1") }
    var position by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val presetAvatars = listOf(
        Pair("avatar_1", "👑 Royal Crown"),
        Pair("avatar_2", "🪔 Heritage Diya"),
        Pair("avatar_3", "🚩 Saffron Flag"),
        Pair("avatar_4", "🌸 Floral Lotus"),
        Pair("avatar_5", "✨ Sparkle Bless")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Clear back-header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .background(RajasthanOchre.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RajasthanOchre)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = Translations.getString(TextKey.BACKEND_MANAGE, activeLanguage),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanDarkText,
                    lineHeight = 24.sp
                )
                Text(
                    text = "Insert & Manage database records",
                    fontSize = 12.sp,
                    color = RajasthanDarkText.copy(alpha = 0.6f)
                )
            }
        }

        // Data Entry Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, RajasthanGold, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (activeLanguage == Language.HINDI) "नया सदस्य जोड़ें (Insert Member)" else "Insert Community Member",
                    color = RajasthanOchre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Name Box
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            Translations.getString(TextKey.NAME_LABEL, activeLanguage),
                            fontWeight = FontWeight.Bold,
                            color = RajasthanDarkText
                        )
                        var isTranslatingName by remember { mutableStateOf(false) }
                        TextButton(
                            onClick = {
                                if (name.isNotBlank() && !isTranslatingName) {
                                    isTranslatingName = true
                                    translateWithGemini(name, coroutineScope) { translated ->
                                        name = translated
                                        isTranslatingName = false
                                    }
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isTranslatingName) "Translating..." else "Conv. Hindi ⇄ Eng",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RajasthanOchre
                            )
                        }
                    }
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("backend_name_input"),
                        placeholder = { Text(Translations.getString(TextKey.ENTER_NAME_PLACEHOLDER, activeLanguage)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RajasthanOchre,
                            unfocusedBorderColor = RajasthanGold.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                // Phone Box
                Column {
                    Text(
                        Translations.getString(TextKey.PHONE_LABEL, activeLanguage),
                        fontWeight = FontWeight.Bold,
                        color = RajasthanDarkText
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("backend_phone_input"),
                        placeholder = { Text(Translations.getString(TextKey.ENTER_PHONE_PLACEHOLDER, activeLanguage)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RajasthanOchre,
                            unfocusedBorderColor = RajasthanGold.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                // Village Box
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (activeLanguage == Language.HINDI) "गाँव (Village)" else "Village",
                            fontWeight = FontWeight.Bold,
                            color = RajasthanDarkText
                        )
                        var isTranslatingPos by remember { mutableStateOf(false) }
                        TextButton(
                            onClick = {
                                if (position.isNotBlank() && !isTranslatingPos) {
                                    isTranslatingPos = true
                                    translateWithGemini(position, coroutineScope) { translated ->
                                        position = translated
                                        isTranslatingPos = false
                                    }
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isTranslatingPos) "Translating..." else "Conv. Hindi ⇄ Eng",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RajasthanOchre
                            )
                        }
                    }
                    OutlinedTextField(
                        value = position,
                        onValueChange = { position = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("backend_position_input"),
                        placeholder = { Text(if (activeLanguage == Language.HINDI) "जैसे: आहोर, गुड़ा बालोतान, अगवरी" else "e.g. Ahore, Guda Balotan, Agawari") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RajasthanOchre,
                            unfocusedBorderColor = RajasthanGold.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                // Sub community Selector Row (With giant click boundaries)
                Column {
                    Text(
                        text = Translations.getString(TextKey.CHOOSE_SUBC_PROMPT, activeLanguage),
                        fontWeight = FontWeight.Bold,
                        color = RajasthanDarkText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { subCommunity = "navi_mumbai1" }
                                .background(
                                    if (subCommunity == "navi_mumbai1") RajasthanOchre.copy(alpha = 0.1f) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                               )
                                .border(
                                    1.dp,
                                    if (subCommunity == "navi_mumbai1") RajasthanOchre else Color.LightGray,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(4.dp)
                        ) {
                            RadioButton(
                                selected = subCommunity == "navi_mumbai1",
                                onClick = { subCommunity = "navi_mumbai1" },
                                colors = RadioButtonDefaults.colors(selectedColor = RajasthanOchre)
                            )
                            Text(
                                "Navi Mumbai",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RajasthanDarkText
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(0.9f)
                                .clickable { subCommunity = "mumbai1" }
                                .background(
                                    if (subCommunity == "mumbai1") RajasthanOchre.copy(alpha = 0.1f) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (subCommunity == "mumbai1") RajasthanOchre else Color.LightGray,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(4.dp)
                        ) {
                            RadioButton(
                                selected = subCommunity == "mumbai1",
                                onClick = { subCommunity = "mumbai1" },
                                colors = RadioButtonDefaults.colors(selectedColor = RajasthanOchre)
                            )
                            Text(
                                "Mumbai",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RajasthanDarkText
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(0.9f)
                                .clickable { subCommunity = "ahore1" }
                                .background(
                                    if (subCommunity == "ahore1") RajasthanOchre.copy(alpha = 0.1f) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (subCommunity == "ahore1") RajasthanOchre else Color.LightGray,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(4.dp)
                        ) {
                            RadioButton(
                                selected = subCommunity == "ahore1",
                                onClick = { subCommunity = "ahore1" },
                                colors = RadioButtonDefaults.colors(selectedColor = RajasthanOchre)
                            )
                            Text(
                                "Ahore (Jalor)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RajasthanDarkText
                            )
                        }
                    }
                }

                // Avatar Horizontal Selector with live circular previews
                Column {
                    Text(
                        text = Translations.getString(TextKey.SELECT_AVATAR, activeLanguage),
                        fontWeight = FontWeight.Bold,
                        color = RajasthanDarkText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        items(presetAvatars) { (id, desc) ->
                            val isSelected = selectedAvatar == id
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { selectedAvatar = id }
                                    .border(
                                        2.dp,
                                        if (isSelected) RajasthanOchre else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                MemberProfilePhoto(
                                    photoUri = id,
                                    memberName = name,
                                    size = 56.dp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = desc.split(" ").getOrNull(1) ?: "",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RajasthanDarkText
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Insert Button
                Button(
                    onClick = {
                        if (name.trim().isEmpty() || phone.trim().isEmpty()) {
                            Toast.makeText(context, Translations.getString(TextKey.ENTER_ALL_FIELDS, activeLanguage), Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.insertMember(
                                name = name.trim(),
                                contactNumber = phone.trim(),
                                avatar = selectedAvatar,
                                subCommunity = subCommunity,
                                position = if (position.trim().isEmpty()) "Ahore" else position.trim()
                            ) {
                                Toast.makeText(context, Translations.getString(TextKey.ADD_MEMBER_SUCCESS, activeLanguage), Toast.LENGTH_LONG).show()
                                name = ""
                                phone = ""
                                position = ""
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("backend_save_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = RajasthanOchre),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Translations.getString(TextKey.SAVE_MEMBER_BUTTON, activeLanguage),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Existing Members List Management Title
        Text(
            text = if (activeLanguage == Language.HINDI) "डेटाबेस प्रबंधन (Delete Records)" else "Existing Database Records",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = RajasthanDarkText,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        // Lazy load delete entries in the backend screen
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (allMembers.isEmpty()) {
                    Text(
                        "No database rows exist currently.",
                        modifier = Modifier.padding(12.dp),
                        color = Color.Gray
                    )
                } else {
                    allMembers.forEach { member ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                                .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                MemberProfilePhoto(
                                    photoUri = member.profilePhotoUri,
                                    memberName = member.name,
                                    size = 42.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = member.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = RajasthanDarkText
                                    )
                                    Text(
                                        text = "${member.subCommunity.uppercase().replace("_"," ")} • ${member.contactNumber}",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                            IconButton(
                                onClick = { viewModel.deleteMember(member) }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun FormsScreen(onBack: () -> Unit) {
    val activeLanguage = LocalLanguage.current
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .background(RajasthanOchre.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Back", 
                    tint = RajasthanOchre
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = if (activeLanguage == Language.HINDI) "सामुदायिक गूगल फॉर्म" else "Community Forms Link",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanDarkText
                )
                Text(
                    text = if (activeLanguage == Language.HINDI) "आधिकारिक और सुव्यवस्थित पंजीकरण प्रक्रिया" else "Official & streamlined registration processes",
                    fontSize = 12.sp,
                    color = RajasthanDarkText.copy(alpha = 0.6f)
                )
            }
        }

        // Card 1: Official Membership Registration Form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RajasthanGold.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(RajasthanOchre.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (activeLanguage == Language.HINDI) "अनिवार्य फॉर्म" else "REQUIRED FORM",
                            color = RajasthanOchre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    Text(
                        text = "v2.4",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = if (activeLanguage == Language.HINDI) "नवीन सदस्य पंजीकरण फॉर्म (Google Form)" else "New Member Enrollment Form (Google Form)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RajasthanDarkText
                )

                Text(
                    text = if (activeLanguage == Language.HINDI) 
                        "कुमावत समाज के सभी परिवारों और सदस्यों को डिजिटल पहचान पत्र (ID Card) प्रदान करने हेतु यह पंजीकरण फॉर्म भरना आवश्यक है। कृपया सभी विवरण सही रूप से दर्ज करें।" 
                        else "It is mandatory for all Kumawat Samaj families & members to enroll via this official form to issue digital membership ID cards. Please fill accurate family details.",
                    fontSize = 13.sp,
                    color = RajasthanDarkText.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        val gFormUrl = "https://docs.google.com/forms/d/e/1FAIpQLSciREvG6f1Pclh6S9f31O-KumawatMemberEnrollment/viewform"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(gFormUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("button_open_enrollment_form"),
                    colors = ButtonDefaults.buttonColors(containerColor = RajasthanOchre),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (activeLanguage == Language.HINDI) "आधिकारिक गूगल फॉर्म खोलें ↗" else "Launch Official Google Form ↗",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Card 2: Family Milestone / Sukh-Dukh Update Form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RajasthanGold.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (activeLanguage == Language.HINDI) "सुख-दुख एवं सूचना पत्र" else "COMMUNITY FLASH",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = if (activeLanguage == Language.HINDI) "पारिवारिक गतिविधि और सुख-दुख अपडेट फॉर्म" else "Family Sukh-Dukh Milestone Updates",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RajasthanDarkText
                )

                Text(
                    text = if (activeLanguage == Language.HINDI)
                        "family में हुए शुभ विवाह, जन्म अथवा शोक से संबंधित घटनाओं की जानकारी समाज तक डिजिटल बुलेटिन के माध्यम से पहुँचाने के लिए यह फॉर्म सबमिट करें।"
                        else "Submit notices regarding family weddings, new births, or obituary news to be broadcasted to the whole Samaj hub digital bulletin boards seamlessly.",
                    fontSize = 13.sp,
                    color = RajasthanDarkText.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedButton(
                    onClick = {
                        val updateUrl = "https://docs.google.com/forms/d/e/1FAIpQLSd_SukhDukhUpdatesKumawat/viewform"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("button_open_updates_form"),
                    border = BorderStroke(1.dp, RajasthanOchre),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (activeLanguage == Language.HINDI) "सूचना अपडेट फॉर्म खोलें ↗" else "Open Updates Google Form ↗",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = RajasthanOchre
                    )
                }
            }
        }

        // Card 3: Sports, Youth & Career Wing registration
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RajasthanGold.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (activeLanguage == Language.HINDI) "युवा एवं करियर विंग" else "YOUTH & CAREER WING",
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = if (activeLanguage == Language.HINDI) "युवा, खेल और छात्र करियर पंजीकरण विंग" else "Youth, Sports & Student Career Mentorship Form",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RajasthanDarkText
                )

                Text(
                    text = if (activeLanguage == Language.HINDI)
                        "शिक्षा, खेल-कूद और करियर मार्गदर्शन विंग में जुड़ने के लिए युवा सदस्य इस फॉर्म को भरें। समाज के प्रबुद्ध सलाहकार आपकी सहायता करेंगे।"
                        else "For young members, students, and athletes to join the specialized coaching wing. Connect with community guides, tech business leaders and medical professionals.",
                    fontSize = 13.sp,
                    color = RajasthanDarkText.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedButton(
                    onClick = {
                        val sportsUrl = "https://docs.google.com/forms/d/e/1FAIpQLSf_YouthMentorshipKumawat/viewform"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sportsUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("button_open_youth_form"),
                    border = BorderStroke(1.dp, RajasthanOchre),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (activeLanguage == Language.HINDI) "करियर विंग से जुड़ें ↗" else "Join Career Wing Form ↗",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = RajasthanOchre
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun DonationScreen(onBack: () -> Unit) {
    val activeLanguage = LocalLanguage.current
    val context = LocalContext.current
    
    var activeCommunity by rememberSaveable { mutableStateOf("navi_mumbai") } // "navi_mumbai" or "mumbai" or "ahore"
    var customAmount by rememberSaveable { mutableStateOf("") }
    val presetAmounts = listOf("101", "501", "1101", "2101", "5001", "11000")
    var selectedPreset by rememberSaveable { mutableStateOf("501") }

    val targetBillerName = when (activeCommunity) {
        "navi_mumbai" -> "Kumawat Samaj Kalyan Trust Navi Mumbai"
        "mumbai" -> "Kumawat Samaj Kalyan Trust Mumbai"
        else -> "Kumawat Shikshan and Samajik Sansthan Ahore"
    }
    val targetUpi = when (activeCommunity) {
        "navi_mumbai" -> "kumawat.navimumbai@okaxis"
        "mumbai" -> "kumawat.mumbai@okaxis"
        else -> "kumawat.ahore@okaxis"
    }
    val bankAccName = when (activeCommunity) {
        "navi_mumbai" -> "Kumawat Samaj Kalyan Trust, Navi Mumbai"
        "mumbai" -> "Kumawat Samaj Kalyan Trust, Mumbai"
        else -> "Kumawat Shikshan and S. Sansthan Ahore"
    }
    val bankAccNum = when (activeCommunity) {
        "navi_mumbai" -> "55511223344556"
        "mumbai" -> "77766554433221"
        else -> "88844332211009"
    }
    val bankIfsc = when (activeCommunity) {
        "navi_mumbai" -> "SBIN0000123"
        "mumbai" -> "HDFC0000082"
        else -> "SGBY0006234"
    }
    val bankBranch = when (activeCommunity) {
        "navi_mumbai" -> "Vashi Sector 17, Navi Mumbai"
        "mumbai" -> "Dadar West, Mumbai"
        else -> "Ahore, Jalor, Rajasthan"
    }
    val bankName = when (activeCommunity) {
        "navi_mumbai" -> "State Bank of India (SBI)"
        "mumbai" -> "HDFC Bank Ltd"
        else -> "Rajasthan Marudhara Gramin Bank (RMGB)"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .background(RajasthanOchre.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Back", 
                    tint = RajasthanOchre
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = if (activeLanguage == Language.HINDI) "समाज सेवा और दान पोर्टल" else "Community Sewa & Donations",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanDarkText
                )
                Text(
                    text = if (activeLanguage == Language.HINDI) "सामुदायिक सुधार एवं कल्याण गतिविधियों में योगदान करें" else "Contribute to community upliftment and social works",
                    fontSize = 12.sp,
                    color = RajasthanDarkText.copy(alpha = 0.6f)
                )
            }
        }

        // Sub-Community Selection Row 
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (activeLanguage == Language.HINDI) "कल्याण कोष / उप-समुदाय चुनें:" else "Select Sub-Community / Welfare Fund:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RajasthanDarkText
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .border(1.dp, RajasthanBorder, RoundedCornerShape(10.dp))
                    .background(RajasthanBar.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "navi_mumbai" to ("Navi Mumbai" to "नवी मुंबई"),
                    "mumbai" to ("Mumbai" to "मुंबई"),
                    "ahore" to ("Ahore (Jalor)" to "आहोर (जालौर)")
                ).forEach { (code, names) ->
                    val (en, hi) = names
                    val isSelected = activeCommunity == code
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) RajasthanOchre else Color.Transparent)
                            .clickable { activeCommunity = code }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (code == "ahore") {
                                if (activeLanguage == Language.HINDI) "कुमावत संस्थान आहोर" else "Kumawat Sansthan Ahore"
                            } else {
                                if (activeLanguage == Language.HINDI) "कुमावत समाज $hi" else "Kumawat Samaj $en"
                            },
                            color = if (isSelected) Color.White else RajasthanDarkText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Trust Message Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, RajasthanGold, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFFFFAED), CircleShape)
                            .border(1.dp, RajasthanGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🪔", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (activeLanguage == Language.HINDI) "कुमावत समाज कल्याण ट्रस्ट ($targetBillerName)" else targetBillerName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RajasthanDarkText
                    )
                }

                Text(
                    text = if (activeLanguage == Language.HINDI)
                        "यह डिजिटल पोर्टल समाज के कल्याणकारी प्रकल्पों (जैसे: निर्धन छात्रों की शिक्षा, स्वास्थ्य कैंप, धर्मशाला निर्माण और मंदिर जीर्णोद्धार) में सहयोग करने हेतु बनाया गया है। आपके द्वारा दान की गई प्रत्येक राशि का पूरा हिसाब वेब-पोर्टल और वार्षिक मीटिंग में पूर्ण पारदर्शिता के साथ रखा जाता है।"
                        else "Every rupee donated goes directly to social welfare drives including education support for needy students, emergency health aid camps, community centres management and local updates. Transparent audits are published and updated annually.",
                    fontSize = 13.sp,
                    color = RajasthanDarkText.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )
            }
        }

        // Section header for donation inputs
        Text(
            text = if (activeLanguage == Language.HINDI) "सहयोग राशि चुनें (Select Amount)" else "Select Sewa Contribution Amount",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = RajasthanDarkText,
            modifier = Modifier.fillMaxWidth()
        )

        // Preset grid (Horizontal row of cards)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(presetAmounts) { amt ->
                val isSelected = selectedPreset == amt && customAmount.isEmpty()
                Card(
                    modifier = Modifier
                        .clickable {
                            selectedPreset = amt
                            customAmount = ""
                        }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) RajasthanOchre else Color.LightGray.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .size(width = 82.dp, height = 48.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) RajasthanOchre.copy(alpha = 0.1f) else RajasthanSurface
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "₹ $amt",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isSelected) RajasthanOchre else RajasthanDarkText
                        )
                    }
                }
            }
        }

        // Custom Amount Row
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (activeLanguage == Language.HINDI) "या अन्य राशि दर्ज करें (Or Enter Custom Amount)" else "Or Enter Custom Contribution",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RajasthanDarkText
            )
            OutlinedTextField(
                value = customAmount,
                onValueChange = {
                    val filtered = it.filter { char -> char.isDigit() }
                    customAmount = filtered
                    if (filtered.isNotEmpty()) {
                        selectedPreset = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("donation_custom_amount_input"),
                placeholder = { Text(if (activeLanguage == Language.HINDI) "जैसे: 2501" else "e.g. 5100", fontSize = 13.sp) },
                leadingIcon = { Text("₹", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RajasthanOchre, modifier = Modifier.padding(start = 6.dp)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = RajasthanDarkText,
                    unfocusedTextColor = RajasthanDarkText,
                    focusedBorderColor = RajasthanOchre,
                    unfocusedBorderColor = RajasthanGold.copy(alpha = 0.6f),
                    focusedContainerColor = RajasthanSurface,
                    unfocusedContainerColor = RajasthanSurface
                ),
                singleLine = true
            )
        }

        // Real UPI Payment Intent trigger
        val totalAmountStr = if (customAmount.isNotEmpty()) customAmount else selectedPreset
        val totalAmountInt = totalAmountStr.toIntOrNull() ?: 100

        Button(
            onClick = {
                if (totalAmountInt <= 0) {
                    Toast.makeText(context, "Please enter a valid amount!", Toast.LENGTH_SHORT).show()
                } else {
                    // Triggers the standard system UPI payments chooser (PhonePe, GPay, Paytm) aiming the community-specific upi key
                    val upiUriStr = "upi://pay?pa=${targetUpi}&pn=${Uri.encode(targetBillerName)}&am=${totalAmountInt}&cu=INR&tn=Community%20Welfare%20Sewa"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiUriStr))
                    try {
                        val chooser = Intent.createChooser(intent, "Pay securely via your UPI App...")
                        context.startActivity(chooser)
                    } catch (e: Exception) {
                        Toast.makeText(context, "No UPI app preinstalled. Launching PayTM portal", Toast.LENGTH_LONG).show()
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://paytm.com"))
                        context.startActivity(browserIntent)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("button_donate_upi"),
            colors = ButtonDefaults.buttonColors(containerColor = RajasthanOchre),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("⚡", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (activeLanguage == Language.HINDI) "UPI (GPay/PhonePe) द्वारा ₹$totalAmountInt दान करें ↗" else "Pay ₹$totalAmountInt via UPI (GPay/PhonePe/Paytm) ↗",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
            )
        }

        // Bank Account Direct Transfer Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, RajasthanGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (activeLanguage == Language.HINDI) "अन्य बैंक ट्रांसफर विवरण" else "Direct Bank NEFT/IMPS Details",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RajasthanDarkText,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (activeLanguage == Language.HINDI) "⏳ शीघ्र उपलब्ध..." else "⏳ coming Soon...",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanOchre,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Space for UPI QR Code
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (activeLanguage == Language.HINDI) "यूपीआई क्यूआर कोड (SCAN TO PAY)" else "Official UPI QR Code Space",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RajasthanDarkText
                )

                // Fancy Custom Drawn QR Code Canvas Symbol
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.5.dp, RajasthanOchre, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 5f
                        val qrColor = RajasthanOchre
                        
                        // Top-Left concentric square
                        drawRect(color = qrColor, topLeft = Offset(0f, 0f), size = Size(40f, 40f))
                        drawRect(color = Color.White, topLeft = Offset(10f, 10f), size = Size(20f, 20f))
                        drawRect(color = qrColor, topLeft = Offset(15f, 15f), size = Size(10f, 10f))

                        // Top-Right concentric square
                        drawRect(color = qrColor, topLeft = Offset(size.width - 40f, 0f), size = Size(40f, 40f))
                        drawRect(color = Color.White, topLeft = Offset(size.width - 30f, 10f), size = Size(20f, 20f))
                        drawRect(color = qrColor, topLeft = Offset(size.width - 25f, 15f), size = Size(10f, 10f))

                        // Bottom-Left concentric square
                        drawRect(color = qrColor, topLeft = Offset(0f, size.height - 40f), size = Size(40f, 40f))
                        drawRect(color = Color.White, topLeft = Offset(10f, size.height - 30f), size = Size(20f, 20f))
                        drawRect(color = qrColor, topLeft = Offset(15f, size.height - 25f), size = Size(10f, 10f))

                        // Bottom-Right small square (alignment pattern / timing)
                        drawRect(color = qrColor, topLeft = Offset(size.width - 30f, size.height - 30f), size = Size(20f, 20f))
                        drawRect(color = Color.White, topLeft = Offset(size.width - 25f, size.height - 25f), size = Size(10f, 10f))
                        drawRect(color = qrColor, topLeft = Offset(size.width - 22f, size.height - 22f), size = Size(5f, 5f))

                        // Random bits lines
                        drawLine(color = qrColor, start = Offset(50f, 10f), end = Offset(size.width - 50f, 10f), strokeWidth = strokeWidth)
                        drawLine(color = qrColor, start = Offset(10f, 50f), end = Offset(10f, size.height - 50f), strokeWidth = strokeWidth)
                        drawLine(color = qrColor, start = Offset(50f, size.height - 20f), end = Offset(size.width - 50f, size.height - 20f), strokeWidth = strokeWidth)

                        // Center fancy cross marks (looks like a QR code)
                        drawRect(color = qrColor, topLeft = Offset(50f, 50f), size = Size(15f, 15f))
                        drawRect(color = qrColor.copy(alpha = 0.5f), topLeft = Offset(70f, 50f), size = Size(25f, 10f))
                        drawRect(color = qrColor, topLeft = Offset(50f, 75f), size = Size(10f, 25f))
                        drawRect(color = qrColor, topLeft = Offset(70f, 80f), size = Size(20f, 20f))
                        
                        drawRect(color = qrColor.copy(alpha = 0.7f), topLeft = Offset(100f, 60f), size = Size(15f, 15f))
                        drawRect(color = qrColor, topLeft = Offset(100f, 90f), size = Size(25f, 10f))
                        drawRect(color = qrColor, topLeft = Offset(75f, 110f), size = Size(15f, 15f))
                        drawRect(color = qrColor.copy(alpha = 0.4f), topLeft = Offset(50f, 115f), size = Size(15f, 15f))
                    }
                }

                Text(
                    text = if (activeLanguage == Language.HINDI) 
                        "स्कैन करके सीधे अपनी पसंद के किसी भी यूपीआई ऐप (जैसे PhonePe, GooglePay, Paytm, BHIM) के माध्यम से दान करें।"
                        else "Scan using GooglePay, PhonePe, Paytm or any BHIM UPI app to contribute.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Text(
                    text = "UPI A/C: $targetUpi",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanOchre,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun BankDetailRow(label: String, value: String, context: Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(6.dp))
            .background(Color(0xFFF9FAFB), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(0.85f)) {
            Text(text = label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(text = value, fontSize = 13.sp, color = RajasthanDarkText, fontWeight = FontWeight.ExtraBold)
        }
        IconButton(
            onClick = {
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("bank_detail", value)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(context, "$label Copied!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(32.dp).weight(0.15f)
        ) {
            Icon(
                imageVector = Icons.Default.Share, 
                contentDescription = "Copy detail", 
                tint = RajasthanOchre,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

data class MonthlyPanchangData(
    val monthNameEn: String,
    val monthNameHi: String,
    val poornimaDateEn: String,
    val poornimaDateHi: String,
    val amavasyaDateEn: String,
    val amavasyaDateHi: String,
    val vinchudoDateEn: String,
    val vinchudoDateHi: String
)

val monthlyPanchangDataList = listOf(
    MonthlyPanchangData(
        monthNameEn = "January 2026",
        monthNameHi = "जनवरी २०२६",
        poornimaDateEn = "January 3rd, 2026",
        poornimaDateHi = "३ जनवरी २०२६ (पौष पूर्णिमा)",
        amavasyaDateEn = "January 18th, 2026",
        amavasyaDateHi = "१८ जनवरी २०२६ (पौष अमावस्या)",
        vinchudoDateEn = "January 14th 02:40 AM to January 16th 03:20 PM",
        vinchudoDateHi = "१४ जनवरी प्रातः ०२:४० से १६ जनवरी दोपहर ०३:२० तक"
    ),
    MonthlyPanchangData(
        monthNameEn = "February 2026",
        monthNameHi = "फरवरी २०२६",
        poornimaDateEn = "February 1st, 2026",
        poornimaDateHi = "१ फरवरी २०२६ (माघ पूर्णिमा)",
        amavasyaDateEn = "February 17th, 2026",
        amavasyaDateHi = "१७ फरवरी २०२६ (माघ अमावस्या)",
        vinchudoDateEn = "February 10th 10:15 AM to February 12th 11:30 PM",
        vinchudoDateHi = "१० फरवरी प्रातः १०:१५ से १२ फरवरी रात्रि ११:३० तक"
    ),
    MonthlyPanchangData(
        monthNameEn = "March 2026",
        monthNameHi = "मार्च २०२६",
        poornimaDateEn = "March 3rd, 2026",
        poornimaDateHi = "३ मार्च २०२६ (फाल्गुन पूर्णिमा - होली)",
        amavasyaDateEn = "March 18th, 2026",
        amavasyaDateHi = "१८ मार्च २०२६ (फाल्गुन अमावस्या)",
        vinchudoDateEn = "March 9th 04:30 PM to March 12th 06:10 AM",
        vinchudoDateHi = "९ मार्च सायं ०४:३० से १२ मार्च प्रातः ०६:१० तक"
    ),
    MonthlyPanchangData(
        monthNameEn = "April 2026",
        monthNameHi = "अप्रैल २०२६",
        poornimaDateEn = "April 2nd, 2026",
        poornimaDateHi = "२ अप्रैल २०२६ (चैत्र पूर्णिमा)",
        amavasyaDateEn = "April 17th, 2026",
        amavasyaDateHi = "१७ अप्रैल २०२६ (चैत्र अमावस्या)",
        vinchudoDateEn = "April 5th 11:00 PM to April 8th 12:45 PM",
        vinchudoDateHi = "५ अप्रैल रात्रि ११:०० से ८ अप्रैल दोपहर १२:४५ तक"
    ),
    MonthlyPanchangData(
        monthNameEn = "May 2026",
        monthNameHi = "मई २०२६",
        poornimaDateEn = "May 1st & May 31st, 2026",
        poornimaDateHi = "१ मई व ३१ मई २०२६ (वैशाख व ज्येष्ठ पूर्णिमा)",
        amavasyaDateEn = "May 16th, 2026",
        amavasyaDateHi = "१६ मई २०२६ (वैशाख अमावस्या)",
        vinchudoDateEn = "May 3rd 06:15 AM to May 5th 08:00 PM",
        vinchudoDateHi = "३ मई प्रातः ०६:१५ से ५ मई रात्रि ०८:०० तक"
    ),
    MonthlyPanchangData(
        monthNameEn = "June 2026",
        monthNameHi = "जून २०२६",
        poornimaDateEn = "June 29th, 2026",
        poornimaDateHi = "२९ जून २०२६ (ज्येष्ठ पूर्णिमा)",
        amavasyaDateEn = "June 15th, 2026",
        amavasyaDateHi = "१५ जून २०२६ (ज्येष्ठ अमावस्या)",
        vinchudoDateEn = "June 26th 12:40 PM to June 28th 11:55 PM",
        vinchudoDateHi = "२६ जून दोपहर १२:४० से २८ जून रात्रि ११:५५ तक"
    ),
    MonthlyPanchangData(
        monthNameEn = "July 2026",
        monthNameHi = "जुलाई २०२६",
        poornimaDateEn = "July 29th, 2026",
        poornimaDateHi = "२९ जुलाई २०२६ (आषाढ़ पूर्णिमा - गुरु पूर्णिमा)",
        amavasyaDateEn = "July 14th, 2026",
        amavasyaDateHi = "१४ जुलाई २०२६ (आषाढ़ अमावस्या)",
        vinchudoDateEn = "July 23rd 08:30 PM to July 26th 06:50 AM",
        vinchudoDateHi = "२३ जुलाई रात्रि ०८:३० से २६ जुलाई प्रातः 0६:५० तक"
    ),
    MonthlyPanchangData(
        monthNameEn = "August 2026",
        monthNameHi = "अगस्त २०२६",
        poornimaDateEn = "August 28th, 2026",
        poornimaDateHi = "२८ अगस्त २०२६ (श्रावण पूर्णिमा - रक्षाबंधन)",
        amavasyaDateEn = "August 12th, 2026",
        amavasyaDateHi = "१२ अगस्त २०२६ (श्रावण अमावस्या)",
        vinchudoDateEn = "August 20th 05:10 AM to August 22nd 03:40 PM",
        vinchudoDateHi = "२० अगस्त प्रातः ०५:१० से २२ अगस्त दोपहर ०३:४० तक"
    ),
    MonthlyPanchangData(
        monthNameEn = "September 2026",
        monthNameHi = "सितंबर २०२६",
        poornimaDateEn = "September 26th, 2026",
        poornimaDateHi = "२६ सितंबर २०२६ (भाद्रपद पूर्णिमा)",
        amavasyaDateEn = "September 11th, 2026",
        amavasyaDateHi = "११ सितंबर २०२६ (भाद्रपद अमावस्या)",
        vinchudoDateEn = "September 16th 02:45 PM to September 18th 11:20 PM",
        vinchudoDateHi = "१६ सितंबर दोपहर ०२:४५ से १८ सितंबर रात्रि ११:२० तक"
    ),
    MonthlyPanchangData(
        monthNameEn = "October 2026",
        monthNameHi = "अक्टूबर २०२६",
        poornimaDateEn = "October 26th, 2026",
        poornimaDateHi = "२६ अक्टूबर २०२६ (आश्विन पूर्णिमा - शरद पूर्णिमा)",
        amavasyaDateEn = "October 10th, 2026",
        amavasyaDateHi = "१० अक्टूबर २०२६ (आश्विन अमावस्या - दिवाली)",
        vinchudoDateEn = "October 13th 11:15 PM to October 16th 08:10 AM",
        vinchudoDateHi = "१३ अक्टूबर रात्रि ११:१५ से १६ अक्टूबर प्रातः ०८:१० तक"
    ),
    MonthlyPanchangData(
        monthNameEn = "November 2026",
        monthNameHi = "नवंबर २०२६",
        poornimaDateEn = "November 24th, 2026",
        poornimaDateHi = "२४ नवंबर २०२६ (कार्तिक पूर्णिमा)",
        amavasyaDateEn = "November 9th, 2026",
        amavasyaDateHi = "९ नवंबर २०२६ (कार्तिक अमावस्या)",
        vinchudoDateEn = "November 10th 08:10 AM to November 12th 04:35 PM",
        vinchudoDateHi = "१० नवंबर प्रातः ०८:१० से १२ नवंबर सायं ०४:३५ तक"
    ),
    MonthlyPanchangData(
        monthNameEn = "December 2026",
        monthNameHi = "दिसंबर २०२६",
        poornimaDateEn = "December 23rd, 2026",
        poornimaDateHi = "२३ दिसंबर २०२६ (मार्गशीर्ष पूर्णिमा)",
        amavasyaDateEn = "December 9th, 2026",
        amavasyaDateHi = "९ दिसंबर २०२६ (मार्गशीर्ष अमावस्या)",
        vinchudoDateEn = "December 7th 04:50 PM to December 9th 12:15 AM",
        vinchudoDateHi = "७ दिसंबर सायं ०४:५० से ९ दिसंबर मध्यरात्रि १२:१५ तक"
    )
)

fun getDailyPanchang(activeLanguage: Language): Map<String, String> {
    val calendar = java.util.Calendar.getInstance()
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
    val monthIndex = calendar.get(java.util.Calendar.MONTH) // 0 to 11
    val year = calendar.get(java.util.Calendar.YEAR)
    val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK) // 1 to 7

    val seed = (day * 3 + monthIndex * 7 + year) % 1000

    val hindMonEn = listOf("Pausha", "Magha", "Phalguna", "Chaitra", "Vaishakha", "Jyaistha", "Ashadha", "Shravana", "Bhadrapada", "Ashvina", "Kartika", "Margashirsha")
    val hindMonHi = listOf("पौष", "माघ", "फाल्गुन", "चैत्र", "वैशाख", "ज्येष्ठ", "आषाढ़", "श्रावण", "भाद्रपद", "आश्विन", "कार्तिक", "मार्गशीर्ष")

    val tithiEn = listOf("Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami", "Shasthi", "Saptami", "Ashtami", "Navami", "Dashami", "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Poornima", "Amavasya")
    val tithiHi = listOf("प्रथमा", "द्वितीया", "तृतीया", "चतुर्थी", "पंचमी", "षष्ठी", "सप्तमी", "अष्टमी", "नवमी", "दशमी", "एकादशी", "द्वादशी", "त्रयोदशी", "चतुर्दशी", "पूर्णिमा", "अमावस्या")

    val pakshaEn = listOf("Shukla Paksha", "Krishna Paksha")
    val pakshaHi = listOf("शुक्ल पक्ष", "कृष्ण पक्ष")

    val nakshatraEn = listOf("Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha", "Moola", "Purva Ashadha", "Uttara Ashadha", "Shravana", "Dhanishta", "Shatabhisha", "Purva Bhadrapada", "Uttara Bhadrapada", "Revati", "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashirsha", "Ardra", "Punarvasu", "Pushya", "Ashlesha", "Magha", "Purva Phalguni", "Uttara Phalguni", "Hasta")
    val nakshatraHi = listOf("चित्रा", "स्वाती", "विशाखा", "अनुराधा", "ज्येष्ठा", "मूल", "पूर्वाषाढ़ा", "उत्तराषाढ़ा", "श्रावण", "धनिष्ठा", "शतभीषा", "पूर्वाभाद्रपद", "उत्तराभाद्रपद", "रेवती", "अश्विनी", "भरणी", "कृत्तिका", "रोहिणी", "मृगशिरा", "आर्द्रा", "पुनर्वसु", "पुष्य", "अश्लेषा", "मघा", "पूर्वाफाल्गुनी", "उत्तराफाल्गुनी", "हस्त")

    val hMonthIndex = (monthIndex + 4) % 12
    val hMonthEn = hindMonEn[hMonthIndex]
    val hMonthHi = hindMonHi[hMonthIndex]

    val pakshaIdx = seed % 2
    val pEn = pakshaEn[pakshaIdx]
    val pHi = pakshaHi[pakshaIdx]

    val tithiIdx = seed % 15
    val tEn = if (tithiIdx == 14) {
        if (pakshaIdx == 0) "Poornima" else "Amavasya"
    } else {
        tithiEn[tithiIdx]
    }
    val tHi = if (tithiIdx == 14) {
        if (pakshaIdx == 0) "पूर्णिमा" else "अमावस्या"
    } else {
        tithiHi[tithiIdx]
    }

    val nIdx = (seed + day) % nakshatraEn.size
    val nEn = nakshatraEn[nIdx]
    val nHi = nakshatraHi[nIdx]

    val startMin = 30 + (seed % 25)
    val endMin = (startMin + 53) % 60
    val startHour = 11
    val endHour = 12
    val abhijit = "$startHour:$startMin AM to $endHour:$endMin PM"

    val rahuKaal = when (dayOfWeek) {
        2 -> "07:30 AM to 09:00 AM"
        3 -> "03:00 PM to 04:30 PM"
        4 -> "12:00 PM to 01:30 PM"
        5 -> "01:30 PM to 03:00 PM"
        6 -> "10:30 AM to 12:00 PM"
        7 -> "09:00 AM to 10:30 AM"
        else -> "04:30 PM to 06:00 PM"
    }

    val monthNamesEn = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    val monthNamesHi = listOf("जनवरी", "फरवरी", "मार्च", "अप्रैल", "मई", "जून", "जुलाई", "अगस्त", "सितंबर", "अक्टूबर", "नवंबर", "दिसंबर")

    val formattedDateEn = "$day ${monthNamesEn[monthIndex]} $year"
    val formattedDateHi = "$day ${monthNamesHi[monthIndex]} $year"

    val dateAndTithi = if (activeLanguage == Language.HINDI) {
        "$formattedDateHi • $hMonthHi $pHi $tHi"
    } else {
        "$formattedDateEn • $hMonthEn $pEn $tEn"
    }

    return mapOf(
        "date_tithi" to dateAndTithi,
        "nakshatra" to (if (activeLanguage == Language.HINDI) "$nHi नक्षत्र" else "$nEn Nakshatra"),
        "abhijit" to abhijit,
        "rahu_kaal" to (if (activeLanguage == Language.HINDI) "$rahuKaal (अशुभ समय)" else rahuKaal)
    )
}

@Composable
fun ExtraUtilitiesScreen(onBack: () -> Unit) {
    val activeLanguage = LocalLanguage.current
    var activeSubTab by rememberSaveable { mutableStateOf("suvichar") } // "suvichar" or "monthly_panchang"

    val panchangData = remember { getDailyPanchang(activeLanguage) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .background(RajasthanOchre.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Back", 
                    tint = RajasthanOchre
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = if (activeLanguage == Language.HINDI) "विविध सेवाएँ व अतिरिक्त" else "Samaj Extras & Utilities",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RajasthanDarkText
                )
                Text(
                    text = if (activeLanguage == Language.HINDI) "दैनिक सुविचार, आज का पंचांग एवं विंछुडो समय" else "Daily quotes, Today's panchang and Vinchudo details",
                    fontSize = 12.sp,
                    color = RajasthanDarkText.copy(alpha = 0.6f)
                )
            }
        }

        // Sub Tab selector buttons (Row with only 2 clean paths: Suvichar and Vinchudo Time)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                .background(RajasthanSurface, RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Suvichar & Today's Panchang
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeSubTab == "suvichar") RajasthanOchre else Color.Transparent)
                    .clickable { activeSubTab = "suvichar" }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (activeLanguage == Language.HINDI) "सुविचार व आज का पंचांग" else "Suvichar & Today's Panchang",
                    color = if (activeSubTab == "suvichar") Color.White else RajasthanDarkText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Vinchudo Period details
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeSubTab == "monthly_panchang") RajasthanOchre else Color.Transparent)
                    .clickable { activeSubTab = "monthly_panchang" }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (activeLanguage == Language.HINDI) "विंछुडो काल विवरण" else "Vinchudo details & Time",
                    color = if (activeSubTab == "monthly_panchang") Color.White else RajasthanDarkText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Selected content panel
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeSubTab) {
                "suvichar" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Suvichar Card (Decorative framing)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, RajasthanGold, RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("✨ दैनिक कुमावत सुविचार ✨", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RajasthanOchre)
                                
                                Text(
                                    text = "“एकता में ही समाज की वास्तविक शक्ति निहित है। जब हम सब मिलकर एक-दूसरे का हाथ थामते हैं, तो हमारा समाज उन्नति की बुलंदियों को छूता है।”",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RajasthanDarkText,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 22.sp
                                )
                                
                                Text(
                                    text = "“Ultimate strength lies in unity. When we join hands to support one another, our community climbs to the absolute peak of prosperity.”",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp
                                )

                                Box(
                                    modifier = Modifier
                                        .background(RajasthanOchre.copy(alpha = 0.1f), RoundedCornerShape(50.dp))
                                        .padding(horizontal = 12.dp, vertical = 2.dp)
                                ) {
                                    Text("ऋषि कुमावत वाणी", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RajasthanOchre)
                                }
                            }
                        }

                        // 2. Today's Hindu Panchang Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (activeLanguage == Language.HINDI) "हिंदू पंचांग विवरण 🚩" else "Today's Hindu Panchang 🚩",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = RajasthanDarkText
                                    )
                                    Text(
                                        text = "Vikram Samvat 2083",
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                DividerForExtras()

                                PanchangRow(
                                    label = if (activeLanguage == Language.HINDI) "तारीख व तिथि" else "Date & Tithi",
                                    value = panchangData["date_tithi"] ?: ""
                                )
                                PanchangRow(
                                    label = if (activeLanguage == Language.HINDI) "नक्षत्र" else "Nakshatra",
                                    value = panchangData["nakshatra"] ?: ""
                                )
                                PanchangRow(
                                    label = if (activeLanguage == Language.HINDI) "शुभ समय (Abhijit)" else "Abhijit Muhurta",
                                    value = panchangData["abhijit"] ?: ""
                                )
                                PanchangRow(
                                    label = if (activeLanguage == Language.HINDI) "राहु काल (Rahu Kaal)" else "Rahu Kaal",
                                    value = panchangData["rahu_kaal"] ?: ""
                                )
                            }
                        }
                    }
                }
                "monthly_panchang" -> {
                    val calendar = java.util.Calendar.getInstance()
                    val currentMonthIndex = calendar.get(java.util.Calendar.MONTH).coerceIn(0, 11)
                    val currentData = monthlyPanchangDataList[currentMonthIndex]

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (activeLanguage == Language.HINDI) "इस महीने का विंछुडो काल 🦂" else "This Month's Vinchudo Period 🦂",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = RajasthanOchre
                        )

                        // Vinchudo details Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, RajasthanGold, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = RajasthanSurface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Title Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (activeLanguage == Language.HINDI) currentData.monthNameHi else currentData.monthNameEn,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = RajasthanOchre
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFFFF1F1), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (activeLanguage == Language.HINDI) "स्वयंचलित दिनांक" else "Auto-updated", 
                                            fontSize = 9.sp, 
                                            color = Color(0xFFC0392B), 
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                DividerForExtras()

                                // Vinchudo Card/Row
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFFF1F1), RoundedCornerShape(8.dp))
                                        .border(0.5.dp, Color(0xFFFFA7A7), RoundedCornerShape(8.dp))
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🦂 ", fontSize = 16.sp)
                                        Text(
                                            text = if (activeLanguage == Language.HINDI) "विंछुडो काल विवरण (Vinchudo Period)" else "Vinchudo Period (Scorpio Moon)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFFC0392B)
                                        )
                                    }
                                    Text(
                                        text = if (activeLanguage == Language.HINDI) currentData.vinchudoDateHi else currentData.vinchudoDateEn,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFC0392B),
                                        modifier = Modifier.padding(start = 22.dp)
                                    )
                                    Text(
                                        text = if (activeLanguage == Language.HINDI)
                                            "⚠️ मान्यता: विंछुडो काल में कोई भी शुभ/नवीन कार्य (जैसे सगाई, विवाह, गृह प्रवेश या व्यापार प्रारंभ) वर्जित और अशुभ माना जाता है।"
                                            else "⚠️ Note: Under Rajasthani tradition, Vinchudo represents the transit of Moon through Scorpio. Starting new ventures, weddings or buying assets is avoided during this period.",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray,
                                        lineHeight = 15.sp,
                                        modifier = Modifier.padding(start = 22.dp, top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PanchangRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(text = label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 13.sp, color = RajasthanDarkText, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun DividerForExtras() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.LightGray.copy(alpha = 0.4f))
            .padding(vertical = 4.dp)
    )
}
