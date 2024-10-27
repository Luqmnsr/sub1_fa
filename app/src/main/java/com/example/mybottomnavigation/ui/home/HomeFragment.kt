package com.example.mybottomnavigation.ui.home

import android.content.Intent
import android.os.Bundle
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
import com.example.mybottomnavigation.databinding.FragmentHomeBinding
import com.example.mybottomnavigation.ui.DetailActivity
import com.example.mybottomnavigation.ui.EventViewModel
import com.example.mybottomnavigation.ui.setting.SettingPreference
import com.example.mybottomnavigation.ui.setting.dataStore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var upcomingAdapter: EventAdapter
    private lateinit var finishedAdapter: EventAdapter
    private lateinit var eventViewModel: EventViewModel
    private lateinit var settingPreference: SettingPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeViewModel()

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

    private fun setupRecyclerViews() {
        // Setup Upcoming Events RecyclerView dengan orientasi horizontal
        upcomingAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id.toString()) // Mengirimkan eventId sebagai string
            startActivity(intent)
        }
        binding.rvUpcomingEvents.apply {
            adapter = upcomingAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        // Setup Finished Events RecyclerView dengan orientasi vertikal
        finishedAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id.toString()) // Mengirimkan eventId sebagai string
            startActivity(intent)
        }
        binding.rvFinishedEvents.apply {
            adapter = finishedAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun observeViewModel() {
        // Observe upcoming events
        eventViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNullOrEmpty()) {
                binding.progressBarUpcoming.visibility = View.GONE
                Toast.makeText(requireContext(), "No upcoming events available", Toast.LENGTH_SHORT).show()
            } else {
                binding.progressBarUpcoming.visibility = View.GONE
                val limitedUpcomingEvents = if (events.size > 5) events.subList(0, 5) else events
                upcomingAdapter.submitList(limitedUpcomingEvents) // Menampilkan data di adapter
            }
        }

        // Observe finished events
        eventViewModel.listEvent.observe(viewLifecycleOwner) { events ->
            if (events.isNullOrEmpty()) {
                binding.progressBarFinished.visibility = View.GONE
                Toast.makeText(requireContext(), "No finished events available", Toast.LENGTH_SHORT).show()
            } else {
                binding.progressBarFinished.visibility = View.GONE
                val limitedFinishedEvents = if (events.size > 5) events.subList(0, 5) else events
                finishedAdapter.submitList(limitedFinishedEvents) // Menampilkan data di adapter
            }
        }

        // Handle error messages
        eventViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            // Tampilkan error ke user jika perlu
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // Observe loading status secara umum
        eventViewModel.isFinishedLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBarFinished.visibility = View.VISIBLE
            } else {
                binding.progressBarFinished.visibility = View.GONE
            }
        }
        eventViewModel.isUpcomingLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBarUpcoming.visibility = View.VISIBLE
            } else {
                binding.progressBarUpcoming.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}