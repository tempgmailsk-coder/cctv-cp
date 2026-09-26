package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.cpplus.CpPlusClient
import com.example.data.cpplus.CpPlusModelPreset
import com.example.data.model.Camera
import com.example.data.model.EventType
import com.example.data.model.KnownPerson
import com.example.data.model.SecurityEvent
import com.example.data.repository.CctvRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CctvViewModel(
    private val repository: CctvRepository
) : ViewModel() {

    val preferences = repository.preferences

    // Cameras list
    val cameras: StateFlow<List<Camera>> = repository.allCameras
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All events
    val allEvents: StateFlow<List<SecurityEvent>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentEvents: StateFlow<List<SecurityEvent>> = repository.recentEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Known persons
    val knownPersons: StateFlow<List<KnownPerson>> = repository.allKnownPersons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // System armed status
    private val _isSystemArmed = MutableStateFlow(preferences.isSystemArmed)
    val isSystemArmed: StateFlow<Boolean> = _isSystemArmed.asStateFlow()

    // Selected camera for live feed / PTZ
    private val _selectedCamera = MutableStateFlow<Camera?>(null)
    val selectedCamera: StateFlow<Camera?> = _selectedCamera.asStateFlow()

    // Selected event for video playback
    private val _selectedEvent = MutableStateFlow<SecurityEvent?>(null)
    val selectedEvent: StateFlow<SecurityEvent?> = _selectedEvent.asStateFlow()

    // Test connection state
    private val _testConnectionResult = MutableStateFlow<CpPlusClient.ConnectionResult?>(null)
    val testConnectionResult: StateFlow<CpPlusClient.ConnectionResult?> = _testConnectionResult.asStateFlow()

    private val _isTestingConnection = MutableStateFlow(false)
    val isTestingConnection: StateFlow<Boolean> = _isTestingConnection.asStateFlow()

    // Filter states for Event History
    val filterCameraId = MutableStateFlow<Long?>(null)
    val filterEventType = MutableStateFlow<EventType?>(null)
    val filterQuery = MutableStateFlow("")
    val filterBookmarkedOnly = MutableStateFlow(false)

    // Filtered events flow
    val filteredEvents: StateFlow<List<SecurityEvent>> = combine(
        allEvents,
        filterCameraId,
        filterEventType,
        filterQuery,
        filterBookmarkedOnly
    ) { events, camId, type, query, bookmarkedOnly ->
        events.filter { event ->
            (camId == null || event.cameraId == camId) &&
            (type == null || event.eventType == type) &&
            (!bookmarkedOnly || event.isBookmarked) &&
            (query.isEmpty() ||
                event.cameraName.contains(query, ignoreCase = true) ||
                (event.personName ?: "").contains(query, ignoreCase = true) ||
                event.eventType.displayName.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Toast/Snackbar notifications
    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage: SharedFlow<String> = _uiMessage.asSharedFlow()

    // App Security Lock state
    private val _isAuthenticated = MutableStateFlow(!preferences.isPinLockEnabled)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun selectCamera(camera: Camera?) {
        _selectedCamera.value = camera
    }

    fun selectEvent(event: SecurityEvent?) {
        _selectedEvent.value = event
    }

    fun selectEventById(eventId: Long) {
        viewModelScope.launch {
            val event = repository.getEventById(eventId)
            _selectedEvent.value = event
        }
    }

    fun toggleSystemArmed() {
        viewModelScope.launch {
            val newArmed = !_isSystemArmed.value
            _isSystemArmed.value = newArmed
            repository.setAllArmed(newArmed)
            _uiMessage.emit(if (newArmed) "Surveillance system ARMED" else "Surveillance system DISARMED")
        }
    }

    fun toggleCameraArmed(camera: Camera) {
        viewModelScope.launch {
            val updated = !camera.isArmed
            repository.toggleCameraArmed(camera.id, updated)
            _uiMessage.emit("${camera.name} ${if (updated) "Armed" else "Disarmed"}")
        }
    }

    fun toggleBookmark(event: SecurityEvent) {
        viewModelScope.launch {
            repository.toggleBookmark(event)
        }
    }

    fun deleteEvent(event: SecurityEvent) {
        viewModelScope.launch {
            repository.deleteEvent(event)
            _uiMessage.emit("Event deleted")
        }
    }

    fun clearAllEvents() {
        viewModelScope.launch {
            repository.clearAllEvents()
            _uiMessage.emit("All event recordings cleared")
        }
    }

    fun saveCamera(camera: Camera, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.saveCamera(camera)
            _uiMessage.emit("Camera '${camera.name}' saved")
            onComplete()
        }
    }

    fun deleteCamera(camera: Camera, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCamera(camera)
            _uiMessage.emit("Camera '${camera.name}' removed")
            onComplete()
        }
    }

    fun testConnection(camera: Camera) {
        viewModelScope.launch {
            _isTestingConnection.value = true
            _testConnectionResult.value = null
            val result = repository.testCameraConnection(camera)
            _testConnectionResult.value = result
            _isTestingConnection.value = false
        }
    }

    fun clearConnectionTestResult() {
        _testConnectionResult.value = null
    }

    fun sendPtz(direction: String) {
        val cam = _selectedCamera.value ?: return
        viewModelScope.launch {
            repository.sendPtz(cam, direction)
            _uiMessage.emit("PTZ: $direction")
        }
    }

    fun triggerTestEvent(eventType: EventType, camera: Camera? = null) {
        viewModelScope.launch {
            val targetCam = camera ?: _selectedCamera.value ?: cameras.value.firstOrNull()
            if (targetCam != null) {
                val event = repository.triggerDetectionEvent(targetCam.id, eventType)
                _uiMessage.emit("Alert triggered: ${event.eventType.displayName}")
            }
        }
    }

    fun saveKnownPerson(person: KnownPerson, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.saveKnownPerson(person)
            _uiMessage.emit("Registered ${person.name}")
            onComplete()
        }
    }

    fun deleteKnownPerson(person: KnownPerson) {
        viewModelScope.launch {
            repository.deleteKnownPerson(person)
            _uiMessage.emit("Removed ${person.name}")
        }
    }

    fun unlockWithPin(pin: String): Boolean {
        return if (pin == preferences.securityPin) {
            _isAuthenticated.value = true
            true
        } else {
            false
        }
    }

    fun lockApp() {
        if (preferences.isPinLockEnabled) {
            _isAuthenticated.value = false
        }
    }

    fun cleanupOldStorage() {
        viewModelScope.launch {
            val deletedCount = repository.cleanupOldEvents()
            _uiMessage.emit("Cleaned up $deletedCount expired clips")
        }
    }
}
