package com.alexis.vigiaruta.data.ubicacion

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class ProveedorUbicacion(private val context: Context) {

    private val cliente = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun ultimaUbicacion(): Location? {
        val permitido = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!permitido) return null

        return try {
            cliente.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }
}
