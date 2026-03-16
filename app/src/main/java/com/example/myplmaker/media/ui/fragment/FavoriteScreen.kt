package com.example.myplmaker.media.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.example.myplmaker.R
import com.example.myplmaker.media.ui.model.FavoritesState
import com.example.myplmaker.media.ui.view.FavoriteViewModel
import com.example.myplmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

private val YsDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))
private val YsDisplayRegular = FontFamily(Font(R.font.ys_display_regular, FontWeight.Normal))

@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel,
    onTrackClick: (Track) -> Unit
) {
    val state by viewModel.state.observeAsState(FavoritesState.Empty)

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadFavoriteTracks()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when (val currentState = state) {
        is FavoritesState.Content -> {
            FavoriteContent(
                tracks = currentState.tracks,
                onTrackClick = onTrackClick
            )
        }

        is FavoritesState.Empty -> {
            FavoriteEmpty()
        }
    }
}

@Composable
private fun FavoriteContent(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = dimensionResource(R.dimen.eight_dp))
    ) {
        items(tracks) { track ->
            FavoriteTrackItem(
                track = track,
                onClick = { onTrackClick(track) }
            )
        }
    }
}

@Composable
private fun FavoriteTrackItem(
    track: Track,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = dimensionResource(R.dimen.sixteen_dp),
                vertical = dimensionResource(R.dimen.eight_dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = track.trackName,
            modifier = Modifier
                .size(dimensionResource(R.dimen.forty_five_dp))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.two_dp))),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder)
        )

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.eight_dp)))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.trackName ?: "",
                fontSize = 16.sp,
                fontFamily = YsDisplayRegular,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colorResource(R.color.color_00_FF)
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.two_dp)))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.artistName ?: "",
                    fontSize = 11.sp,
                    fontFamily = YsDisplayRegular,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colorResource(R.color.gray_text),
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.two_dp)))

                Canvas(modifier = Modifier.size(dimensionResource(R.dimen.six_dp))) {
                    drawCircle(color = Color(0xFFAEAFB4))
                }

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.six_dp)))

                Text(
                    text = SimpleDateFormat("mm:ss", Locale.getDefault())
                        .format(track.trackTimeMillis),
                    fontSize = 11.sp,
                    fontFamily = YsDisplayRegular,
                    maxLines = 1,
                    color = colorResource(R.color.gray_text)
                )
            }
        }

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.twelve_dp)))

        Icon(
            painter = painterResource(R.drawable.more),
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.twenty_four_dp)),
            tint = Color.Unspecified
        )
    }
}

@Composable
private fun FavoriteEmpty() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.no_track),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.sixteen_dp)))
        Text(
            text = stringResource(R.string.empty_media),
            fontSize = 19.sp,
            fontFamily = YsDisplayMedium,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.color_FF_B22)
        )
    }
}