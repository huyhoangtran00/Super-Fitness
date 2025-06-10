package com.example.superfitness.ui.screens.running_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superfitness.data.local.entity.RunEntity
import com.example.superfitness.domain.repository.RunRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RunDetailsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val runRepository: RunRepository
) : ViewModel() {

    val runItemId = checkNotNull(savedStateHandle.get<Int>(RunDetailsDestination.runItemIdArg))

    val runItem: StateFlow<RunDetails> =
        runRepository.getRunStream(runItemId)
            .filterNotNull()
            .map {
                it.toRunDetails()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = RunDetails()
            )

    /**
     * Delete a run from database
     */
    fun deleteRun(runEntity: RunEntity) {
        viewModelScope.launch {
            runRepository.removeRun(runEntity)
        }
    }
}

data class RunDetails(
    val id: Int = 0,
    val timeStamp: Long = 0L,
    val distance: Int = 0,
    val duration: Long = 0L,
    val pathPoints: String = "",
    val steps: Long = 0L
)

fun RunDetails.toRunEntity(): RunEntity = RunEntity(
    id = id,
    timeStamp = timeStamp,
    distance = distance,
    duration = duration,
    pathPoints = pathPoints,
    steps = steps
)

fun RunEntity.toRunDetails(): RunDetails {
    return RunDetails(
        id = id,
        timeStamp = timeStamp,
        distance = distance,
        duration = duration,
        pathPoints = pathPoints,
        steps = steps
    )
}
