package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SecurityAlertRed
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.SentinelCyan

@Composable
fun MotionZoneGrid(
    selectedZones: Set<Int>,
    onZoneToggled: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, SentinelCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // 4 rows x 4 columns = 16 detection cells
        for (row in 0 until 4) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                for (col in 0 until 4) {
                    val zoneIndex = row * 4 + col
                    val isArmed = selectedZones.contains(zoneIndex)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isArmed) SentinelCyan.copy(alpha = 0.35f)
                                else Color(0x33000000)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isArmed) SentinelCyan else Color(0x33FFFFFF),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { onZoneToggled(zoneIndex) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Z$zoneIndex",
                            color = if (isArmed) Color.White else Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
