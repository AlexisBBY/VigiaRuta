package com.alexis.vigiaruta.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.alexis.vigiaruta.data.EstadoMonitoreo
import com.alexis.vigiaruta.data.MonitoreoRepositorio
import com.alexis.vigiaruta.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MonitoreoViewModel(
    private val repositorio: MonitoreoRepositorio
) : ViewModel() {

    val estado: StateFlow<EstadoMonitoreo> = repositorio.estado

    private val _modoSimulacion = MutableStateFlow(ServiceLocator.modoSimulacion)
    val modoSimulacion: StateFlow<Boolean> = _modoSimulacion.asStateFlow()

    fun cambiarModoSimulacion() {
        ServiceLocator.modoSimulacion = !ServiceLocator.modoSimulacion
        _modoSimulacion.value = ServiceLocator.modoSimulacion
    }

    fun onCirculoTocado() = repositorio.registrarReaccion()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { MonitoreoViewModel(ServiceLocator.monitoreoRepositorio) }
        }
    }
}
