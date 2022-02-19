package com.istir.weebtoolscompose

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class MangaViewZoomedImagesModel : ViewModel() {

    var shouldChangePagesZoomed by mutableStateOf(false)

    var time by mutableStateOf<Long>(0)
    var timeChanged by mutableStateOf<Long>(0)
    var flingDistance by mutableStateOf(0f)
    var isScrolling by mutableStateOf(false)

    var draggingOverboard by mutableStateOf(false)

    var isDraggingSmall by mutableStateOf(false)

    var shouldScrollUpdate by mutableStateOf(true)
        private set

    var listOffset by mutableStateOf(0f)
        private set

    var scrollingHack by mutableStateOf(0)
        private set

    var maxImageOffsetX by mutableStateOf(0f)
    var currentImageOffsetX by mutableStateOf(0f)
    var currentListOffsetX by mutableStateOf(0)

    var overboardOffset by mutableStateOf(0f)

    var updateLaunchedEffectBack by mutableStateOf(0)
    var scrollStartPage by mutableStateOf(0)
    var page by mutableStateOf(0)
    fun refreshScrollingBack() {
//        scrollingHack += 1
        if (updateLaunchedEffectBack >= 10) {
            updateLaunchedEffectBack = 0
        } else {
            updateLaunchedEffectBack += 1
        }
//        if (scrollingHack >= 10) scrollingHack -= 10
    }


    fun refreshScrolling() {
//        scrollingHack += 1
        if (scrollingHack >= 10) {
            scrollingHack = 0
        } else {
            scrollingHack += 1
        }
//        if (scrollingHack >= 10) scrollingHack -= 10
    }

    fun stopScrollFromUpdating() {
        shouldScrollUpdate = false
    }

    fun letScrollUpdate() {
        shouldScrollUpdate = true
    }

    fun refreshScrollIfNeeded() {
        if (shouldScrollUpdate) refreshScrolling()
    }

    fun setScrolling(value: Int) {
        scrollingHack = value
    }

    //    var isScrolling by mutableStateOf(false)
//        private set
//
//    fun setIsScrolling(value: Boolean) {
//        isScrolling = value
//    }
    var emergencyMeeting by mutableStateOf(false)
    fun setOffset(value: Float) {
        listOffset = value
        emergencyMeeting = false
    }

    fun cumulateOffset(value: Float) {
        listOffset += value
    }

    fun resetOffset() {
        listOffset = 0f
        emergencyMeeting = true

    }

    fun clearOffset() {
        listOffset = 0f
    }

    var zoomedImages = mutableStateListOf<Int>()
        private set

    fun addItem(item: Int) {
        zoomedImages.add(item)
    }

    fun removeItem(item: Int) {
        zoomedImages.remove(item)
    }

    //    fun getItem(item: Int): Int {
//        if (zoomedImages.contains(item))
//            return zoomedImages[item]
//        return -1
//    }
    fun contains(item: Int): Boolean {
//    fun getItem(item: Int): Int {
//        if (zoomedImages.contains(item))
//            return true
//        return false
        return zoomedImages.contains(item)
//    }
    }

    fun getItems(): List<Int> {
        return zoomedImages.toList()
    }
}