package com.moodfood.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.moodfood.app.ui.navigation.RootPager
import com.moodfood.app.ui.theme.MoodFoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoodFoodTheme {
                RootPager()
            }
        }
    }
}
