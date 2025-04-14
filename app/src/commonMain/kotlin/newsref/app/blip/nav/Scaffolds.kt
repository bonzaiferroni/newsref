package newsref.app.blip.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import newsref.app.blip.behavior.SlideIn
import newsref.app.blip.theme.Blip


@Composable
fun LazyScaffold(
    showBottomNav: Boolean = true,
    transition: EnterTransition = slideInVertically { it },
    content: LazyListScope.() -> Unit
) {
    val portal = LocalPortal.current

    LaunchedEffect(showBottomNav) {
        portal.setBottomBarIsVisible(showBottomNav)
    }

    SlideIn(enter = transition) {
        LazyColumn {
            item {
                Spacer(modifier = Modifier.height(portalTopBarHeight + Blip.ruler.innerSpacing))
            }

            content()

            if (showBottomNav) {
                item {
                    Spacer(modifier = Modifier.height(portalBottomBarHeight + Blip.ruler.innerSpacing))
                }
            }
        }
    }
}

@Composable
fun Scaffold(
    showBottomNav: Boolean = true,
    transition: EnterTransition = slideInVertically { it },
    verticalArrangement: Arrangement.Vertical = Blip.ruler.columnTight,
    content: @Composable ColumnScope.() -> Unit
) {
    val portal = LocalPortal.current

    LaunchedEffect(Unit) {
        portal.setBottomBarIsVisible(showBottomNav)
    }

    SlideIn(enter = transition) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = verticalArrangement
        ) {
            Spacer(modifier = Modifier.height(portalTopBarHeight))

            content()

            if (showBottomNav) {
                Spacer(modifier = Modifier.height(portalBottomBarHeight))
            }
        }
    }
}