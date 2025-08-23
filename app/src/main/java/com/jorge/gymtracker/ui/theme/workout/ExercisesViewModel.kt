package com.jorge.gymtracker.ui.theme.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jorge.gymtracker.core.matchesAllTokens
import com.jorge.gymtracker.data.repository.ExerciseRepository
import com.jorge.gymtracker.domain.model.Exercise
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExercisesViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _all = MutableStateFlow<List<Exercise>>(emptyList())

    val filtered: StateFlow<List<Exercise>> =
        combine(_all, _query) { list, q ->
            if (q.isBlank()) list
            else list.filter { ex ->
                matchesAllTokens(q, listOf(ex.name, ex.primaryMuscle))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun load() {
        if (_all.value.isNotEmpty()) return
        viewModelScope.launch {
            _all.value = repository.getAll()
        }
    }

    fun onQueryChange(q: String) { _query.value = q }
}
