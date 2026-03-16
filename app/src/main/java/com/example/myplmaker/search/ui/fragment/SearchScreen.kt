package com.example.myplmaker.search.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myplmaker.R
import com.example.myplmaker.search.domain.model.Track
import com.example.myplmaker.search.ui.view.SearchViewModel
import java.text.SimpleDateFormat
import java.util.Locale

private val YsDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))
private val YsDisplayRegular = FontFamily(Font(R.font.ys_display_regular, FontWeight.Normal))

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit
) {
    val state by viewModel.observeState().observeAsState()
    var queryText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.color_B22_FF))
    ) {
        Text(
            text = stringResource(R.string.search_name),
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

        SearchTextField(
            query = queryText,
            onQueryChange = { newText ->
                queryText = newText
                if (newText.isNotEmpty()) {
                    isLoading = true
                }
                viewModel.searchDebounce(newText)
            },
            onClear = {
                queryText = ""
                isLoading = false
                viewModel.searchDebounce("")
                keyboardController?.hide()
            },
            onSearch = {
                focusManager.clearFocus()
            },
            onFocusGained = {
                if (queryText.isEmpty()) {
                    viewModel.load()
                }
            }
        )

        state?.let { result ->
            when (result.code) {
                200 -> {
                    isLoading = false
                    if (result.trackList.isNullOrEmpty()) {
                        NoTracksPlaceholder()
                    } else {
                        TrackList(
                            tracks = result.trackList,
                            onTrackClick = { track ->
                                if (viewModel.clickDebounce()) {
                                    viewModel.save(track)
                                    onTrackClick(track)
                                }
                            }
                        )
                    }
                }

                -1 -> {
                    isLoading = false
                    NoInternetPlaceholder(
                        onRetry = {
                            isLoading = true
                            viewModel.searchDebounce(queryText)
                        }
                    )
                }

                -2 -> {
                    isLoading = false
                    if (result.historyList.isNotEmpty() && queryText.isEmpty()) {
                        HistoryBlock(
                            tracks = result.historyList,
                            onTrackClick = { track ->
                                if (viewModel.clickDebounce()) {
                                    onTrackClick(track)
                                }
                            },
                            onClearHistory = {
                                viewModel.clearHistory()
                            }
                        )
                    }
                }
            }
        }

        if (isLoading && state?.code != 200 && state?.code != -1) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = dimensionResource(R.dimen.hundred120dp))
                        .size(dimensionResource(R.dimen.forty_for_44dp)),
                    color = colorResource(R.color.blue)
                )
            }
        }
    }
}

@Composable
private fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    onFocusGained: () -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.fifty_dp))
            .padding(horizontal = dimensionResource(R.dimen.sixteen_dp))
            .clip(RoundedCornerShape(dimensionResource(R.dimen.grid_corner_radius)))
            .onFocusEvent { focusState ->
                if (focusState.isFocused && !hasFocus) {
                    hasFocus = true
                    onFocusGained()
                }
                if (!focusState.isFocused) {
                    hasFocus = false
                }
            },
        placeholder = {
            Text(
                text = stringResource(R.string.search_name),
                fontSize = 16.sp,
                fontFamily = YsDisplayRegular,
                color = colorResource(R.color.color_search_text)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = colorResource(R.color.color_search_text)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = colorResource(R.color.color_search_16)
                    )
                }
            }
        },
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onSearch() }),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp,
            fontFamily = YsDisplayRegular
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = colorResource(R.color.color_E6E8EB),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = colorResource(R.color.blue),
            textColor = colorResource(R.color.black_text)
        )
    )
}

@Composable
private fun TrackList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = dimensionResource(R.dimen.sixteen_dp))
    ) {
        items(tracks) { track ->
            SearchTrackItem(
                track = track,
                onClick = { onTrackClick(track) }
            )
        }
    }
}

@Composable
fun SearchTrackItem(
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

                Spacer(modifier = Modifier.width(5.dp))

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
private fun HistoryBlock(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = dimensionResource(R.dimen.eight_dp))
    ) {
        Text(
            text = stringResource(R.string.search_history),
            fontSize = 22.sp,
            fontFamily = YsDisplayMedium,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.color_FF_B22),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(R.dimen.eight_dp)),
            textAlign = TextAlign.Center
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = dimensionResource(R.dimen.eight_dp))
        ) {
            items(tracks) { track ->
                SearchTrackItem(
                    track = track,
                    onClick = { onTrackClick(track) }
                )
            }
        }

        Button(
            onClick = onClearHistory,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(
                    top = dimensionResource(R.dimen.twenty_four_dp),
                    bottom = dimensionResource(R.dimen.twenty_four_dp)
                )
                .height(dimensionResource(R.dimen.thirty_six_dp)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.radius_54)),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.color_FF_B22),
                contentColor = colorResource(R.color.color_B22_FF)
            )
        ) {
            Text(
                text = stringResource(R.string.clear_history),
                fontSize = 14.sp,
                fontFamily = YsDisplayMedium
            )
        }
    }
}

@Composable
private fun NoTracksPlaceholder() {
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
            text = stringResource(R.string.no_track),
            fontSize = 19.sp,
            fontFamily = YsDisplayMedium,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.color_FF_B22)
        )
    }
}

@Composable
private fun NoInternetPlaceholder(
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.light_mode),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.sixteen_dp)))
        Text(
            text = stringResource(R.string.no_internet),
            fontSize = 19.sp,
            fontFamily = YsDisplayMedium,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.color_FF_B22)
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.eight_dp)))
        Text(
            text = stringResource(R.string.no_loading),
            fontSize = 14.sp,
            fontFamily = YsDisplayRegular,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.color_FF_B22)
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.twenty_four_dp)))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(dimensionResource(R.dimen.sixteen_dp)),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.color_FF_B22),
                contentColor = colorResource(R.color.color_B22_FF)
            )
        ) {
            Text(
                text = stringResource(R.string.update),
                fontSize = 14.sp,
                fontFamily = YsDisplayMedium
            )
        }
    }
}