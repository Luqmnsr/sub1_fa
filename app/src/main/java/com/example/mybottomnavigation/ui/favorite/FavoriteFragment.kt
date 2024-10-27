package com.example.mybottomnavigation.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mybottomnavigation.adapter.EventAdapter
import com.example.mybottomnavigation.data.EventRepository
import com.example.mybottomnavigation.data.remote.response.ListEventsItem
import com.example.mybottomnavigation.databinding.FragmentFavoriteBinding

import com.example.mybottomnavigation.ui.DetailActivity
import com.example.mybottomnavigation.ui.setting.SettingPreference
import com.example.mybottomnavigation.ui.setting.dataStore

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: EventAdapter
    private lateinit var settingPreference: SettingPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize repository and ViewModel using factory
        val repository = EventRepository(requireActivity().application)
        val factory = FavoriteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        adapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id.toString())
            startActivity(intent)
        }

        // Setup RecyclerView
        binding.rvEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoriteFragment.adapter
        }

        // Observe loading state and show progress bar if loading
        viewModel.isFavoriteLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("FavoriteFragment", "Loading state: $isLoading")
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvEvent.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        // Observe favoriteEvents to display data
        viewModel.favoriteEvents.observe(viewLifecycleOwner) { favoriteEvents ->
            if (favoriteEvents.isNullOrEmpty()) {
                showNoFavoriteMessage()
            } else {
                val items = favoriteEvents.mapNotNull { event ->
                    event.id.toIntOrNull()?.let { id ->
                        ListEventsItem(
                            id = id,
                            name = event.name,
                            imageLogo = event.mediaCover ?: "",
                            summary = event.summary ?: "No description available"
                        )
                    }
                }
                adapter.submitList(items)
                binding.noFavoriteMessage.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                showError(message)
            }
        }

        settingPreference = SettingPreference.getInstance(requireContext().dataStore)
        settingPreference.getThemeSetting().asLiveData()
            .observe(viewLifecycleOwner) { isDarkModeActive ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
    }

    private fun showNoFavoriteMessage() {
        binding.noFavoriteMessage.visibility = View.VISIBLE
        binding.rvEvent.visibility = View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}