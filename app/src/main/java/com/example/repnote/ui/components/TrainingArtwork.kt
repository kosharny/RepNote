package com.example.repnote.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.repnote.R

enum class TrainingArtworkKind(@param:DrawableRes val resource: Int) {
    HERO(R.drawable.repnote_workouts_hero),
    HISTORY(R.drawable.repnote_history_empty),
    BUILDER(R.drawable.repnote_workout_builder),
    REST(R.drawable.repnote_rest_timer)
}

@Composable
fun TrainingArtwork(modifier: Modifier = Modifier, kind: TrainingArtworkKind = TrainingArtworkKind.HERO) {
    Box(modifier.clip(MaterialTheme.shapes.large).background(MaterialTheme.colorScheme.surfaceContainerLow)) {
        Image(painterResource(kind.resource), contentDescription = null,
            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
    }
}
