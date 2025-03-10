package com.solarwinds.devthoughts.ui.thought

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import com.solarwinds.devthoughts.data.DevThoughtsDatabase
import com.solarwinds.devthoughts.data.Repository
import com.solarwinds.devthoughts.data.Thought
import com.solarwinds.devthoughts.ui.theme.AppTheme

class HomeFragment : Fragment() {
    private lateinit var repository: Repository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = content {
        repository = Repository.create(DevThoughtsDatabase.getInstance(requireContext()))
        val thoughts: List<Thought> by repository.findAllThoughts().collectAsState(listOf())

        AppTheme {
            if (thoughts.isEmpty()) {
                HomePlaceholder()
            } else {
                DevThoughtsView(thoughts)
            }
        }
    }
}