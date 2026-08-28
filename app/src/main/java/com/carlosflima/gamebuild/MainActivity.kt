package com.carlosflima.gamebuild

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.carlosflima.gamebuild.data.TermsRepository
import com.carlosflima.gamebuild.ui.GameBuildApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val termsRepository = TermsRepository(applicationContext)
        val initialTerms = termsRepository.loadInitial()

        setContent {
            var terms by remember { mutableStateOf(initialTerms) }

            LaunchedEffect(Unit) {
                termsRepository.refreshFromRemote()?.let { refreshedTerms ->
                    terms = refreshedTerms
                }
            }

            GameBuildApp(terms = terms)
        }
    }
}
