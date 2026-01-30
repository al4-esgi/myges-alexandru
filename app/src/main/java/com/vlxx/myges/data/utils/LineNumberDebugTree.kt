package com.vlxx.myges.data.utils

import timber.log.Timber

class LineNumberDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return "${super.createStackElementTag(element)}:${element.lineNumber}"
    }
}