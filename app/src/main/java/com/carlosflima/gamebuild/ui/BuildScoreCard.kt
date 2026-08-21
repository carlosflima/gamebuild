package com.carlosflima.gamebuild.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carlosflima.gamebuild.domain.BuildScore
import kotlin.math.roundToInt

@Composable
fun BuildScoreCard(score: BuildScore) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Build Score: ${score.total}/100", style = MaterialTheme.typography.titleLarge)
            ScoreRow("Cobertura das fontes", score.sourceCoverage)
            ScoreRow("Atualidade", score.freshness)
            ScoreRow("Completude da build", score.buildCompleteness)
            ScoreRow("Disponibilidade F2P", score.f2pAvailability)
            ScoreRow("Concordância das fontes", score.sourceAgreement)
            Text(
                "A pontuação é explicável e considera somente os dados disponíveis no catálogo.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ScoreRow(label: String, value: Int) {
    Text("$label: $value%", style = MaterialTheme.typography.bodyMedium)
    LinearProgressIndicator(
        progress = { (value.coerceIn(0, 100) / 100f).coerceIn(0f, 1f) },
        modifier = Modifier.fillMaxWidth()
    )
}
