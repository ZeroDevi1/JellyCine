package com.vela.app.player.mpv

import org.junit.Assert.assertEquals
import org.junit.Test

class MpvWarmDecisionTest {

    @Test
    fun skipsWarmupWhenEngineIsNotMpv() {
        assertEquals(
            MpvWarmDecision.SkipNotMpv,
            decideMpvWarmup(
                engineIsMpv = false,
                hasLiveController = false,
                alreadyWarmOrWarming = false
            )
        )
    }

    @Test
    fun skipsWarmupWhilePipOrFullscreenPlaybackHoldsMpv() {
        assertEquals(
            MpvWarmDecision.SkipLivePlayback,
            decideMpvWarmup(
                engineIsMpv = true,
                hasLiveController = true,
                alreadyWarmOrWarming = false
            )
        )
    }

    @Test
    fun skipsWarmupWhenPoolAlreadyHasMatchingInstance() {
        assertEquals(
            MpvWarmDecision.SkipAlreadyWarm,
            decideMpvWarmup(
                engineIsMpv = true,
                hasLiveController = false,
                alreadyWarmOrWarming = true
            )
        )
    }

    @Test
    fun startsWarmupOnlyWhenMpvIsIdle() {
        assertEquals(
            MpvWarmDecision.StartWarmup,
            decideMpvWarmup(
                engineIsMpv = true,
                hasLiveController = false,
                alreadyWarmOrWarming = false
            )
        )
    }

    @Test
    fun livePlaybackWinsOverIdleWarmSlot() {
        assertEquals(
            MpvWarmDecision.SkipLivePlayback,
            decideMpvWarmup(
                engineIsMpv = true,
                hasLiveController = true,
                alreadyWarmOrWarming = true
            )
        )
    }
}
