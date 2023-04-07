package com.itmo.services.users.impl.repository

import org.springframework.data.jpa.repository.JpaRepository
import com.itmo.services.users.impl.entity.AppUser
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<AppUser, String>{
    fun findByUsername(username: String): AppUser?
}