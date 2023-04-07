package com.itmo.services.users.impl.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class AppUser {

    @Id
    @Column(unique = true)
    var username: String? = null
    var password: String? = null

    constructor()

    constructor(username: String?, password: String?) {
        this.username = username
        this.password = password
    }
}