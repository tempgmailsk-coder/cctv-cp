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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.KnownPerson
import com.example.ui.theme.SecurityAlertRed
import com.example.ui.theme.SecurityNavyBorder
import com.example.ui.theme.SecurityNavyCard
import com.example.ui.theme.SecurityNavyDark
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.SentinelCyan
import com.example.ui.viewmodel.CctvViewModel

@Composable
fun KnownPersonsScreen(
    viewModel: CctvViewModel,
    modifier: Modifier = Modifier
) {
    val persons by viewModel.knownPersons.collectAsStateWithLifecycle()
    val preferences = viewModel.preferences

    var faceRecEnabled by remember { mutableStateOf(preferences.faceRecognitionEnabled) }
    var confidenceThreshold by remember { mutableFloatStateOf(preferences.faceConfidenceThreshold) }
    var showAddPersonDialog by remember { mutableStateOf(false) }

    var newName by remember { mutableStateOf("") }
    var newRelationship by remember { mutableStateOf("Family Member") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SecurityNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Face Recognition & Known Persons",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Distinguish family & known visitors from unknown subjects",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = { showAddPersonDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SentinelCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_person_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Master Face Recognition Setting Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SecurityNavyBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SecurityNavyCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable Face Identification",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Compares detected faces with registered members. If no match is found with sufficient confidence, triggers 'Unknown person detected' alert.",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Switch(
                            checked = faceRecEnabled,
                            onCheckedChange = {
                                faceRecEnabled = it
                                preferences.faceRecognitionEnabled = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SentinelCyan,
                                checkedTrackColor = Color(0xFF004D63)
                            ),
                            modifier = Modifier.testTag("face_rec_toggle")
                        )
                    }

                    if (faceRecEnabled) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Unknown Subject Confidence Threshold",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${(confidenceThreshold * 100).toInt()}%",
                                color = SentinelCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Only alerts 'Unknown Person' when camera face analysis confidence is above this threshold to eliminate false claims.",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                        )

                        Slider(
                            value = confidenceThreshold,
                            onValueChange = {
                                confidenceThreshold = it
                                preferences.faceConfidenceThreshold = it
                            },
                            valueRange = 0.50f..0.95f,
                            colors = SliderDefaults.colors(
                                thumbColor = SentinelCyan,
                                activeTrackColor = SentinelCyan,
                                inactiveTrackColor = Color(0xFF334155)
                            ),
                            modifier = Modifier.testTag("face_threshold_slider")
                        )
                    }
                }
            }
        }

        // Registered Persons Header
        item {
            Text(
                text = "Registered Profiles (${persons.size})",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Persons List
        if (persons.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No family members or known persons registered.\nTap '+ Add' to register a profile.",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(persons, key = { it.id }) { person ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SecurityNavyBorder, RoundedCornerShape(12.dp))
                        .testTag("known_person_${person.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SecurityNavyCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SentinelCyan.copy(alpha = 0.2f))
                                .border(1.5.dp, SentinelCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = person.name,
                                tint = SentinelCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = person.name,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = person.relationship,
                                color = SentinelCyan,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Baseline Match: ${(person.confidenceBaseline * 100).toInt()}%",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }

                        IconButton(
                            onClick = { viewModel.deleteKnownPerson(person) },
                            modifier = Modifier.testTag("delete_person_${person.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete person",
                                tint = SecurityAlertRed
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Person Dialog
    if (showAddPersonDialog) {
        AlertDialog(
            onDismissRequest = { showAddPersonDialog = false },
            containerColor = SecurityNavyCard,
            title = {
                Text("Register Known Person", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Add family members or authorized staff so CP PLUS facial recognition suppresses unknown intruder alarms.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Full Name") },
                        placeholder = { Text("e.g. David Miller") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_person_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SentinelCyan,
                            unfocusedBorderColor = SecurityNavyBorder
                        )
                    )

                    OutlinedTextField(
                        value = newRelationship,
                        onValueChange = { newRelationship = it },
                        label = { Text("Relationship / Role") },
                        placeholder = { Text("e.g. Family / Spouse, Property Staff") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_person_role_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SentinelCyan,
                            unfocusedBorderColor = SecurityNavyBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.saveKnownPerson(
                                KnownPerson(
                                    name = newName.trim(),
                                    relationship = newRelationship.ifBlank { "Family" },
                                    faceTag = "face_${newName.lowercase().replace(" ", "_")}"
                                )
                            ) {
                                showAddPersonDialog = false
                                newName = ""
                                newRelationship = "Family Member"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SentinelCyan),
                    modifier = Modifier.testTag("confirm_add_person_btn")
                ) {
                    Text("Register Profile", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPersonDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}
