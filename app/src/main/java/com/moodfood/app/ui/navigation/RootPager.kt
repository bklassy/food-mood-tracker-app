package com.moodfood.app.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
        // Background + scrim deliberately ignore system bar insets, so the
        // image runs edge-to-edge behind the status bar and gesture nav area
        // instead of stopping at a flat painted strip there.
        Image(
            painter = painterResource(R.drawable.background_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f)),
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) { pageIndex ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
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
