




fun Container.catalogPage(context: AppContext): PortalEvents? {
    val catalogModel = CatalogModel()
    return null
}

class CatalogModel(): ViewModel() {
    private val _state = MutableStateFlow(CatalogState())
    val state = _state.asStateFlow()
}

class CatalogState() {

}




val catalog = PageConfig("Catalog", "/user/catalog", builder = TransientPageBuilder {
        catalogPage(it)
    })
