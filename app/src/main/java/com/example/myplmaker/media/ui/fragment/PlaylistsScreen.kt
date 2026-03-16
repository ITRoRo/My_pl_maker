package com.example.myplmaker.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myplmaker.R
import com.example.myplmaker.media.ui.view.PlaylistsScreenState
import com.example.myplmaker.media.ui.view.PlaylistsViewModel
import com.example.myplmaker.playlist.domain.model.Playlist

private val YsDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))
private val YsDisplayRegular = FontFamily(Font(R.font.ys_display_regular, FontWeight.Normal))

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistsViewModel,
    onCreatePlaylistClick: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit
) {
    val state by viewModel.state.observeAsState(PlaylistsScreenState.Empty)

    Column(modifier = Modifier.fillMaxSize()) {

        Button(
            onClick = onCreatePlaylistClick,
            modifier = Modifier
                .padding(
                    top = dimensionResource(R.dimen.twenty_four_dp),
                    bottom = dimensionResource(R.dimen.sixteen_dp)
                )
                .height(dimensionResource(R.dimen.thirty_six_dp))
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(dimensionResource(R.dimen.radius_54)),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.color_FF_B22),
                contentColor = colorResource(R.color.color_B22_FF)
            )
        ) {
            Text(
                text = stringResource(R.string.new_playlist),
                fontSize = 14.sp,
                fontFamily = YsDisplayMedium
            )
        }

        when (val currentState = state) {
            is PlaylistsScreenState.Content -> {
                PlaylistsGrid(
                    playlists = currentState.playlists,
                    onPlaylistClick = onPlaylistClick
                )
            }

            is PlaylistsScreenState.Empty -> {
                PlaylistsEmpty()
            }
        }
    }
}

@Composable
private fun PlaylistsGrid(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.twelve_dp),
            vertical = dimensionResource(R.dimen.sixteen_dp)
        ),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.eight_dp)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.sixteen_dp))
    ) {
        items(playlists) { playlist ->
            PlaylistItem(
                playlist = playlist,
                onClick = { onPlaylistClick(playlist) }
            )
        }
    }
}

@Composable
private fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() }
    ) {
        AsyncImage(
            model = playlist.coverImagePath,
            contentDescription = playlist.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(dimensionResource(R.dimen.grid_corner_radius))),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder)
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.four_dp)))

        Text(
            text = playlist.name,
            fontSize = 12.sp,
            fontFamily = YsDisplayRegular,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = colorResource(R.color.color_FF_B22),
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.four_dp))
        )

        Text(
            text = "${playlist.trackCount} треков",
            fontSize = 12.sp,
            fontFamily = YsDisplayRegular,
            maxLines = 1,
            color = colorResource(R.color.gray_text),
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.four_dp))
        )
    }
}

@Composable
private fun PlaylistsEmpty() {
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
            text = stringResource(R.string.no_single_playlist),
            fontSize = 19.sp,
            fontFamily = YsDisplayMedium,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.color_FF_B22),
            textAlign = TextAlign.Center
        )
    }
}