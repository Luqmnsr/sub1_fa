package com.example.mybottomnavigation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.mybottomnavigation.R
import com.example.mybottomnavigation.data.response.Event

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var imgMediaCover: ImageView
    private lateinit var tvEventName: TextView
    private lateinit var tvEventTime: TextView
    private lateinit var tvEventOwner: TextView
    private lateinit var tvRemainingQuota: TextView
    private lateinit var tvEventDescription: TextView
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar

    private val viewModel: EventViewModel by viewModels()
    private var currentEvent: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        imgMediaCover = findViewById(R.id.imgMediaCover)
        tvEventName = findViewById(R.id.tvEventName)
        tvEventTime = findViewById(R.id.tvEventTime)
        tvEventOwner = findViewById(R.id.tvEventOwner)
        tvRemainingQuota = findViewById(R.id.tvRemainingQuota)
        tvEventDescription = findViewById(R.id.tvDescription)
        progressBar = findViewById(R.id.progressBar)

        btnRegister = findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener(this)

        // Terima data ID event dari intent
        val eventId = intent.getStringExtra("EVENT_ID")

        // Panggil API untuk mendapatkan detail event
        if (eventId != null) {
            // Tampilkan ProgressBar saat mulai memuat data
            progressBar.visibility = View.VISIBLE

            // Fetch detail event
            viewModel.fetchDetailEvent(eventId)

            // Observe the event detail LiveData
            viewModel.eventDetail.observe(this) { event ->
                progressBar.visibility = View.GONE
                event?.let {
                    currentEvent = it
                    displayEventDetails(it)

                }

                // Observe error messages
                viewModel.errorMessage.observe(this) { message ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
        private fun displayEventDetails(event: Event) {
            // Update TextView with event details
            tvEventName.text = event.name
            tvEventOwner.text = event.ownerName
            tvEventTime.text = event.beginTime

            // Use HtmlCompat to set the description
            tvEventDescription.text =
                HtmlCompat.fromHtml(event.description.orEmpty(), HtmlCompat.FROM_HTML_MODE_LEGACY)

            // Calculate remaining quota
            val remainingQuota = event.quota?.minus(event.registrants ?: 0) ?: 0
            tvRemainingQuota.text = getString(R.string.remaining_quota_label, remainingQuota)

            // Update ActionBar title with event name
            supportActionBar?.title = event.name

            // Load event image using Glide
            Glide.with(this)
                .load(event.mediaCover ?: event.imageLogo)
                .into(imgMediaCover)
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.btnRegister -> {
                    val url = currentEvent?.link
                    if (url != null) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Registration URL not available", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    // Handle back button in ActionBar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}