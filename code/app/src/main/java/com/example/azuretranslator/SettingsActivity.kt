package com.example.azuretranslator

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.azuretranslator.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefsManager: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PrefsManager(this)

        // Load current values
        binding.etKey.setText(prefsManager.speechKey)
        binding.etRegion.setText(prefsManager.region)

        binding.btnSave.setOnClickListener {
            val newKey = binding.etKey.text.toString().trim()
            val newRegion = binding.etRegion.text.toString().trim()

            if (newKey.isEmpty() || newRegion.isEmpty()) {
                Toast.makeText(this, "Key and Region cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefsManager.speechKey = newKey
            prefsManager.region = newRegion

            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            
            // Set result so MainActivity knows to refresh
            setResult(RESULT_OK)
            finish()
        }
        
        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
