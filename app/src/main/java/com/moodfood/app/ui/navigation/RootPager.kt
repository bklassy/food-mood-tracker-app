package com.moodfood.app.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moodfood.app.R
import com.moodfood.app.ui.screens.FavoriteFoodsScreen
import com.moodfood.app.ui.screens.JournalScreen
import com.moodfood.app.ui.screens.MentalHealthToolsScreen

/** Page order for the root swipe pager. Journal is the default landing page. */
private enum class RootPage {
    MentalHealthTools,
    Journal,
    FavoriteFoods,
}

private val pages = RootPage.values().toList()
private val journalPageIndex = pages.indexOf(RootPage.Journal)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RootPager() {
    val pagerState: PagerState = rememberPagerState(
        initialPage = journalPageIndex,
        pageCount = { pages.size },
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.background_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // The background is fixed while content scrolls over it, so any given
        // piece of text passes over both the image's darkest and palest
        // regions depending on scroll position - this scrim keeps text
        // readable everywhere rather than only where the image happens to be
        // dark.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f)),
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { pageIndex ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                when (pages[pageIndex]) {
                    RootPage.MentalHealthTools -> MentalHealthToolsScreen()
                    RootPage.Journal -> JournalScreen()
                    RootPage.FavoriteFoods -> FavoriteFoodsScreen()
                }
            }
        }
    }
}
