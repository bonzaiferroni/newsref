package newsref.model.data

enum class NewsSection(override val title: String): TitleEnum {
    Unknown("Unknown"),
    General("General News and Events"),
    Sports("Sports"),
    Government("Government and Policy"),
    Entertainment("Arts and Entertainment"),
    Weather("Weather"),
    International("International"),
    Technology("Internet and Technology"),
    Business("Business and Markets"),
    Science("Science and Space"),
    Celebrity("Celebrity and Influencer"),
    Editorial("Editorial and Opinion"),
}