package newsref.web.core

import newsref.web.ui.pages.*

object Pages {
    val home = PageConfig("Home", "/", builder = CachedPageBuilder { homePage() })
    val about = PageConfig("About", "/about", true, builder = CachedPageBuilder { aboutPage() })
    val privacy = PageConfig("Privacy", "/about/privacy", builder = CachedPageBuilder { privacyPage() })
    val basePages = listOf(home, about, privacy)

    val login = PageConfig("Login", "/login", builder = CachedPageBuilder { loginPage(it) })
    val admin = PageConfig("Admin", "/admin", builder = TransientPageBuilder { adminPage(it) })
    val signUp = PageConfig("SignUp", "/user/create", builder = TransientPageBuilder { signUpPage(it) })
    val account = PageConfig("Account", "/user/account", builder = TransientPageBuilder { accountPage(it) })
    val user = PageConfig("User", "/user", true, builder = TransientPageBuilder { userPage(it) })
    val userPages = listOf(signUp, admin, account, login, user)
}