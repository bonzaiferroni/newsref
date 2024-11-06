package newsref.web.ui.components

import io.kvision.core.Container
import io.kvision.core.onChange
import io.kvision.form.select.Select
import io.kvision.form.select.select
import io.kvision.state.ObservableValue
import io.kvision.state.bind

fun <T> Container.menu(
	value: T,
	options: List<T>,
	bind: ((T) -> Unit)? = null
): Select {
	val map = options.associateBy { it.toString() }
	val select = select(
		value = value.toString(),
		options = options.map { Pair(it.toString(), it.toString()) }
	) {
		onChange {
			bind?.invoke(map[this.value]!!)
		}
	}
	return select
}