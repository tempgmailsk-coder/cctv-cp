package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EventType
import com.example.data.model.SecurityEvent
import com.example.ui.components.SecurityBadge
import com.example.ui.theme.SecurityAlertRed
import com.example.ui.theme.SecurityNavyBorder
import com.example.ui.theme.SecurityNavyCard
import com.example.ui.theme.SecurityNavyDark
import com.example.ui.theme.SentinelCyan
import com.example.ui.viewmodel.CctvViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventHistoryScreen(
    viewModel: CctvViewModel,
    onPlayEvent: (SecurityEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val events by viewModel.filteredEvents.collectAsStateWithLifecycle()
    val cameras by viewModel.cameras.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf<EventType?>(null) }
    var selectedCameraId by remember { mutableStateOf<Long?>(null) }
    var bookmarkedOnly by remember { mutableStateOf(false) }

    fun updateFilters() {
        viewModel.filterQuery.value = searchQuery
        viewModel.filterEventType.value = selectedTypeFilter
        viewModel.filterCameraId.value = selectedCameraId
        viewModel.filterBookmarkedOnly.value = bookmarkedOnly
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SecurityNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Surveillance Event History",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${events.size} incident clips recorded automatically",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = { viewModel.clearAllEvents() },
                    modifier = Modifier.testTag("clear_all_events_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear all",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    updateFilters()
                },
                placeholder = { Text("Search by camera, person, or type...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF94A3B8))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            updateFilters()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("event_search_bar"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = SentinelCyan,
                    unfocusedBorderColor = SecurityNavyBorder
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Filter Chips (All, Unknown, Person, Motion, Bookmarked)
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        label = "All Events",
                        isSelected = selectedTypeFilter == null && !bookmarkedOnly,
                        onClick = {
                            selectedTypeFilter = null
                            bookmarkedOnly = false
                            updateFilters()
                        }
                    )
                }
                item {
                    FilterChip(
                        label = "⚠️ Unknown",
                        isSelected = selectedTypeFilter == EventType.UNKNOWN_PERSON,
                        onClick = {
                            selectedTypeFilter = if (selectedTypeFilter == EventType.UNKNOWN_PERSON) null else EventType.UNKNOWN_PERSON
                            updateFilters()
                        }
                    )
                }
                item {
                    FilterChip(
                        label = "👤 Person",
                        isSelected = selectedTypeFilter == EventType.PERSON_DETECTED,
                        onClick = {
                            selectedTypeFilter = if (selectedTypeFilter == EventType.PERSON_DETECTED) null else EventType.PERSON_DETECTED
                            updateFilters()
                        }
                    )
                }
                item {
                    FilterChip(
                        label = "🚨 Intrusion",
                        isSelected = selectedTypeFilter == EventType.ZONE_INTRUSION,
                        onClick = {
                            selectedTypeFilter = if (selectedTypeFilter == EventType.ZONE_INTRUSION) null else EventType.ZONE_INTRUSION
                            updateFilters()
                        }
                    )
                }
                item {
                    FilterChip(
                        label = "★ Saved",
                        isSelected = bookmarkedOnly,
                        onClick = {
                            bookmarkedOnly = !bookmarkedOnly
                            updateFilters()
                        }
                    )
                }
            }
        }

        // Event List
        if (events.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No recorded security events match filters",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp
                        )
                        TextButton(onClick = {
                            searchQuery = ""
                            selectedTypeFilter = null
                            selectedCameraId = null
                            bookmarkedOnly = false
                            updateFilters()
                        }) {
                            Text("Reset Filters", color = SentinelCyan)
                        }
                    }
                }
            }
        } else {
            items(events, key = { it.id }) { event ->
                val timeFormat = SimpleDateFormat("hh:mm:ss a • dd MMM yyyy", Locale.getDefault())
                val formattedTime = timeFormat.format(Date(event.timestamp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SecurityNavyBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            viewModel.selectEvent(event)
                            onPlayEvent(event)
                        }
                        .testTag("history_event_${event.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SecurityNavyCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Thumbnail with Duration Overlay
                        Box(
                            modifier = Modifier
                                .size(width = 96.dp, height = 68.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play event",
                                tint = SentinelCyan,
                                modifier = Modifier.size(32.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xCC000000))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${event.clipDurationSeconds}s",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            SecurityBadge(eventType = event.eventType)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = event.cameraName,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (event.personName != null) {
                                Text(
                                    text = event.personName,
                                    color = if (event.eventType == EventType.UNKNOWN_PERSON) SecurityAlertRed else SentinelCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Text(
                                text = formattedTime,
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }

                        // Bookmark & Delete Action buttons
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = { viewModel.toggleBookmark(event) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (event.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Save clip",
                                    tint = if (event.isBookmarked) SentinelCyan else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.deleteEvent(event) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete event",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(18.dp)
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
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) SentinelCyan else SecurityNavyCard)
            .border(1.dp, if (isSelected) SentinelCyan else SecurityNavyBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
