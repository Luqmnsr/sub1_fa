package com.example.mybottomnavigation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.text.HtmlCompat
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.example.mybottomnavigation.R
import com.example.mybottomnavigation.data.EventRepository
import com.example.mybottomnavigation.data.local.entity.EventEntity
import com.example.mybottomnavigation.data.remote.response.Event
import com.example.mybottomnavigation.databinding.ActivityDetailBinding
import com.example.mybottomnavigation.ui.setting.SettingPreference
import com.example.mybottomnavigation.ui.setting.dataStore

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var settingPreference: SettingPreference
    private lateinit var eventRepository: EventRepository
    private lateinit var eventEntity: EventEntity
    private var isFavorite: Boolean = false

    private val viewModel: EventViewModel by viewModels()
    private var currentEvent: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        binding.btnRegister.setOnClickListener(this)
        val eventId = intent.getStringExtra("EVENT_ID")

        eventRepository = EventRepository(application)

        if (eventId != null) {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.fetchDetailEvent(eventId)
            viewModel.eventDetail.observe(this) { event ->
                binding.progressBar.visibility = View.GONE
                event?.let {
                    currentEvent = it
                    displayEventDetails(it)
                    eventEntity = EventEntity(
                        it.id.toString(),
                        it.name ,
                        it.mediaCover ,
                        System.currentTimeMillis(),
                        it.summary,
                        isFavorite
                    )
                    observeFavoriteStatus(eventId)
                }
            }

            viewModel.errorMessage.observe(this) { message ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        settingPreference = SettingPreference.getInstance(application.dataStore)
        settingPreference.getThemeSetting().asLiveData().observe(this) { isDarkModeActive ->
            AppCompatDelegate.setDefaultNightMode(if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding.btnFavorite.setOnClickListener {
            handleFavoriteClick()
        }
    }

    private fun displayEventDetails(event: Event) {
        binding.apply {
            tvEventName.text = event.name
            tvEventOwner.text = event.ownerName
            tvEventTime.text = event.beginTime
            tvDescription.text = HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            tvRemainingQuota.text = getString(R.string.remaining_quota_label, (event.quota) - (event.registrants))
            supportActionBar?.title = event.name
            Glide.with(this@DetailActivity)
                .load(event.mediaCover)
                .into(imgMediaCover)
        }
    }

    private fun observeFavoriteStatus(eventId: String) {
        eventRepository.getFavoriteEventById(eventId).observe(this) { favoriteEvent ->
            isFavorite = favoriteEvent != null
            updateFavoriteIcon(isFavorite)
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        binding.btnFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite
            else R.drawable.baseline_favorite_border_24)
    }

    private fun handleFavoriteClick() {
        if (isFavorite) {
            eventRepository.deleteFavorite(eventEntity)
        } else {
            eventRepository.insertFavorite(eventEntity)
        }
    }

    override fun onClick(v: View) {
        if (v.id == binding.btnRegister.id) {
            currentEvent?.link?.let { url ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } ?: Toast.makeText(this, "Registration URL not available", Toast.LENGTH_SHORT).show()
        }
    }

    //BackButton
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}