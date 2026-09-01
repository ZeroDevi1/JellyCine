package com.vela.app.ui.navigation

import org.junit.Assert.assertTrue
import org.junit.Test

class NavTransitionsTest {

    @Test
    fun stackSlideIsLargeEnoughToSee() {
        assertTrue(NavTransitions.SLIDE_FRACTION >= 0.25f)
        assertTrue(NavTransitions.DURATION_MS >= 300)
    }

    @Test
    fun popExitSlidesTheFullPageAway() {
        assertTrue(NavTransitions.POP_SLIDE_FRACTION >= 1f)
        assertTrue(NavTransitions.POP_SCALE <= 0.95f)
    }

    @Test
    fun tabEnterScaleIsVisible() {
        assertTrue(NavTransitions.TAB_ENTER_SCALE <= 0.97f)
    }
}
