package com.bloom404.scrcpyconnect.adb

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.conscrypt.Conscrypt
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.security.KeyStore
import java.security.SecureRandom
import java.security.Security
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/**
 * Lightweight ADB-over-Wi-Fi client for ScrcpyConnect.
 *
 * Uses Conscrypt as the TLS provider for Android-compatible
 * TLS 1.3 support required by ADB wireless pairing (Android 11+).
 */
object AdbClient {

    private const val TAG = "AdbClient"
    private const val DEFAULT_ADB_PORT = 5555
    private const val CONNECT_TIMEOUT_MS = 5_000
    private const val READ_TIMEOUT_MS = 10_000

    init {
        // Insert Conscrypt as the highest-priority TLS provider
        val provider = Conscrypt.newProviderBuilder().build()
        Security.insertProviderAt(provider, 1)
        Log.d(TAG, "Conscrypt TLS provider installed: ${provider.name} ${provider.version}")
    }

    /**
     * Tests whether a raw TCP connection to the given ADB endpoint succeeds.
     *
     * @param host  IP address of the target device
     * @param port  ADB port (default 5555)
     * @return `true` if the socket connected successfully
     */
    suspend fun testConnection(
        host: String,
        port: Int = DEFAULT_ADB_PORT
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            Socket().use { socket ->
                socket.connect(
                    InetSocketAddress(host, port),
                    CONNECT_TIMEOUT_MS
                )
                socket.soTimeout = READ_TIMEOUT_MS
                socket.isConnected
            }
        } catch (e: IOException) {
            Log.w(TAG, "Connection to $host:$port failed: ${e.message}")
            false
        }
    }

    /**
     * Creates a Conscrypt-backed [SSLContext] suitable for ADB TLS pairing.
     *
     * The returned context uses the platform default trust store and a
     * freshly generated ephemeral key pair, matching the behaviour
     * expected by `adb pair`.
     */
    fun createTlsContext(): SSLContext {
        val trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        ).apply {
            init(null as KeyStore?)
        }

        val keyManagerFactory = KeyManagerFactory.getInstance(
            KeyManagerFactory.getDefaultAlgorithm()
        ).apply {
            init(null, null)
        }

        return SSLContext.getInstance("TLS", "Conscrypt").apply {
            init(
                keyManagerFactory.keyManagers,
                trustManagerFactory.trustManagers,
                SecureRandom()
            )
        }
    }
}
