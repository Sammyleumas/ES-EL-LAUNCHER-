package com.sllauncher.app.home

import com.sllauncher.app.MainDispatcherRule
import com.sllauncher.app.launcher.FakeDefaultHomeChecker
import com.sllauncher.app.launcher.HomeRoleRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fixedTime = ZonedDateTime.of(2026, 11, 16, 9, 41, 15, 500_000_000, ZoneOffset.UTC)

    @Test
    fun `ticker waits exactly until the next minute boundary`() {
        // 15.5 s into the minute -> 44.5 s until the next minute.
        assertEquals(44_500L, HomeViewModel.millisUntilNextMinute(fixedTime))
    }

    @Test
    fun `ticker at an exact minute waits a full minute`() {
        val onTheMinute = fixedTime.withSecond(0).withNano(0)
        assertEquals(60_000L, HomeViewModel.millisUntilNextMinute(onTheMinute))
    }

    @Test
    fun `ui state exposes current time and default-home status`() = runTest {
        val checker = FakeDefaultHomeChecker(isDefault = false)
        val vm = HomeViewModel(HomeRoleRepository(checker)) { fixedTime }
        vm.uiState.launchIn(backgroundScope)

        assertEquals(fixedTime, vm.uiState.value.now)

        checker.isDefault = true
        vm.refreshDefaultHomeStatus()
        assertTrue(vm.uiState.value.isDefaultHome)
    }
}
