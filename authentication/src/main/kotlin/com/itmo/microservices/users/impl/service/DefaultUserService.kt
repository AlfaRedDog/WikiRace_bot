package com.itmo.microservices.users.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.common.exception.NotFoundException
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.users.api.messaging.UserCreatedEvent
import com.itmo.microservices.users.api.model.AppUserModel
import com.itmo.microservices.users.api.model.RegistrationRequest
import com.itmo.microservices.users.api.service.UserService
import com.itmo.microservices.users.impl.entity.AppUser
import com.itmo.microservices.users.impl.logging.UserServiceNotableEvents
import com.itmo.microservices.users.impl.repository.UserRepository
import com.itmo.microservices.users.impl.util.toModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Suppress("UnstableApiUsage")
@Service
class DefaultUserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val eventBus: EventBus
) : UserService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun findUser(username: String): AppUserModel? = userRepository
        .findByLogin(username)?.toModel()

    override fun registerUser(request: RegistrationRequest) {
        val userEntity = userRepository.save(request.toEntity())
        eventBus.post(UserCreatedEvent(userEntity.toModel()))
        eventLogger.info(UserServiceNotableEvents.I_USER_CREATED, userEntity.login)
    }

    override fun getAccountData(requester: UserDetails): AppUserModel =
        userRepository.findByIdOrNull(requester.username)?.toModel()
            ?: throw NotFoundException("User ${requester.username} not found")


    fun RegistrationRequest.toEntity(): AppUser =
        AppUser(
            login = this.login,
            password = passwordEncoder.encode(this.password)
        )
}
