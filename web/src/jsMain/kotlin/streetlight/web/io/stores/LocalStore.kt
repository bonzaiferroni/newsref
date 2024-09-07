package streetlight.web.io.stores

import kotlinx.browser.localStorage
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LocalStore {
    var username by LocalStorageString("username")
    var session by LocalStorageString("session")
    var jwt by LocalStorageString("jwt")
    var save by LocalStorageBoolean("save")
}

class LocalStorageString(private val key: String) : ReadWriteProperty<Any?, String?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String? {
        return localStorage.getItem(key)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        if (value == null) {
            localStorage.removeItem(key)
        } else {
            localStorage.setItem(key, value)
        }
    }
}

class LocalStorageBoolean(private val key: String) : ReadWriteProperty<Any?, Boolean?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean? {
        return localStorage.getItem(key)?.toBoolean()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean?) {
        if (value == null) {
            localStorage.removeItem(key)
        } else {
            localStorage.setItem(key, value.toString())
        }
    }
}