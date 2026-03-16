package com.example.myplmaker.media.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myplmaker.R
import com.example.myplmaker.media.ui.view.FavoriteViewModel
import com.example.myplmaker.media.ui.view.PlaylistsViewModel
import com.example.myplmaker.playlist.domain.model.Playlist
import com.example.myplmaker.search.domain.model.Track
import kotlinx.coroutines.launch

private val YsDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))

@Composable
fun MediaScreen(
    favoriteViewModel: FavoriteViewModel,
    playlistViewModel: PlaylistsViewModel,
    onTrackClick: (Track) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val tabs = listOf(
        stringResource(R.string.favorite_tracks),
        stringResource(R.string.playlists)
    )

    val backgroundColor = colorResource(R.color.color_B22_FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Text(
            text = stringResource(R.string.media),
            fontSize = 22.sp,
            fontFamily = YsDisplayMedium,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.black_white),
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.sixteen_dp),
                top = dimensionResource(R.dimen.sixteen_dp),
                bottom = dimensionResource(R.dimen.eight_dp)
            )
        )

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = backgroundColor,
            contentColor = colorResource(R.color.color_FF_B22),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = colorResource(R.color.color_FF_B22)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontFamily = YsDisplayMedium,
                            color = colorResource(R.color.color_FF_B22)
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> FavoriteScreen(
                    viewModel = favoriteViewModel,
                    onTrackClick = onTrackClick
                )
                1 -> PlaylistsScreen(
                    viewModel = playlistViewModel,
                    onCreatePlaylistClick = onCreatePlaylistClick,
                    onPlaylistClick = onPlaylistClick
                )
            }
        }
    }
}