package newsref.app.blip.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

interface BlipRuler {
    val spacing: Int
    val corner: Int

    val baseSpacing: Dp get() = spacing.dp
    val halfSpacing: Dp get() = (spacing / 2).dp
    val innerSpacing: Dp get() = (spacing / 4).dp
    val basePadding: PaddingValues get() = PaddingValues(baseSpacing)
    val halfPadding: PaddingValues get() = PaddingValues(halfSpacing)
    val innerPadding: PaddingValues get() = PaddingValues(innerSpacing)

    val rowTight: Arrangement.Horizontal get() = Arrangement.spacedBy(innerSpacing)
    val rowGrouped: Arrangement.Horizontal get() = Arrangement.spacedBy(halfSpacing)
    val rowSpaced: Arrangement.Horizontal get() = Arrangement.spacedBy(baseSpacing)
    val columnGrouped: Arrangement.Vertical get() = Arrangement.spacedBy(halfSpacing)
    val columnSpaced: Arrangement.Vertical get() = Arrangement.spacedBy(baseSpacing)

    val round: RoundedCornerShape get() = RoundedCornerShape(corner.dp)
    val roundTop: RoundedCornerShape get() = RoundedCornerShape(
        topStart = corner.dp,
        topEnd = corner.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    val roundStart: RoundedCornerShape get() = RoundedCornerShape(
        topStart = corner.dp,
        topEnd = 0.dp,
        bottomStart = corner.dp,
        bottomEnd = 0.dp
    )

    val roundEnd: RoundedCornerShape get() = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = corner.dp,
        bottomStart = 0.dp,
        bottomEnd = corner.dp
    )

    val roundBottom: RoundedCornerShape get() = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = corner.dp,
        bottomEnd = corner.dp
    )
}