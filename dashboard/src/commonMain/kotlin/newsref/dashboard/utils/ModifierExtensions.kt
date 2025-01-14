package newsref.dashboard.utils

import androidx.compose.ui.Modifier

inline fun <V> Modifier.modifyIfNotNull(
    value: V?,
    noinline elseBlock: (Modifier.() -> Modifier)? = null,
    block: Modifier.(V) -> Modifier
): Modifier {
    if (value != null) return this.block(value)
    else if(elseBlock != null) return this.elseBlock()
    return this
}