package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.MexcPayDatabase
import com.example.data.repository.MexcPayRepository
import com.example.ui.navigation.MexcPayAppRoot
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MexcPayViewModel
import com.example.ui.viewmodel.MexcPayViewModelFactory

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = MexcPayDatabase.getInstance(applicationContext)
        val repository = MexcPayRepository(database.mexcPayDao())
        val viewModelFactory = MexcPayViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[MexcPayViewModel::class.java]

        setContent {
            MyApplicationTheme {
                MexcPayAppRoot(viewModel = viewModel)
            }
        }
    }
}
