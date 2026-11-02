package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BrandDeepNavy
import com.example.ui.theme.BrandGoldenYellow
import com.example.ui.theme.BrandPureWhite

/**
 * Official RoomSaathi Circular Emblem Icon.
 */
@Composable
fun RoomSaathiLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showBorder: Boolean = true,
    elevation: Dp = 4.dp
) {
    Surface(
        modifier = modifier
            .size(size)
            .shadow(elevation, CircleShape)
            .clip(CircleShape),
        shape = CircleShape,
        color = BrandDeepNavy
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .then(
                    if (showBorder) {
                        Modifier.border(1.5.dp, BrandGoldenYellow.copy(alpha = 0.8f), CircleShape)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.roomsaathi_official_logo),
                contentDescription = "RoomSaathi Official Logo",
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * Official Brand Name Typography:
 * "Room" in RoomColor (pure white or deep navy) + "Saathi" in Golden Yellow (#FFC400).
 */
@Composable
fun RoomSaathiBrandText(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp,
    roomColor: Color = BrandPureWhite,
    saathiColor: Color = BrandGoldenYellow
) {
    val text = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = roomColor,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
        ) {
            append("Room")
        }
        withStyle(
            style = SpanStyle(
                color = saathiColor,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
        ) {
            append("Saathi")
        }
    }

    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium.copy(
            fontSize = fontSize,
            lineHeight = (fontSize.value * 1.2).sp
        ),
        modifier = modifier
    )
}

/**
 * Full Brand Header with Logo, Stylized "RoomSaathi", and Official Tagline:
 * "Your Room, Your Comfort, Your Saathi."
 */
@Composable
fun RoomSaathiFullBrandHeader(
    modifier: Modifier = Modifier,
    logoSize: Dp = 100.dp,
    titleSize: TextUnit = 32.sp,
    roomColor: Color = BrandPureWhite,
    taglineColor: Color = BrandGoldenYellow,
    taglineSubtitleColor: Color = BrandPureWhite.copy(alpha = 0.85f)
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RoomSaathiLogoIcon(
            size = logoSize,
            showBorder = true,
            elevation = 12.dp
        )

        Spacer(modifier = Modifier.height(18.dp))

        RoomSaathiBrandText(
            fontSize = titleSize,
            roomColor = roomColor,
            saathiColor = BrandGoldenYellow
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Official Tagline Badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = BrandDeepNavy.copy(alpha = 0.75f),
            border = androidx.compose.foundation.BorderStroke(1.dp, BrandGoldenYellow.copy(alpha = 0.5f))
        ) {
            Text(
                text = "Your Room, Your Comfort, Your Saathi.",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp
                ),
                color = taglineColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

/**
 * Compact Top Bar Brand Unit used across app screens.
 */
@Composable
fun RoomSaathiTopBarBrand(
    modifier: Modifier = Modifier,
    logoSize: Dp = 40.dp,
    fontSize: TextUnit = 20.sp,
    roomColor: Color = BrandPureWhite,
    subtitle: String? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoomSaathiLogoIcon(size = logoSize, elevation = 2.dp)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            RoomSaathiBrandText(fontSize = fontSize, roomColor = roomColor)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = roomColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}
