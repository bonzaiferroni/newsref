package newsref.web

object Css {
    // layout
    val col_default = "flex flex-col space-y-3"
    val col_group = "flex flex-col space-y-1"
    val row_default = "flex flex-row space-x-3"
    val row_group = "flex flex-row w-full space-x-1"
    val grow = "flex-1"
    val expand = "w-full"

    // content
    val content = "_content absolute left-0 right-0 w-full max-w-5xl mx-auto p-5 " +
            "box-border border-l border-r border-b border-gray-900 rounded-b-3xl " +
            "transition-all duration-300 ease-out " +
            "bg-paper/25"
    val content_parent = "relative w-full h-full"

    // navbar
    val navbar = "flex bg-navbar justify-between items-center p-2.5 pl-5"
    val navbar_link = "uppercase text-sm transition-all duration-200 rounded-full " +
            "hover:text-white hover:bg-white/10"
    val navbar_link_active = "bg-white/10"
    val navbar_logo = "w-7 glow-effect-color"
    val navbar_logo_i = "glow-effect text-yellow-400"

    // misc
    val text_muted = "text-gray-400"
    val text_shadow_none = "text-shadow-none"
}