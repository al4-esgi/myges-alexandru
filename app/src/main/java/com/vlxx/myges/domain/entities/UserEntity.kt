package com.vlxx.myges.domain.entities

import java.util.UUID

data class UserEntity(
    val id: UUID,
    val name: String,
    val accessToken: String,
)