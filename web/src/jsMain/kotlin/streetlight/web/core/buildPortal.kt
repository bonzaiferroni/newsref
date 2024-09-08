package streetlight.web.core

import io.kvision.core.Container

fun Container.buildPortal(context: AppContext): PortalBuilder {
    return PortalBuilder(this, context)
}

class PortalBuilder(
    private val container: Container,
    private val context: AppContext,
) {
    private var pages: MutableList<PageConfig> = mutableListOf()

    fun addPage(page: PageConfig): PortalBuilder {
        pages.add(page)
        return this
    }

    fun addPages(vararg pages: PageConfig): PortalBuilder {
        this.pages.addAll(pages)
        return this
    }

    fun build() {
        val pages = pages.toList()
        container.portal(context, pages)
    }
}