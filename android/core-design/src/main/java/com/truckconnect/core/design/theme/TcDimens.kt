package com.truckconnect.core.design.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object TcAccessibility {
    var PRD_FLOORS: Boolean = false
}

object TcDimens {
    // Canvas reference size: 324 x 648 dp
    val CanvasWidth = 324.dp
    val CanvasHeight = 648.dp

    // Standard Spacing & Gaps
    val Zero = 0.dp
    val Gap2 = 2.dp
    val Gap3 = 3.dp
    val Gap4 = 4.dp
    val Gap5 = 5.dp
    val Gap6 = 6.dp
    val Gap8 = 8.dp
    val Gap10 = 10.dp
    val Gap12 = 12.dp
    val Gap14 = 14.dp
    val Gap16 = 16.dp
    val Gap18 = 18.dp
    val Gap20 = 20.dp
    val Gap24 = 24.dp

    // Standard Paddings
    val Padding2 = 2.dp
    val Padding3 = 3.dp
    val Padding4 = 4.dp
    val Padding6 = 6.dp
    val Padding8 = 8.dp
    val Padding10 = 10.dp
    val Padding11 = 11.dp
    val Padding12 = 12.dp
    val Padding13 = 13.dp
    val Padding14 = 14.dp
    val Padding16 = 16.dp
    val Padding18 = 18.dp
    val Padding20 = 20.dp
    val Padding22 = 22.dp
    val Padding24 = 24.dp
    val Padding34 = 34.dp

    // Standard Component Sizes
    val Size8 = 8.dp
    val Size10 = 10.dp
    val Size12 = 12.dp
    val Size14 = 14.dp
    val Size16 = 16.dp
    val Size18 = 18.dp
    val Size20 = 20.dp
    val Size22 = 22.dp
    val Size24 = 24.dp
    val Size28 = 28.dp
    val Size30 = 30.dp
    val Size32 = 32.dp
    val Size34 = 34.dp
    val Size36 = 36.dp
    val Size38 = 38.dp
    val Size40 = 40.dp
    val Size42 = 42.dp
    val Size44 = 44.dp
    val Size46 = 46.dp
    val Size48 = 48.dp
    val Size54 = 54.dp
    val Size56 = 56.dp
    val Size58 = 58.dp
    val Size62 = 62.dp
    val Size64 = 64.dp
    val Size68 = 68.dp
    val Size72 = 72.dp
    val Size76 = 76.dp
    val Size84 = 84.dp
    val Size92 = 92.dp
    val Size96 = 96.dp
    val Size110 = 110.dp

    // Screen padding
    val ScreenPaddingHorizontal = 16.dp
    val ScreenPaddingTop = 0.dp
    val ScreenPaddingBottom = 14.dp
    val ScreenGap = 12.dp

    // App Bar
    val AppBarPaddingTop = 8.dp
    val AppBarPaddingBottom = 10.dp
    val AppBarPaddingHorizontal = 16.dp
    val AppBarGap = 10.dp
    val IconButtonSize = 38.dp
    val IconButtonRadius = 12.dp
    val IconSizeDefault = 20.dp
    val NotificationPipSize = 8.dp
    val NotificationPipRing = 2.dp

    // Cards
    val CardRadius = 16.dp
    val CardPadding = 14.dp
    val CardBorder = 1.dp
    val KpiCardPadding = 12.dp
    val KpiGridGap = 10.dp

    // Buttons
    val ButtonMdHeight: Dp get() = if (TcAccessibility.PRD_FLOORS) 48.dp else 46.dp
    val ButtonMdRadius = 14.dp

    val ButtonLgHeight: Dp get() = if (TcAccessibility.PRD_FLOORS) 56.dp else 56.dp
    val ButtonLgRadius = 18.dp

    val ButtonXlHeight: Dp get() = if (TcAccessibility.PRD_FLOORS) 64.dp else 64.dp
    val ButtonXlRadius = 20.dp

    val ButtonSmHeight = 34.dp
    val ButtonSmRadius = 10.dp
    val ButtonSmPaddingHorizontal = 12.dp

    val ButtonTextHeight = 36.dp
    val ButtonContentGap = 8.dp

    // Inputs
    val InputAdminHeight = 48.dp
    val InputDriverHeight = 58.dp
    val InputAdminRadius = 12.dp
    val InputDriverRadius = 14.dp
    val InputPaddingHorizontal = 14.dp
    val InputGap = 10.dp

    // Chips
    val ChipRadius = 99.dp
    val ChipDotSize = 6.dp
    val ChipGap = 5.dp
    val ChipPaddingHorizontalAdmin = 9.dp
    val ChipPaddingHorizontalDriver = 11.dp
    val ChipPaddingVerticalAdmin = 3.dp
    val ChipPaddingVerticalDriver = 4.dp

    // Avatars & Icons
    val AvatarSize = 38.dp
    val AvatarLargeSize = 72.dp
    val IconBoxSize = 40.dp
    val IconBoxRadius = 12.dp

    // Progress Bar
    val ProgressBarHeight = 6.dp
    val ProgressBarRadius = 3.dp

    // List Rows
    val ListRowPaddingVerticalAdmin = 13.dp
    val ListRowPaddingVerticalDriver = 16.dp
    val ListRowGap = 12.dp

    // Segmented Control
    val SegmentedRadius = 12.dp
    val SegmentedPadding = 3.dp
    val SegmentedGap = 2.dp
    val SegmentedItemRadius = 9.dp
    val SegmentedItemPaddingVertical = 4.dp
    val SegmentedItemPaddingHorizontal = 8.dp

    // Bottom Sheet
    val BottomSheetTopRadius = 26.dp
    val BottomSheetPaddingTop = 10.dp
    val BottomSheetPaddingHorizontal = 18.dp
    val BottomSheetPaddingBottom = 20.dp
    val BottomSheetGap = 12.dp
    val BottomSheetHandleWidth = 38.dp
    val BottomSheetHandleHeight = 4.dp
    val BottomSheetHandleRadius = 2.dp

    // Toggle & Radio
    val ToggleWidth = 42.dp
    val ToggleHeight = 24.dp
    val ToggleKnobSize = 18.dp
    val ToggleKnobInset = 3.dp
    val RadioSize = 22.dp
    val RadioDotSize = 10.dp

    // Steps Bar
    val StepsBarHeight = 5.dp
    val StepsBarRadius = 3.dp
    val StepsBarGap = 6.dp

    // PIN Dots & Keypad
    val PinDotSize = 18.dp
    val PinDotGap = 12.dp
    val KeypadGap = 10.dp
    val KeypadKeyHeight = 56.dp
    val KeypadKeyRadius = 16.dp

    // Photo Box
    val PhotoBoxHeight = 96.dp
    val PhotoBoxLgHeight = 130.dp
    val PhotoBoxRadius = 14.dp
    val PhotoBoxBorderWidth = 1.5.dp

    // Floating Buttons
    val FabSize = 54.dp
    val FabRadius = 18.dp
    val SosButtonSize = 62.dp
    val ActiveTripSosBottom = 176.dp
    val Size26 = 26.dp
    val PulseContainerSize = 110.dp

    // Bottom Nav
    val BottomNavAdminHeight = 68.dp
    val BottomNavDriverHeight = 76.dp
    val BottomNavActivePillWidth = 46.dp
    val BottomNavActivePillHeight = 28.dp
    val BottomNavActivePillRadius = 14.dp
    val BottomNavItemGap = 3.dp

    // Map Box
    val MapBoxRadius = 18.dp
}
