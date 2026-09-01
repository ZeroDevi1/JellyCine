package com.vela.app.ui.screens.dashboard.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LibraryImageRequestTest {

    @Test
    fun posterRequestsHighResJpeg() {
        val (width, height, quality) = LibraryImageRequest.dimensions("Primary", "Primary")
        assertEquals(LibraryImageRequest.POSTER_WIDTH, width)
        assertEquals(LibraryImageRequest.POSTER_HEIGHT, height)
        assertEquals(LibraryImageRequest.POSTER_QUALITY, quality)
    }

    @Test
    fun landscapeFallbackKeepsLandscapeSize() {
        val (width, height, quality) = LibraryImageRequest.dimensions("Primary", "Thumb")
        assertEquals(LibraryImageRequest.LANDSCAPE_WIDTH, width)
        assertEquals(LibraryImageRequest.LANDSCAPE_HEIGHT, height)
        assertEquals(LibraryImageRequest.LANDSCAPE_QUALITY, quality)
    }

    @Test
    fun bannerUsesWideFrame() {
        val (width, height, quality) = LibraryImageRequest.dimensions("Banner", "Banner")
        assertEquals(LibraryImageRequest.BANNER_WIDTH, width)
        assertEquals(LibraryImageRequest.BANNER_HEIGHT, height)
        assertEquals(LibraryImageRequest.BANNER_QUALITY, quality)
    }

    @Test
    fun posterIsSharpEnoughForXxxhdpiGrid() {
        assertTrue(LibraryImageRequest.POSTER_WIDTH >= 520)
        assertTrue(LibraryImageRequest.POSTER_QUALITY >= 90)
    }
}
