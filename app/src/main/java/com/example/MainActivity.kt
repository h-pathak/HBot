package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.HBotRepository
import com.example.data.vault.PrivateVaultManager
import com.example.ui.HBotApp
import com.example.ui.HBotViewModel

class MainActivity : ComponentActivity() {

    private val vaultManager by lazy { PrivateVaultManager(applicationContext) }

    private val viewModel by viewModels<HBotViewModel> {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = HBotRepository(initialVaultProfile = vaultManager.vaultProfile.value)
                return HBotViewModel(
                    repository = repository,
                    vaultManager = vaultManager
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HBotApp(viewModel = viewModel)
        }
    }
}
