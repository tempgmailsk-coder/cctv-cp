package com.example.data.cpplus

enum class CpPlusModelPreset(
    val id: String,
    val displayName: String,
    val defaultHttpPort: Int,
    val defaultRtspPort: Int,
    val rtspPathPattern: String,
    val snapshotPathPattern: String,
    val hasHardwareSMD: Boolean,
    val supportsPtz: Boolean,
    val description: String
) {
    CP_PLUS_ORANGE(
        id = "CP_PLUS_ORANGE",
        displayName = "CP PLUS Orange (DVR/NVR)",
        defaultHttpPort = 80,
        defaultRtspPort = 554,
        rtspPathPattern = "/cam/realmonitor?channel={channel}&subtype={subtype}",
        snapshotPathPattern = "/cgi-bin/snapshot.cgi?channel={channel}",
        hasHardwareSMD = true,
        supportsPtz = true,
        description = "CP PLUS standard Orange series DVR/NVR (gCMOB compatible). Supports hardware Smart Motion & IVS."
    ),
    CP_PLUS_COSMIC(
        id = "CP_PLUS_COSMIC",
        displayName = "CP PLUS Cosmic HDcVI",
        defaultHttpPort = 80,
        defaultRtspPort = 554,
        rtspPathPattern = "/cam/realmonitor?channel={channel}&subtype={subtype}",
        snapshotPathPattern = "/cgi-bin/snapshot.cgi?channel={channel}",
        hasHardwareSMD = true,
        supportsPtz = false,
        description = "High-definition Cosmic analog/digital hybrid DVR series with Smart Person/Vehicle filter."
    ),
    CP_PLUS_INDIGO(
        id = "CP_PLUS_INDIGO",
        displayName = "CP PLUS Indigo Enterprise",
        defaultHttpPort = 80,
        defaultRtspPort = 554,
        rtspPathPattern = "/cam/realmonitor?channel={channel}&subtype={subtype}",
        snapshotPathPattern = "/cgi-bin/snapshot.cgi?channel={channel}",
        hasHardwareSMD = true,
        supportsPtz = true,
        description = "Enterprise-grade NVR with onboard Face Recognition & perimeter defense."
    ),
    CP_PLUS_EZYKAM(
        id = "CP_PLUS_EZYKAM",
        displayName = "CP PLUS Ezykam Wi-Fi",
        defaultHttpPort = 8899,
        defaultRtspPort = 554,
        rtspPathPattern = "/live/ch0",
        snapshotPathPattern = "/webcapture.jpg?command=snap",
        hasHardwareSMD = true,
        supportsPtz = true,
        description = "Standalone wireless smart PTZ camera with two-way audio and motion tracking."
    ),
    ONVIF_GENERIC(
        id = "ONVIF_GENERIC",
        displayName = "Generic ONVIF Profile S/T",
        defaultHttpPort = 80,
        defaultRtspPort = 554,
        rtspPathPattern = "/onvif1",
        snapshotPathPattern = "/onvif/snapshot",
        hasHardwareSMD = false,
        supportsPtz = false,
        description = "Standard ONVIF camera stream. Falls back to app-level intelligent motion analysis."
    );

    companion object {
        fun fromId(id: String): CpPlusModelPreset {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: CP_PLUS_ORANGE
        }
    }
}
