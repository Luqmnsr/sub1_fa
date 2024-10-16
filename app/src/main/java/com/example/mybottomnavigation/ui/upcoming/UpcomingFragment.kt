package com.example.mybottomnavigation.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mybottomnavigation.adapter.EventAdapter
import com.example.mybottomnavigation.databinding.FragmentUpcomingBinding
import com.example.mybottomnavigation.ui.DetailActivity
import com.example.mybottomnavigation.ui.EventViewModel

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventViewModel: EventViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        eventViewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]

        // Inisialisasi RecyclerView
        setupRecyclerView()

        // Fetch event aktif, hanya jika data belum dimuat
        eventViewModel.fetchUpcomingEvents()

        // Observasi data dari ViewModel
        eventViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events) // Update adapter dengan data terbaru
        }

        // Observasi error message
        eventViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            showError(message)
        }

        // Observasi loading status untuk mengontrol visibilitas ProgressBar
        eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE // Tampilkan ProgressBar
            } else {
                binding.progressBar.visibility = View.GONE // Sembunyikan ProgressBar
            }
        }
    }

    private fun setupRecyclerView() {
        // Inisialisasi Adapter
        eventAdapter = EventAdapter { event ->
            // Intent ke DetailActivity dengan mengirimkan eventId

            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id.toString()) // Mengirimkan eventId sebagai string
            startActivity(intent)
        }

        // Set adapter dan layout manager untuk RecyclerView
        binding.rvEvent.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = eventAdapter
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
