package org.babetech.borastock.ui.screens.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.babetech.borastock.data.models.ChartData
import org.babetech.borastock.domain.usecase.GetChartDataUseCase

class GraphicViewModel(
    private val getChartDataUseCase: GetChartDataUseCase
) : ViewModel() {

    private val _chartData = MutableStateFlow<ChartData?>(null)
    val chartData: StateFlow<ChartData?> = _chartData.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadChartData()
        observeChartData()
    }

    private fun loadChartData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val data = getChartDataUseCase()
                _chartData.value = data
            } catch (e: Exception) {
                // Handle error
                _chartData.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun observeChartData() {
        viewModelScope.launch {
            getChartDataUseCase.getChartDataFlow().collect { data ->
                _chartData.value = data
            }
        }
    }

    fun refreshData() {
        loadChartData()
    }
}