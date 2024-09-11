package com.example.aplikacijazasportsketerene.UserInterface.Map

import androidx.lifecycle.ViewModel



class MapDrawerViewModel private constructor(): ViewModel() {

    companion object {
        private var instance: MapDrawerViewModel? = null

        fun getClassInstance(): MapDrawerViewModel {

            return instance ?: synchronized(this) {
                return instance ?: MapDrawerViewModel().also { instance = it }
            }
        }
    }
}