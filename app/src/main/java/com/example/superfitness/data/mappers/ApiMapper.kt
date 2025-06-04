package com.example.superfitness.data.mappers

interface ApiMapper<Domain, Entity> {
    fun mapToDomain(apiEntity: Entity): Domain
}