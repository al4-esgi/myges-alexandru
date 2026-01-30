package com.vlxx.myges.data.dtos

import com.vlxx.myges.domain.entities.UserEntity
import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: UUID,
    val name: String,
    val accessToken: String,
)

fun UserDto.mapToEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        accessToken = accessToken,
    )
}

