package com.truckconnect.feature.driver

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import com.truckconnect.core.design.R
import com.truckconnect.core.design.component.TcButton
import com.truckconnect.core.design.component.TcButtonSize
import com.truckconnect.core.design.component.TcButtonVariant
import com.truckconnect.core.design.component.TcCard
import com.truckconnect.core.design.component.TcChip
import com.truckconnect.core.design.component.TcChipKind
import com.truckconnect.core.design.component.TcIconBox
import com.truckconnect.core.design.component.TcScreen
import com.truckconnect.core.design.theme.TcThemeState

@Composable
fun JobDetailsScreen(
    onBackClick: () -> Unit = {},
    onNavigateClick: () -> Unit = {},
    onCallOfficeClick: () -> Unit = {},
    onStartTripClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val colors = TcThemeState.colors
    val typography = TcThemeState.typography
    val dimens = TcThemeState.dimens
    val isMalayalam = TcThemeState.isMalayalam

    TcScreen(
        title = "TC-2026-000131",
        onBackClick = onBackClick,
        topBarActions = {
            TcChip(text = "Accepted", kind = TcChipKind.Info)
        },
        isScrollable = true,
        pinnedBottomActions = {
            TcButton(
                onClick = onStartTripClick,
                variant = TcButtonVariant.Primary,
                size = TcButtonSize.Xl,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_truck),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.Size26),
                    tint = colors.accentInk
                )
                Text(
                    text = if (isMalayalam) "യാത്ര ആരംഭിക്കുക" else "Start trip",
                    modifier = Modifier.padding(start = dimens.Gap8)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.Gap10)
        ) {
            // Route Card
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap10)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isMalayalam) "റൂട്ട് വിശദാംശങ്ങൾ" else "Route summary",
                            style = typography.caption,
                            color = colors.muted
                        )
                        Text(
                            text = "165 km · ~4.5 hrs",
                            style = typography.caption,
                            color = colors.accent
                        )
                    }

                    Text(
                        text = "Kasaragod → Malappuram",
                        style = typography.h2,
                        color = colors.text
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimens.Gap10)
                    ) {
                        TcButton(
                            onClick = {
                                try {
                                    val gmmIntentUri = Uri.parse("google.navigation:q=11.07,76.07")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    onNavigateClick()
                                }
                            },
                            variant = TcButtonVariant.Outline,
                            size = TcButtonSize.Lg,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_nav),
                                contentDescription = null,
                                modifier = Modifier.size(dimens.Size22),
                                tint = colors.text
                            )
                            Text(
                                text = if (isMalayalam) "വഴി കാണിക്കുക" else "Navigate",
                                modifier = Modifier.padding(start = dimens.Gap6)
                            )
                        }

                        TcButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919876543210"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    onCallOfficeClick()
                                }
                            },
                            variant = TcButtonVariant.Outline,
                            size = TcButtonSize.Lg,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_phone),
                                contentDescription = null,
                                modifier = Modifier.size(dimens.Size22),
                                tint = colors.text
                            )
                            Text(
                                text = if (isMalayalam) "ഓഫീസ്" else "Call dispatch",
                                modifier = Modifier.padding(start = dimens.Gap6)
                            )
                        }
                    }
                }
            }

            // Pickup & Drop Cards
            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap8)) {
                    Text(
                        text = if (isMalayalam) "കയറ്റുന്ന സ്ഥലം (Pickup)" else "Pickup point",
                        style = typography.caption,
                        color = colors.muted
                    )
                    Text(
                        text = "Kasaragod APMC Yard, Bay 4",
                        style = typography.btnSm,
                        color = colors.text
                    )
                    Text(
                        text = "Contact: Suresh Babu (+91 94471 22334)",
                        style = typography.xsmall,
                        color = colors.muted
                    )
                }
            }

            TcCard {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.Gap8)) {
                    Text(
                        text = if (isMalayalam) "ഇറക്കുന്ന സ്ഥലം (Drop)" else "Drop location",
                        style = typography.caption,
                        color = colors.muted
                    )
                    Text(
                        text = "Malappuram Central Warehouse, Gate 2",
                        style = typography.btnSm,
                        color = colors.text
                    )
                    Text(
                        text = "Contact: Venugopal R (+91 94472 33445)",
                        style = typography.xsmall,
                        color = colors.muted
                    )
                }
            }
        }
    }
}
