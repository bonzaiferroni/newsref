package newsref.web.ui.pages

import io.kvision.core.Container
import io.kvision.form.select.select
import io.kvision.html.TAG
import io.kvision.html.label
import io.kvision.html.tag
import newsref.web.core.AppContext
import newsref.web.core.PortalEvents

fun Container.sandboxPage(context: AppContext): PortalEvents? {
	//<label for="pirate-treasure">Choose yer treasure:</label>
	//<select id="pirate-treasure" name="treasure">
	//  <option value="gold">Gold</option>
	//  <option value="silver">Silver</option>
	//  <option value="gems">Gems</option>
	//  <option value="map">Treasure Map</option>
	//</select>
	select(
		value = "gold",
		options = listOf(
			Pair("gold", "Gold"),
			Pair("silver", "Silver"),
			Pair("gems", "Gems"),
			Pair("map", "Treasure Map")
		)
	) {

	}
	return null
}