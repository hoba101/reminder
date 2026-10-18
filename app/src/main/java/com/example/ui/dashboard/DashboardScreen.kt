package com.example.ui.dashboard

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyItem
import com.example.data.model.SubjectWithItems
import com.example.ui.ChecklistFilter
import com.example.ui.StudyViewModel
import com.example.ui.components.BotanicalBranchDivider
import com.example.ui.components.BotanicalCard
import com.example.ui.components.BotanicalFilterChip
import com.example.ui.components.BotanicalIcons
import com.example.ui.components.BotanicalProgressBar
import com.example.ui.components.BotanicalProgressRing
import com.example.ui.components.GirlyFlowerLineIcon
import com.example.ui.components.GirlyHeartLineIcon
import com.example.ui.components.GirlySparkleLineIcon
import com.example.ui.theme.GirlyAmberAccent
import com.example.ui.theme.GirlyAmberContainer
import com.example.ui.theme.GirlyButterHoney
import com.example.ui.theme.GirlyCanvasBackground
import com.example.ui.theme.GirlyCardBorder
import com.example.ui.theme.GirlyCardSurface
import com.example.ui.theme.GirlyDividerColor
import com.example.ui.theme.GirlyPinkContainer
import com.example.ui.theme.GirlyPinkDark
import com.example.ui.theme.GirlyPinkDeep
import com.example.ui.theme.GirlyPinkHighlight
import com.example.ui.theme.GirlyPinkPrimary
import com.example.ui.theme.GirlyTextPrimary
import com.example.ui.theme.GirlyTextSecondary
import com.example.ui.theme.GirlyTextTertiary

