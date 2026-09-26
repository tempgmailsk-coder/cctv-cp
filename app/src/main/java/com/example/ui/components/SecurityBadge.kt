package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventType
import com.example.ui.theme.SecurityAlertRed
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.SecurityWarningAmber
import com.example.ui.theme.SentinelCyan

@Composable
fun SecurityBadge(
    eventType: EventType,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (eventType) {
        EventType.UNKNOWN_PERSON -> Triple(
            SecurityAlertRed.copy(alpha = 0.2f),
            SecurityAlertRed,
            "UNKNOWN PERSON"
        )
        EventType.PERSON_DETECTED -> Triple(
            SentinelCyan.copy(alpha = 0.2f),
            SentinelCyan,
            "PERSON DETECTED"
        )
        EventType.FACE_RECOGNIZED -> Triple(
            SecuritySafeGreen.copy(alpha = 0.2f),
            SecuritySafeGreen,
            "KNOWN PERSON"
        )
        EventType.ZONE_INTRUSION -> Triple(
            Color(0xFFEC4899).copy(alpha = 0.2f),
            Color(0xFFEC4899),
            "INTRUSION"
        )
        EventType.MOTION_DETECTED -> Triple(
            SecurityWarningAmber.copy(alpha = 0.2f),
            SecurityWarningAmber,
            "MOTION ALERT"
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun StatusPill(
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isOnline) SecuritySafeGreen else Color.Gray
    val label = if (isOnline) "LIVE" else "OFFLINE"
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0x99000000))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
