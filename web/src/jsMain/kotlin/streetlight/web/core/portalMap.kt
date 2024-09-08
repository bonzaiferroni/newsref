package streetlight.web.core

import streetlight.web.pages.*

val basePages = listOf(
    PageConfig("Home", "/", builder = CachedPageBuilder {
        homePage()
    }),
    PageConfig("About", "/about", true, builder = CachedPageBuilder {
        aboutPage()
    }),
    PageConfig("Privacy", "/privacy", builder = CachedPageBuilder {
        privacyPage()
    }),
    PageConfig("Events", "/event", true, builder = CachedPageBuilder {
        eventPage()
    }),
    PageConfig("Event", "/event/:id", builder = IdPageBuilder { _, id ->
        eventProfile(id)
    }),
)

val userPages = listOf(
    PageConfig("User", "/user", true, builder =TransientPageBuilder {
        userPage(it)
    }),
    PageConfig("Login", "/login", builder = CachedPageBuilder {
        loginPage(it)
    }),
    PageConfig("EditUser", "/user/edit", builder = TransientPageBuilder {
        editUserPage(it)
    }),
    PageConfig("Admin", "/admin", builder = TransientPageBuilder {
        adminPage(it)
    }),
    PageConfig("CreateUser", "/user/create", builder = TransientPageBuilder {
        signUpPage(it)
    }),
)