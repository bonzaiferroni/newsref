package streetlight.web.core

import streetlight.web.ui.pages.*

object Pages {
    val home = PageConfig("Home", "/", builder = CachedPageBuilder { homePage() })
    val about = PageConfig("About", "/about", true, builder = CachedPageBuilder { aboutPage() })
    val privacy = PageConfig("Privacy", "/about/privacy", builder = CachedPageBuilder { privacyPage() })
    val events = PageConfig("Events", "/event", true, builder = CachedPageBuilder { eventPage() })
    val event = PageConfig("Event", "/event/:id", builder = IdPageBuilder { _, id -> eventProfile(id) })
    val basePages = listOf(home, about, privacy, events, event)

    val login = PageConfig("Login", "/login", builder = CachedPageBuilder { loginPage(it) })
    val admin = PageConfig("Admin", "/admin", builder = TransientPageBuilder { adminPage(it) })
    val signUp = PageConfig("SignUp", "/user/create", builder = TransientPageBuilder { signUpPage(it) })
    val account = PageConfig("Account", "/user/account", builder = TransientPageBuilder { accountPage(it) })
    val user = PageConfig("User", "/user", true, builder = TransientPageBuilder { userPage(it) })
    val catalog = PageConfig("Catalog", "/user/catalog", builder = TransientPageBuilder { catalogPage(it) })
    val userPages = listOf(signUp, admin, account, login, user, catalog)
}