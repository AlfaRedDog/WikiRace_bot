package com.itmo.services.users.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.services.common.exception.NotFoundException
import com.itmo.services.users.api.messaging.UserCreatedEvent
import com.itmo.services.users.api.service.UserService
import com.itmo.services.users.impl.entity.AppUser
import com.itmo.services.users.api.model.AppUserModel
import com.itmo.services.users.api.model.RegistrationRequest
import com.itmo.services.users.impl.logging.UserServiceNotableEvents
import com.itmo.services.users.impl.repository.UserRepository
import com.itmo.services.users.impl.util.toModel
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Suppress("UnstableApiUsage")
@Service
class DefaultUserService(private val userRepository: UserRepository,
                         private val passwordEncoder: PasswordEncoder,
                         private val eventBus: EventBus
                         ): UserService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun findUser(username: String): AppUserModel? = userRepository
            .findByUsername(username)?.toModel()

    override fun registerUser(request: RegistrationRequest) {
        val userEntity = userRepository.save(request.toEntity())
        eventBus.post(UserCreatedEvent(userEntity.toModel()))
        eventLogger.info(UserServiceNotableEvents.I_USER_CREATED, userEntity.username)
    }

    override fun getAccountData(requester: UserDetails): AppUserModel =
            userRepository.findByIdOrNull(requester.username)?.toModel() ?:
            throw NotFoundException("User ${requester.username} not found")


    fun RegistrationRequest.toEntity(): AppUser =
        AppUser(username = this.username,
            password = passwordEncoder.encode(this.password)
        )
}
