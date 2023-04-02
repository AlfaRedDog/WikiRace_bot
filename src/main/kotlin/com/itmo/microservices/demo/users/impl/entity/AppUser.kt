package com.itmo.microservices.demo.users.impl.entity

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class AppUser {

    @Id
    @Column(unique = true)
    var login: String? = null
    var password: String? = null

    constructor()

    constructor(login: String?, password: String?) {
        this.login = login
        this.password = password
    }
}