package com.example.cloudmate.screens.forecast


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudmate.contracts.IForecastScreenViewModel
import com.example.cloudmate.data.WeatherDbRepository
import com.example.cloudmate.module.CurrentWeatherObject
import com.example.cloudmate.network.common.AppResponse
import com.example.cloudmate.network.weatherapi.Weather
import com.example.cloudmate.network.weatherapi.WeatherApiRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(private val repository: WeatherDbRepository) :
    ViewModel(), IForecastScreenViewModel {
    private val _weatherObjectList = MutableStateFlow<List<CurrentWeatherObject>>(emptyList())
    override val weatherObjectList = _weatherObjectList.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWeatherObjects().distinctUntilChanged()
                .collect { listOfObjects ->
                    if (listOfObjects.isNullOrEmpty()) {
                        Log.d("TAG", "Empty favs")
                    } else {
                        _weatherObjectList.value = listOfObjects
                        Log.d("TAG", "${weatherObjectList.value}")
                    }
                }
        }
    }

    override suspend fun getWeatherById(id: Int): CurrentWeatherObject {
        return repository.getWeatherById(id)
    }

    override fun insertCurrentWeatherObject(currentWeatherObject: CurrentWeatherObject) {
        viewModelScope.launch {
            repository.insertCurrentWeatherObject(currentWeatherObject)
        }
    }

    override fun updateCurrentWeatherObject(currentWeatherObject: CurrentWeatherObject) {
        viewModelScope.launch {
            repository.updateCurrentWeatherObject(currentWeatherObject)
        }
    }
}