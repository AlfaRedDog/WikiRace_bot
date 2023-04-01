package com.itmo.microservices.demo.path.api.model

class PathRequest {
    // конструкторы, геттеры и сеттеры
    private val articleA: String? = null
    private val articleB: String? = null

    // метод, который проверяет валидность объекта
    val isValid: Boolean
        get() = articleA != null && articleB != null && !articleA.isEmpty() && !articleB.isEmpty()
}