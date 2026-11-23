package com.truckconnect.core.design.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.truckconnect.core.design.R

@OptIn(ExperimentalTextApi::class)
val PlusJakartaSans = FontFamily(
    Font(
        resId = R.font.plus_jakarta_sans_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        resId = R.font.plus_jakarta_sans_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        resId = R.font.plus_jakarta_sans_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    ),
    Font(
        resId = R.font.plus_jakarta_sans_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    ),
    Font(
        resId = R.font.plus_jakarta_sans_variable,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(800))
    )
)

@OptIn(ExperimentalTextApi::class)
val NotoSansMalayalam = FontFamily(
    Font(
        resId = R.font.noto_sans_malayalam_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        resId = R.font.noto_sans_malayalam_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        resId = R.font.noto_sans_malayalam_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    ),
    Font(
        resId = R.font.noto_sans_malayalam_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    ),
    Font(
        resId = R.font.noto_sans_malayalam_variable,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(800))
    )
)

data class TcTypography(
    val h1: TextStyle,
    val appBarTitle: TextStyle,
    val h2: TextStyle = appBarTitle,
    val btn: TextStyle,
    val btnLg: TextStyle,
    val btnXl: TextStyle,
    val btnSm: TextStyle,
    val chip: TextStyle,
    val label: TextStyle,
    val body: TextStyle,
    val small: TextStyle,
    val xsmall: TextStyle,
    val caption: TextStyle = xsmall,
    val note: TextStyle,
    val kpiValue: TextStyle,
    val keypadKey: TextStyle,
    val bottomNavLabel: TextStyle,
    val avatar: TextStyle,
    val avatarLarge: TextStyle,
    val inputText: TextStyle,
    val splashTitle: TextStyle,
    val jobAlertDestination: TextStyle,
    val segmented: TextStyle,
    val timelineTitle: TextStyle,
    val timelineSubtitle: TextStyle
)

fun tcTypography(isDriver: Boolean, isMalayalam: Boolean = false): TcTypography {
    val defaultFont = if (isMalayalam && isDriver) NotoSansMalayalam else PlusJakartaSans

    return if (isDriver) {
        TcTypography(
            h1 = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (TcAccessibility.PRD_FLOORS) 26.sp else 26.sp,
                lineHeight = 31.2.sp,
                letterSpacing = (-0.02).em
            ),
            appBarTitle = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                letterSpacing = (-0.01).em
            ),
            btn = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            ),
            btnLg = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            ),
            btnXl = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            btnSm = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            ),
            chip = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            ),
            label = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            ),
            body = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = if (TcAccessibility.PRD_FLOORS) 18.sp else 16.sp,
                lineHeight = 21.6.sp
            ),
            small = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp
            ),
            xsmall = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 11.5.sp
            ),
            caption = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 11.5.sp
            ),
            note = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 18.sp
            ),
            kpiValue = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                letterSpacing = (-0.02).em
            ),
            keypadKey = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            bottomNavLabel = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp
            ),
            avatar = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.5.sp
            ),
            avatarLarge = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            ),
            inputText = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            ),
            splashTitle = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp
            ),
            jobAlertDestination = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp
            ),
            segmented = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            timelineTitle = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            ),
            timelineSubtitle = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp
            )
        )
    } else {
        TcTypography(
            h1 = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                lineHeight = 26.4.sp,
                letterSpacing = (-0.02).em
            ),
            appBarTitle = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 19.sp,
                letterSpacing = (-0.01).em
            ),
            btn = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            btnLg = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            ),
            btnXl = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            btnSm = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            ),
            chip = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            label = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.5.sp
            ),
            body = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 17.55.sp
            ),
            small = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 11.5.sp
            ),
            xsmall = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 10.5.sp
            ),
            caption = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 10.5.sp
            ),
            note = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                lineHeight = 16.5.sp
            ),
            kpiValue = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                letterSpacing = (-0.02).em
            ),
            keypadKey = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            bottomNavLabel = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.5.sp
            ),
            avatar = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.5.sp
            ),
            avatarLarge = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            ),
            inputText = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            ),
            splashTitle = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp
            ),
            jobAlertDestination = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp
            ),
            segmented = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            timelineTitle = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp
            ),
            timelineSubtitle = TextStyle(
                fontFamily = defaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp
            )
        )
    }
}

val LocalTcTypography = staticCompositionLocalOf {
    tcTypography(false, false)
}
