package com.example.superfitness.utils

import android.content.Context

interface PermissionChecker {
    fun hasLocationPermission(): Boolean
}

class PermissionCheckerImpl(private val context: Context) : PermissionChecker {
    override fun hasLocationPermission(): Boolean {
        return context.hasLocationPermission()
    }
}
