package com.vela.data.network

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SavedServerMatchTest {

    @Test
    fun sameUserAndUrlReusesSavedServer() {
        assertTrue(
            matchesSavedServerIdentity(
                existingUserId = "user-a",
                existingServerUrl = "http://jellyfin.local:8096/",
                incomingUserId = "user-a",
                incomingServerUrl = "http://jellyfin.local:8096"
            )
        )
    }

    @Test
    fun sameUsernameDifferentServersStaySeparate() {
        assertFalse(
            matchesSavedServerIdentity(
                existingUserId = "user-jellyfin",
                existingServerUrl = "http://192.168.1.10:8096",
                incomingUserId = "user-dxp",
                incomingServerUrl = "http://dxp4800plus:8096"
            )
        )
    }

    @Test
    fun sameUserIdDifferentUrlsStaySeparate() {
        assertFalse(
            matchesSavedServerIdentity(
                existingUserId = "user-a",
                existingServerUrl = "http://192.168.1.10:8096",
                incomingUserId = "user-a",
                incomingServerUrl = "http://dxp4800plus:8096"
            )
        )
    }

    @Test
    fun blankUserIdDoesNotMatch() {
        assertFalse(
            matchesSavedServerIdentity(
                existingUserId = "",
                existingServerUrl = "http://jellyfin.local:8096",
                incomingUserId = "",
                incomingServerUrl = "http://jellyfin.local:8096"
            )
        )
    }
}
