package com.moodfood.app.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
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