@Composable
fun DashboardScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allSubjects by viewModel.allSubjects.collectAsState()
    val filteredSubjects by viewModel.filteredSubjects.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val isReminderActive by viewModel.isReminderActive.collectAsState()

    var showReminderDialog by remember { mutableStateOf(false) }

    // Request notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Default: all subjects expanded for instant checklist access
    val expandedMap = remember { mutableStateMapOf<Long, Boolean>() }

    // Overall stats
    val totalTopics = allSubjects.sumOf { it.totalCount }
    val completedTopics = allSubjects.sumOf { it.completedCount }
    val remainingTopics = allSubjects.sumOf { it.remainingCount }
    val progressPercent = if (totalTopics > 0) ((completedTopics.toFloat() / totalTopics) * 100).toInt() else 0

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GirlyCanvasBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Cute Girly Top Bar & Motivation Banner
            item {
                GirlyTopHeader(
                    onOpenReminders = { showReminderDialog = true },
                    isReminderActive = isReminderActive
                )
            }

            // 2. Cute Overall Progress Hero Card
            item {
                GirlyProgressCard(
                    completedCount = completedTopics,
                    totalCount = totalTopics,
                    progressPercent = progressPercent
                )
            }

            // 3. User Feedback Toast
            item {
                AnimatedVisibility(visible = userMessage != null) {
                    userMessage?.let { msg ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            color = GirlyPinkContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GirlyPinkHighlight)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = GirlyPinkDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = msg,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = GirlyPinkDeep,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.dismissMessage() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "إغلاق",
                                        tint = GirlyTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Search and Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Soft Pink Search Input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = {
                            Text(
                                text = "ابحثي عن أي مادة أو فصل أو موضوع...",
                                style = MaterialTheme.typography.bodySmall.copy(color = GirlyTextTertiary)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                tint = GirlyPinkPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "مسح",
                                        tint = GirlyTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_input_field"),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GirlyPinkPrimary,
                            unfocusedBorderColor = GirlyCardBorder,
                            focusedContainerColor = GirlyCardSurface,
                            unfocusedContainerColor = GirlyCardSurface
                        )
                    )

                    // Filter Chips (All 9 Subjects, Pending, Completed)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BotanicalFilterChip(
                            selected = selectedFilter == ChecklistFilter.ALL,
                            label = "جميع المواد (${allSubjects.size})",
                            count = totalTopics,
                            onClick = { viewModel.setFilter(ChecklistFilter.ALL) }
                        )
                        BotanicalFilterChip(
                            selected = selectedFilter == ChecklistFilter.PENDING,
                            label = "المتبقي",
                            count = remainingTopics,
                            onClick = { viewModel.setFilter(ChecklistFilter.PENDING) }
                        )
                        BotanicalFilterChip(
                            selected = selectedFilter == ChecklistFilter.COMPLETED,
                            label = "المكتمل",
                            count = completedTopics,
                            onClick = { viewModel.setFilter(ChecklistFilter.COMPLETED) }
                        )
                    }
                }
            }

            // 5. Subject Cards List (المواد الدراسية التسعة)
            if (filteredSubjects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GirlyFlowerLineIcon(
                                modifier = Modifier.size(44.dp),
                                tint = GirlyPinkPrimary
                            )
                            Text(
                                text = if (searchQuery.isNotEmpty()) "لا توجد نتائج مطابقة لبحثكِ" else "جاري تحميل موادكِ الدراسية...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = GirlyTextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            } else {
                items(filteredSubjects, key = { it.subject.id }) { subjectWithItems ->
                    val isExpanded = expandedMap[subjectWithItems.subject.id] ?: true

                    GirlySubjectCardItem(
                        subjectWithItems = subjectWithItems,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            expandedMap[subjectWithItems.subject.id] = !isExpanded
                        },
                        onToggleTopic = { topicId, currentVal ->
                            viewModel.toggleItem(topicId, currentVal)
                        }
                    )
                }
            }

            // 6. Cute Motivation Footer
            item {
                GirlyFooterQuote()
            }
        }
    }

    // Reminder Dialog
    if (showReminderDialog) {
        AlertDialog(
            onDismissRequest = { showReminderDialog = false },
            confirmButton = {
                Button(
                    onClick = { showReminderDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GirlyPinkPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("تم", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.sendTestNotification(context)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GirlyPinkDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GirlyPinkHighlight)
                ) {
                    Text("تجربة إشعار فوري", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GirlyFlowerLineIcon(modifier = Modifier.size(20.dp), tint = GirlyPinkPrimary)
                    Text(
                        text = "تذكيرات المذاكرة اليومية",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GirlyTextPrimary
                        )
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "التطبيق بيفكركِ كل بضع ساعات بإشعار لطيف يسألكِ: هل ذاكرتِ أي حاجة من موضوعات كليتكِ النهاردة؟",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GirlyTextSecondary)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GirlyPinkContainer)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "تفعيل التذكير التلقائي",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GirlyPinkDeep
                            )
                        )
                        Switch(
                            checked = isReminderActive,
                            onCheckedChange = { viewModel.toggleReminders(context, it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GirlyPinkPrimary,
                                uncheckedThumbColor = GirlyTextTertiary,
                                uncheckedTrackColor = GirlyCardBorder
                            )
                        )
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = GirlyCardSurface
        )
    }
}

