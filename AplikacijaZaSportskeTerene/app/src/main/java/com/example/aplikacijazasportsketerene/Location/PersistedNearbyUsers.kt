package com.example.aplikacijazasportsketerene.Location

import com.example.aplikacijazasportsketerene.DataClasses.User

class PersistedNearbyUsers private constructor(){

    companion object{
        private var instance: PersistedNearbyUsers? = null

        fun getClassInstance() : PersistedNearbyUsers{
            return instance ?: synchronized(this){
                return instance ?: PersistedNearbyUsers().also { instance = it }
            }
        }

        var persistedUsers = mutableListOf<User?>(null)
    }

    fun filterAndUpdateList(users: List<User>): List<User>{
        val newUsers = users.filter { user ->
            persistedUsers.none { it?.id == user.id }
        }

        persistedUsers.removeAll { persistedUser ->
            persistedUser != null && users.none { it.id == persistedUser.id }
        }

        persistedUsers.addAll(newUsers)

        return newUsers
    }

    fun reset(){
        persistedUsers = mutableListOf(null)
    }
}