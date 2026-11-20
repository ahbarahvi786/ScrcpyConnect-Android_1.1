package com.bloom404.scrcpyconnect.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.Inet4Address

data class SystemReadiness(
    val developerOptionsEnabled: Boolean = false,
    val usbDebuggingEnabled: Boolean = false,
    val wirelessDebuggingEnabled: Boolean = false,
    val wirelessDebuggingSupported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
)

data class NetworkInfo(
    val isWifiConnected: Boolean = false,
    val ssid: String = "",
    val ipAddress: String = "",
    val hasIp: Boolean = false
)

data class UiState(
    val readiness: SystemReadiness = SystemReadiness(),
    val networkInfo: NetworkInfo = NetworkInfo(),
    val selectedGuide: GuideTab = GuideTab.WIRED,
    val copiedToast: Boolean = false
)

enum class GuideTab { WIRED, WIRELESS }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var periodicJob: Job? = null

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val connectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Suppress("DEPRECATION")
    private val wifiManager =
        application.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    init {
        registerNetworkCallback()
    }

    // ── Public API ──────────────────────────────────────────

    fun refreshSystemChecks() {
        val resolver = getApplication<Application>().contentResolver
        val devOpts = try {
            Settings.Global.getInt(resolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1
        } catch (_: Exception) { false }

        val usbDbg = try {
            Settings.Global.getInt(resolver, Settings.Global.ADB_ENABLED, 0) == 1
        } catch (_: Exception) { false }

        val wirelessDbg = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Settings.Global.getInt(resolver, "adb_wifi_enabled", 0) == 1
            } catch (_: Exception) { false }
        } else false

        _uiState.update { state ->
            state.copy(
                readiness = state.readiness.copy(
                    developerOptionsEnabled = devOpts,
                    usbDebuggingEnabled = usbDbg,
                    wirelessDebuggingEnabled = wirelessDbg
                )
            )
        }
    }

    fun startPeriodicRefresh() {
        if (periodicJob?.isActive == true) return
        periodicJob = viewModelScope.launch {
            while (true) {
                refreshSystemChecks()
                delay(2000L)
            }
        }
    }

    fun selectGuide(tab: GuideTab) {
        _uiState.update { it.copy(selectedGuide = tab) }
    }

    fun showCopiedToast() {
        viewModelScope.launch {
            _uiState.update { it.copy(copiedToast = true) }
            delay(1800L)
            _uiState.update { it.copy(copiedToast = false) }
        }
    }

    // ── Network Monitoring ──────────────────────────────────

    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateNetworkInfo(network)
            }

            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                updateNetworkInfo(network, linkProperties)
            }

            override fun onLost(network: Network) {
                _uiState.update {
                    it.copy(networkInfo = NetworkInfo())
                }
            }

            override fun onUnavailable() {
                _uiState.update {
                    it.copy(networkInfo = NetworkInfo())
                }
            }
        }

        try {
            connectivityManager.registerNetworkCallback(request, networkCallback!!)
        } catch (_: Exception) { }

        // Initial check
        refreshNetworkInfo()
    }

    private fun updateNetworkInfo(network: Network, lp: LinkProperties? = null) {
        val linkProps = lp ?: connectivityManager.getLinkProperties(network)
        val ipv4 = linkProps?.linkAddresses
            ?.map { it.address }
            ?.filterIsInstance<Inet4Address>()
            ?.firstOrNull { !it.isLoopbackAddress }
            ?.hostAddress ?: ""

        val ssid = getCurrentSsid()

        _uiState.update {
            it.copy(
                networkInfo = NetworkInfo(
                    isWifiConnected = true,
                    ssid = ssid,
                    ipAddress = ipv4,
                    hasIp = ipv4.isNotBlank()
                )
            )
        }
    }

    private fun refreshNetworkInfo() {
        val activeNetwork = connectivityManager.activeNetwork
        val caps = activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
        if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            updateNetworkInfo(activeNetwork)
        } else {
            _uiState.update { it.copy(networkInfo = NetworkInfo()) }
        }
    }

    @Suppress("DEPRECATION")
    private fun getCurrentSsid(): String {
        return try {
            val info = wifiManager.connectionInfo
            val raw = info.ssid ?: ""
            raw.removeSurrounding("\"").let {
                if (it == "<unknown ssid>" || it.isBlank()) "Connected Wi-Fi" else it
            }
        } catch (_: Exception) {
            "Connected Wi-Fi"
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkCallback?.let {
            try { connectivityManager.unregisterNetworkCallback(it) } catch (_: Exception) { }
        }
    }
}
