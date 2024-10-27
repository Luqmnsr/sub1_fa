package com.example.mybottomnavigation.ui.finished

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mybottomnavigation.adapter.EventAdapter
import com.example.mybottomnavigation.databinding.FragmentFinishedBinding
import com.example.mybottomnavigation.ui.DetailActivity
import com.example.mybottomnavigation.ui.EventViewModel
import com.example.mybottomnavigation.ui.setting.SettingPreference
import com.example.mybottomnavigation.ui.setting.dataStore

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventViewModel: EventViewModel
    private lateinit var eventAdapter: EventAdapter
    private lateinit var settingPreference: SettingPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        // Initialize RecyclerView
        setupRecyclerView()

        // Observe finished events list
        eventViewModel.listEvent.observe(viewLifecycleOwner) { eventList ->
            eventAdapter.submitList(eventList)
            updateUi(eventList.isEmpty())
        }

        // Observe error message
        eventViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!eventViewModel.isFinishedEventsLoaded) {  // Only show error if data not loaded
                showError(message)
            }
        }

        // Observe loading status
        eventViewModel.isFinishedLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        // Setup search functionality
        setupSearchBar()

        // Load finished events if they have not been loaded before
        if (!eventViewModel.isFinishedEventsLoaded) {
            eventViewModel.loadFinishedEvents()
        }

        settingPreference = SettingPreference.getInstance(requireContext().dataStore)

        settingPreference.getThemeSetting().asLiveData()
            .observe(viewLifecycleOwner) { isDarkModeActive ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
    }

    private fun setupSearchBar() {
        try {
            binding.searchView.setupWithSearchBar(binding.searchBar)
            Log.d("FinishedFragment", "SearchView connected to SearchBar")

            binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
                val query = binding.searchView.text.toString()
                Log.d("FinishedFragment", "Search query: $query")
                if (query.isNotEmpty()) {
                    eventViewModel.searchFinishedEvents(query)
                } else {
                    eventViewModel.loadFinishedEvents()
                }
                binding.searchBar.setText(binding.searchView.text)
                binding.searchView.hide()
                false
            }
        } catch (e: Exception) {
            Log.e("FinishedFragment", "Error in setupSearchBar: ${e.message}")
        }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { event ->
            // Handle event click to show event details
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id.toString()) // Mengirimkan eventId sebagai string
            startActivity(intent)
        }
        binding.rvEvent.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = eventAdapter
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun updateUi(isEmpty: Boolean) {
        if (isEmpty) {
            binding.rvEvent.visibility = View.GONE
        } else {
            binding.rvEvent.visibility = View.VISIBLE
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}