package com.example.superfitness.data.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConnectivityObserverImplTest {

    private val context: Context = mockk(relaxed = true)
    private val connectivityManager: ConnectivityManager = mockk(relaxed = true)
    private val connectivityObserver = ConnectivityObserverImpl(context, connectivityManager)

    @Test
    fun `isConnected emits initial state`() = runTest {
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns
                true

        connectivityObserver.isConnected.test { assertTrue(awaitItem()) }
    }

    @Test
    fun `isConnected emits updates when network becomes available`() = runTest {
        val callbackSlot = slot<ConnectivityManager.NetworkCallback>()
        every { connectivityManager.registerDefaultNetworkCallback(capture(callbackSlot)) } returns
                Unit

        connectivityObserver.isConnected.test {
            awaitItem() // Initial state

            callbackSlot.captured.onAvailable(mockk())
            assertTrue(awaitItem())
        }
    }

    @Test
    fun `isConnected emits updates when network is lost`() = runTest {
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns
                true

        val callbackSlot = slot<ConnectivityManager.NetworkCallback>()
        every { connectivityManager.registerDefaultNetworkCallback(capture(callbackSlot)) } returns
                Unit

        connectivityObserver.isConnected.test {
            assertTrue(awaitItem()) // Initial state is true

            callbackSlot.captured.onLost(mockk())
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `isConnected emits updates when network is unavailable`() = runTest {
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns
                true

        val callbackSlot = slot<ConnectivityManager.NetworkCallback>()
        every { connectivityManager.registerDefaultNetworkCallback(capture(callbackSlot)) } returns
                Unit

        connectivityObserver.isConnected.test {
            assertTrue(awaitItem()) // Initial state is true

            callbackSlot.captured.onUnavailable()
            assertFalse(awaitItem())
        }
    }
}
