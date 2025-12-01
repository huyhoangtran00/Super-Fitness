package com.example.superfitness.data.location

import android.location.Address
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GeocoderHelperImplTest {

    private val geocoderWrapper: GeocoderWrapper = mockk()
    private val geocoderHelper = GeocoderHelperImpl(geocoderWrapper)

    @Test
    fun `getAddressFromLocation returns address line`() = runTest {
        val lat = 10.0
        val long = 20.0
        val address = mockk<Address>()
        every { address.getAddressLine(0) } returns "Test Address, Country"

        // Mocking the callback behavior for Tiramisu+ logic which is used in the impl
        // Note: The impl checks SDK_INT. For unit tests running on JVM, SDK_INT is usually 0 unless
        // mocked.
        // However, standard unit tests might not easily mock Build.VERSION.SDK_INT without
        // Robolectric or reflection.
        // The impl has a branch. If we can't control SDK_INT, we might hit the 'else' block
        // (legacy).
        // Let's assume we hit the legacy block or we can mock both if possible.
        // But wait, the wrapper abstracts the call.
        // The wrapper implementation checks SDK_INT. The helper implementation ALSO checks SDK_INT?
        // Ah, I see in my refactoring I kept the SDK_INT check in the Helper.
        // And I also put it in the Wrapper?
        // Let's check the Helper code again.
        // Yes, the Helper checks SDK_INT.

        // If we are running on JVM, SDK_INT is 0. So it goes to 'else' block.
        every { geocoderWrapper.getFromLocation(lat, long, 1) } returns listOf(address)

        val result = geocoderHelper.getAddressFromLocation(lat, long)

        assertEquals("Test Address", result)
    }
}
