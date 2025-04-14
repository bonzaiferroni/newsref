package newsref.app.pond.nav

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
import newsref.app.pond.behavior.SlideIn
import newsref.app.pond.theme.Pond


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
                Spacer(modifier = Modifier.height(portalTopBarHeight + Pond.ruler.innerSpacing))
            }

            content()

            if (showBottomNav) {
                item {
                    Spacer(modifier = Modifier.height(portalBottomBarHeight + Pond.ruler.innerSpacing))
                }
            }
        }
    }
}

@Composable
fun Scaffold(
    showBottomNav: Boolean = true,
    transition: EnterTransition = slideInVertically { it },
    verticalArrangement: Arrangement.Vertical = Pond.ruler.columnTight,
    content: @Composable ColumnScope.() -> Unit
) {
    val portal = LocalPortal.current

    LaunchedEffect(Unit) {
        portal.setBottomBarIsVisible(showBottomNav)
    }

    SlideIn(enter = transition) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Pond.ruler.columnTight
        ) {
            Spacer(modifier = Modifier.height(portalTopBarHeight))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = verticalArrangement
            ) {
                content()
            }

            if (showBottomNav) {
                Spacer(modifier = Modifier.height(portalBottomBarHeight))
            }
        }
    }
}