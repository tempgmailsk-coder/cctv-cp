package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.cpplus.CpPlusModelPreset
import com.example.data.model.Camera
import com.example.data.model.EventType
import com.example.data.model.SecurityEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CP Guard", appName)
    }

    @Test
    fun `test CP PLUS RTSP URL generation`() {
        val camera = Camera(
            name = "Front Gate (CP-PLUS)",
            ipAddress = "192.168.1.108",
            rtspPort = 554,
            channel = 1,
            username = "admin",
            passwordEncrypted = "CpPlus@2026",
            streamType = "MAIN"
        )
        val rtspUrl = camera.buildRtspUrl()
        assertEquals("rtsp://admin:CpPlus@2026@192.168.1.108:554/cam/realmonitor?channel=1&subtype=0", rtspUrl)

        val subStreamCamera = camera.copy(streamType = "SUB")
        val subRtspUrl = subStreamCamera.buildRtspUrl()
        assertEquals("rtsp://admin:CpPlus@2026@192.168.1.108:554/cam/realmonitor?channel=1&subtype=1", subRtspUrl)
    }

    @Test
    fun `test CP PLUS model presets`() {
        val orangePreset = CpPlusModelPreset.fromId("CP_PLUS_ORANGE")
        assertTrue(orangePreset.hasHardwareSMD)
        assertEquals(80, orangePreset.defaultHttpPort)
        assertEquals(554, orangePreset.defaultRtspPort)

        val ezykamPreset = CpPlusModelPreset.fromId("CP_PLUS_EZYKAM")
        assertEquals(8899, ezykamPreset.defaultHttpPort)
    }

    @Test
    fun `test security event buffering metadata`() {
        val event = SecurityEvent(
            cameraId = 1,
            cameraName = "Front Gate",
            eventType = EventType.PERSON_DETECTED,
            clipDurationSeconds = 20
        )
        assertEquals(20, event.clipDurationSeconds)
        assertEquals(EventType.PERSON_DETECTED, event.eventType)
    }
}
