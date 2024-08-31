package com.example.aplikacijazasportsketerene

import androidx.lifecycle.ViewModel

abstract class SingletonViewModel<T : ViewModel> {
    companion object {
        private val instances: MutableMap<Class<out ViewModel>, ViewModel> = mutableMapOf()
        // treba ocistiti sve instance pri log-outu, postaviti na null i onda ce (VALJDA!!!) Garbage Collector da ih ukloni iz RAM
        // treba resetovati sve ViewModele pri log-outu
        // jos se testira ovaj pristup Singletona

        @Suppress("UNCHECKED_CAST")
        fun <T : ViewModel> getInstance(clazz: Class<T>, factory: () -> T): T {
            return instances.getOrPut(clazz) {
                synchronized(instances) {
                    instances[clazz] ?: factory().also { instances[clazz] = it }
                }
            } as T
        }
    }
}