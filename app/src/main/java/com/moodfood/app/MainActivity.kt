package com.moodfood.app

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.moodfood.app.data.AppDatabase
import com.moodfood.app.ui.navigation.RootPager
import com.moodfood.app.ui.theme.MoodFoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This app always renders dark (see MoodFoodTheme) regardless of the
        // system's light/dark setting, so the status/nav bar icons need to
        // stay light-colored too rather than auto-adapting - SystemBarStyle.
        // auto() would pick dark icons on a system set to light mode, making
        // them invisible against our always-dark background.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
        AppDatabase.init(applicationContext)
        setContent {
            MoodFoodTheme {
                RootPager()
            }
        }
    }
}
