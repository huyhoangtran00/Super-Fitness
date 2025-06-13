package com.example.superfitness.domain.usecases.run

data class RunUseCases (
    val getAllRuns: GetAllRuns,
    val getRun: GetRun,
    val deleteRun: DeleteRun,
    val addRun: AddRun
)