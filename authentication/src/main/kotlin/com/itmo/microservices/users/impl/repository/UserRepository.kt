package com.itmo.microservices.users.impl.repository

import com.itmo.microservices.users.impl.entity.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<AppUser, String> {
    fun findByLogin(login: String): AppUser?
}