package newsref.app.pond.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

interface PondRuler {
    val spacing: Int
    val corner: Int
    val bigCorner: Int
    val shadowElevation: Dp

    val baseSpacing: Dp get() = spacing.dp
    val halfSpacing: Dp get() = (spacing / 2).dp
    val innerSpacing: Dp get() = (spacing / 4).dp
    val basePadding: PaddingValues get() = PaddingValues(baseSpacing)
    val halfPadding: PaddingValues get() = PaddingValues(halfSpacing)
    val innerPadding: PaddingValues get() = PaddingValues(innerSpacing)

    val rowTight: Arrangement.Horizontal get() = Arrangement.spacedBy(innerSpacing)
    val rowGrouped: Arrangement.Horizontal get() = Arrangement.spacedBy(halfSpacing)
    val rowSpaced: Arrangement.Horizontal get() = Arrangement.spacedBy(baseSpacing)
    val columnTight: Arrangement.Vertical get() = Arrangement.spacedBy(innerSpacing)
    val columnGrouped: Arrangement.Vertical get() = Arrangement.spacedBy(halfSpacing)
    val columnSpaced: Arrangement.Vertical get() = Arrangement.spacedBy(baseSpacing)

    val round: Shape get() = RoundedCornerShape(percent = 100)
    val rounded: Shape get() = RoundedCornerShape(corner.dp)
    val bigRounded: Shape get() = RoundedCornerShape(bigCorner.dp)
    val innerCorners: Shape get() = RoundedCornerShape((corner / 4).dp)

    val roundTop: Shape get() = RoundedCornerShape(
        topStart = corner.dp,
        topEnd = corner.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    val roundStart: Shape get() = RoundedCornerShape(
        topStart = corner.dp,
        topEnd = 0.dp,
        bottomStart = corner.dp,
        bottomEnd = 0.dp
    )

    val roundEnd: Shape get() = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = corner.dp,
        bottomStart = 0.dp,
        bottomEnd = corner.dp
    )

    val roundBottom: Shape get() = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = corner.dp,
        bottomEnd = corner.dp
    )

    val roundedBottom: Shape get() = RoundedCornerShape(
        topStartPercent = 0,
        topEndPercent = 0,
        bottomStartPercent = 50,
        bottomEndPercent = 50
    )

    val roundedTop: Shape get() = RoundedCornerShape(
        topStartPercent = 50,
        topEndPercent = 50,
        bottomStartPercent = 0,
        bottomEndPercent = 0
    )
}

object DefaultRuler : PondRuler{
    override val spacing: Int = 16
    override val corner: Int = 16
    override val bigCorner: Int = 64
    override val shadowElevation = 12.dp
}