@Composable
fun GirlyTopHeader(
    onOpenReminders: () -> Unit,
    isReminderActive: Boolean
) {
    BotanicalCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        backgroundColor = GirlyCardSurface,
        borderColor = GirlyCardBorder,
        borderWidth = 1.2.dp,
        elevation = 1.5.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(GirlyPinkContainer)
                            .border(1.dp, GirlyPinkHighlight, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        GirlyFlowerLineIcon(
                            modifier = Modifier.size(26.dp),
                            tint = GirlyPinkDark
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "مذكرتي الوردية",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GirlyTextPrimary
                                )
                            )
                            GirlySparkleLineIcon(
                                modifier = Modifier.size(16.dp),
                                tint = GirlyPinkPrimary
                            )
                        }
                        Text(
                            text = "رفيقة المذاكرة وتتبع مواد الكلية",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = GirlyTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Notification Reminder Bell Button
                IconButton(
                    onClick = onOpenReminders,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isReminderActive) GirlyPinkContainer else Color(0xFFFBF4F6))
                        .border(1.dp, if (isReminderActive) GirlyPinkHighlight else GirlyCardBorder, CircleShape)
                        .testTag("notification_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsActive,
                        contentDescription = "التذكيرات",
                        tint = if (isReminderActive) GirlyPinkPrimary else GirlyTextTertiary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sweet Encouragement Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(GirlyButterHoney)
                    .border(1.dp, Color(0xFFFFECC4), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GirlyHeartLineIcon(modifier = Modifier.size(16.dp), tint = GirlyPinkPrimary)
                Text(
                    text = "جميلتي.. كل خطوة وإنجاز اليوم يصنع مستقبلكِ المشرق غداً",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GirlyPinkDeep,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun GirlyProgressCard(
    completedCount: Int,
    totalCount: Int,
    progressPercent: Int
) {
    BotanicalCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = GirlyCardSurface,
        borderColor = GirlyCardBorder,
        borderWidth = 1.2.dp,
        elevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "مستوى الإنجاز العام",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GirlyTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تم إنجاز $completedCount من أصل $totalCount فصلاً وموضوعاً",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GirlyTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (progressPercent >= 50) GirlyPinkHighlight else GirlyPinkContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (progressPercent >= 100) "أتممتِ كل الفصول بنجاح!" else "متبقي $${totalCount - completedCount} موضوعاً",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GirlyPinkDeep
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Beautiful Pink Progress Ring
                BotanicalProgressRing(
                    progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f,
                    modifier = Modifier.size(76.dp),
                    strokeWidth = 7.dp,
                    ringColor = GirlyPinkPrimary,
                    trackColor = GirlyPinkContainer
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$progressPercent%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GirlyPinkDeep
                            )
                        )
                        Text(
                            text = "منجز",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = GirlyTextTertiary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            BotanicalProgressBar(
                progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f,
                height = 8.dp,
                barColor = GirlyPinkPrimary,
                trackColor = GirlyPinkContainer
            )
        }
    }
}

@Composable
fun GirlySubjectCardItem(
    subjectWithItems: SubjectWithItems,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleTopic: (Long, Boolean) -> Unit
) {
    val subject = subjectWithItems.subject

    BotanicalCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subject_card_${subject.code}"),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = GirlyCardSurface,
        borderColor = GirlyCardBorder,
        borderWidth = 1.2.dp,
        elevation = 1.2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Icon, Subject Title, Code Badge, Expand Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Subject Icon with Soft Pink background
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(GirlyPinkContainer)
                            .border(1.dp, GirlyPinkHighlight, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = BotanicalIcons.getIconForSubject(subject.iconKey),
                            contentDescription = subject.name,
                            tint = GirlyPinkDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GirlyTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Subject Code Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GirlyPinkHighlight)
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = subject.code,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GirlyPinkDeep,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            // Practical/Theory Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (subject.hasPractical) GirlyPinkContainer else Color(0xFFF9F0FD))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (subject.hasPractical) "نظري وعملي" else "نظري فقط",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (subject.hasPractical) GirlyPinkDark else Color(0xFF8B5FA8),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Completion score badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (subjectWithItems.isAllCompleted) GirlyPinkHighlight else GirlyPinkContainer)
                            .border(1.dp, if (subjectWithItems.isAllCompleted) GirlyPinkPrimary else GirlyCardBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${subjectWithItems.completedCount}/${subjectWithItems.totalCount}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GirlyPinkDeep
                            )
                        )
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        contentDescription = if (isExpanded) "طي" else "توسيع",
                        tint = GirlyPinkPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Soft Pink Progress Bar for this Subject
            BotanicalProgressBar(
                progress = subjectWithItems.progressFraction,
                height = 7.dp,
                barColor = GirlyPinkPrimary,
                trackColor = GirlyPinkContainer
            )

            // Expanded Checklist Section
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            GirlyFlowerLineIcon(modifier = Modifier.size(16.dp), tint = GirlyPinkPrimary)
                            Text(
                                text = "فصول وموضوعات المادة:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GirlyPinkDeep
                                )
                            )
                        }

                        Text(
                            text = "اضغطي لوضع علامة صح",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GirlyTextTertiary,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Checklist Topic Items
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        subjectWithItems.sortedItems.forEach { item ->
                            GirlyTopicRow(
                                item = item,
                                onToggle = { onToggleTopic(item.id, item.isCompleted) }
                            )
                        }
                    }

                    // Optional Curriculum Details Accordion
                    var showConcepts by remember { mutableStateOf(false) }

                    if (subject.keyConcepts.isNotBlank() || subject.learningGoal.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(GirlyPinkContainer.copy(alpha = 0.5f))
                                .clickable { showConcepts = !showConcepts }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = GirlyPinkPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (showConcepts) "إخفاء ملخص ومفاهيم المادة" else "عرض أهم مفاهيم المادة وأهدافها",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GirlyPinkDeep
                                    )
                                )
                            }
                            Icon(
                                imageVector = if (showConcepts) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                contentDescription = null,
                                tint = GirlyPinkPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        AnimatedVisibility(visible = showConcepts) {
                            Column(
                                modifier = Modifier.padding(top = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (subject.keyConcepts.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(GirlyCardSurface)
                                            .border(1.dp, GirlyCardBorder, RoundedCornerShape(14.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "أهم المفاهيم والموضوعات:",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = GirlyPinkDark
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = subject.keyConcepts,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = GirlyTextPrimary,
                                                    lineHeight = 19.sp
                                                )
                                            )
                                        }
                                    }
                                }

                                if (subject.learningGoal.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(GirlyAmberContainer)
                                            .border(1.dp, Color(0xFFFFECC4), RoundedCornerShape(14.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "ماذا يجب أن تفهمي في هذا المقرر؟",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = GirlyAmberAccent
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = subject.learningGoal,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = GirlyTextPrimary,
                                                    lineHeight = 19.sp
                                                )
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
    }
}

@Composable
fun GirlyTopicRow(
    item: StudyItem,
    onToggle: () -> Unit
) {
    val isDone = item.isCompleted

    val rowBg by animateColorAsState(
        targetValue = if (isDone) GirlyPinkContainer else GirlyCardSurface,
        label = "topicBg"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (isDone) GirlyPinkHighlight else GirlyCardBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable { onToggle() },
        color = rowBg,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = isDone,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = GirlyPinkPrimary,
                        uncheckedColor = GirlyCardBorder,
                        checkmarkColor = Color.White
                    ),
                    modifier = Modifier.size(22.dp)
                )

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isDone) GirlyTextSecondary else GirlyTextPrimary,
                        fontWeight = if (isDone) FontWeight.Medium else FontWeight.SemiBold,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
            }

            if (isDone) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GirlyPinkPrimary)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "منجز",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            } else {
                Text(
                    text = "متبقي",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GirlyTextTertiary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
fun GirlyFooterQuote() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BotanicalBranchDivider(
            modifier = Modifier.fillMaxWidth().height(14.dp),
            tint = GirlyPinkPrimary.copy(alpha = 0.35f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            GirlyFlowerLineIcon(modifier = Modifier.size(14.dp), tint = GirlyPinkPrimary)
            Text(
                text = "بالعلم تصنعين مستقبلكِ • والتنظيم هو سر تفوقكِ الدائم",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = GirlyPinkDeep,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
            GirlySparkleLineIcon(modifier = Modifier.size(14.dp), tint = GirlyPinkPrimary)
        }
    }
}
