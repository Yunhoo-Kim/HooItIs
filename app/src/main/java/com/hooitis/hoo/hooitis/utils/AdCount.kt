package com.hooitis.hoo.hooitis.utils


class AdCount{
    var count: Int = 0
    fun addCount() = count++

    companion object {
        private val adObject: AdCount = AdCount()
        fun getInstance(): AdCount{
            return adObject
        }
    }
}