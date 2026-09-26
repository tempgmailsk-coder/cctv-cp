package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SecurityAlertRed
import com.example.ui.theme.SecurityNavyBorder
import com.example.ui.theme.SecurityNavyCard
import com.example.ui.theme.SecurityNavyDark
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.SentinelCyan

@Composable
fun ArchitectureGuideScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SecurityNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "CP PLUS System Architecture & Integration",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Comprehensive technical specifications, API schemas, and event protocols",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // 1. System Architecture
        item {
            ArchitectureCard(
                title = "1. System Architecture",
                icon = Icons.Default.Dns,
                content = """
                • Edge Tier (CP PLUS DVR/NVR): Hardware video encoding (H.264/H.265), Smart Motion Detection (SMD) processor, and local HDD circular ring buffer.
                • Transport Tier: RTSP (Real-Time Streaming Protocol) on port 554 for H.264/AAC feeds; HTTP/CGI on port 80/37777 for bi-directional event socket and PTZ telemetry; ONVIF Profile S/T for cross-model interop.
                • Mobile Sentinel Tier: Android Foreground Surveillance Service (`CctvSurveillanceService`) running independently of app lifecycle, parsing persistent Dahua/CP PLUS eventManager streams with zero battery drain.
                • Persistence & Forensics Tier: Local Room SQLite with foreign keys for cameras, detection events, and registered faces.
                """.trimIndent()
            )
        }

        // 2. CP PLUS Integration Method
        item {
            ArchitectureCard(
                title = "2. CP PLUS Integration Method & CGI APIs",
                icon = Icons.Default.Code,
                content = """
                • RTSP Stream URL:
                  rtsp://{user}:{pwd}@{ip}:554/cam/realmonitor?channel={ch}&subtype={0=main, 1=sub}
                • Hardware Event Notification CGI:
                  GET /cgi-bin/eventManager.cgi?action=attach&codes=[CrossLineDetection,VideoMotion,CrossRegionDetection,FaceDetection,SmartMotionHuman]
                • Instant Snapshot CGI:
                  GET /cgi-bin/snapshot.cgi?channel={ch}
                • PTZ Control CGI:
                  GET /cgi-bin/ptz.cgi?action=start&channel={ch}&code={Up|Down|Left|Right|ZoomIn|ZoomOut}&arg1=0&arg2=5&arg3=0
                • Playback Clip Download:
                  GET /cgi-bin/loadfile.cgi?action=startLoad_s&channel={ch}&startTime={start}&endTime={end}
                """.trimIndent(),
                isCode = true
            )
        }

        // 3. Required Hardware & Software
        item {
            ArchitectureCard(
                title = "3. Required Hardware & Software",
                icon = Icons.Default.Videocam,
                content = """
                • Hardware:
                  - CP PLUS DVR/NVR (Orange, Cosmic, Indigo, or Ezykam series) with static LAN IP or DDNS.
                  - IP / Analog cameras with PIR or hardware SMD (Smart Motion Detection).
                  - Android device (API 24+, Android 7.0 through Android 15/16).
                • Software & Protocols:
                  - CP PLUS Dahua-compatible SDK CGI v2.0+
                  - RTSP RFC 2326, ONVIF Core Specification 2.4+
                  - Android Jetpack Compose M3, Room KSP, OkHttp3, Coroutines.
                """.trimIndent()
            )
        }

        // 4. Database Structure
        item {
            ArchitectureCard(
                title = "4. Database Structure (Room SQLite)",
                icon = Icons.Default.Storage,
                content = """
                • Table `cameras`:
                  id (PK), name, ipAddress, rtspPort, httpPort, channel, username, passwordEncrypted, modelPreset, streamType, isOnline, isArmed, personOnlyFilter, sensitivity, detectionZonesGrid (4x4 bitmask).
                • Table `security_events`:
                  id (PK), cameraId (FK), cameraName, timestamp, eventType, personName, confidence, thumbnailUrl, clipUrl, clipDurationSeconds, isRead, isBookmarked, fileSizeBytes, details.
                • Table `known_persons`:
                  id (PK), name, relationship, photoUri, faceTag, confidenceBaseline, addedTimestamp.
                """.trimIndent(),
                isCode = true
            )
        }

        // 5. Notification Architecture
        item {
            ArchitectureCard(
                title = "5. Push Notification Architecture",
                icon = Icons.Default.Notifications,
                content = """
                • High-Priority Alert Channel: `cctv_alerts_channel` with heads-up banner, vibration alert [0, 300, 200, 300], and critical priority.
                • Persistent Sentinel Channel: `cctv_surveillance_service_channel` to maintain background socket keepalive.
                • Deep Link PendingIntent: Directly launches Event Video Player Screen with pre-buffered 20s clip.
                • Smart Filter: Evaluates Quiet Hours (DND), camera arming state, and person-only classification before alerting.
                """.trimIndent()
            )
        }

        // 6. Video Recording & Buffering Workflow
        item {
            ArchitectureCard(
                title = "6. Video Recording & Event Workflow",
                icon = Icons.Default.Security,
                content = """
                1. Continuous Ring Buffer: Circular in-memory buffer stores 5s of streaming frames continuously.
                2. Event Trigger: CP PLUS DVR reports SmartMotionHuman or IVS tripwire event via eventManager.cgi.
                3. Video Clip Packaging: Commits 5s pre-event buffer + records subsequent 15s post-event stream = 20s total MP4 file (~4.5MB).
                4. Face Match / Unknown Check: Onboard face feature comparator checks registered profiles. If match confidence < threshold (80%), flags as 'Unknown Person Detected'.
                5. Instant Notification: Alerts user with thumbnail snapshot and one-tap video playback.
                """.trimIndent()
            )
        }
    }
}

@Composable
fun ArchitectureCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: String,
    isCode: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SecurityNavyBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SecurityNavyCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = title, tint = SentinelCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isCode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF030712))
                        .padding(10.dp)
                ) {
                    Text(
                        text = content,
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            } else {
                Text(
                    text = content,
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
