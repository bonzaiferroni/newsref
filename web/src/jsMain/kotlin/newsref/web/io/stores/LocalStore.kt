package newsref.web.io.stores

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

class LocalStorageJson<T>(private val key: String) : ReadWriteProperty<Any?, T?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return localStorage.getItem(key)?.let { JSON.parse<T>(it) }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (value == null) {
            localStorage.removeItem(key)
        } else {
            localStorage.setItem(key, JSON.stringify(value))
        }
    }
}