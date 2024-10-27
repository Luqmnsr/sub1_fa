package com.example.mybottomnavigation.ui.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mybottomnavigation.databinding.FragmentSettingBinding
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var preference: SettingPreference
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preference = SettingPreference.getInstance(requireContext().dataStore)
        settingViewModel = ViewModelProvider(this, SettingViewModelFactory(preference))[SettingViewModel::class.java]

        // Theme settings observer
        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }

        // Theme switch listener
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        // Notification settings observer
        settingViewModel.getNotificationSetting().observe(viewLifecycleOwner) { isNotificationEnabled ->
            binding.switchDaily.isChecked = isNotificationEnabled
        }

        // Notification switch listener
        binding.switchDaily.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveNotificationSetting(isChecked)
            if (isChecked) {
                startDailyReminder()
            } else {
                cancelDailyReminder()
            }
        }
    }

    private fun startDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminder>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "DailyReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun cancelDailyReminder() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("DailyReminder")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}