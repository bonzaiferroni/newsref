package newsref.krawly.agents

import it.skrape.selects.DocElement
import newsref.model.core.ArticleType

data class ContentInfo(
	val text: String,
	val wordCount: Int,
	val typeSets: Map<ArticleType, Int>
)

class ElementReader {

	fun read(element: DocElement): ContentInfo? {
		// is all link
		val firstLinkText = element.eachLink.keys.firstOrNull()?.trim()
		val text = element.text.trim()
		if (text == firstLinkText) return null

		return readChars(text)
	}

	private fun readChars(text: String): ContentInfo? {
		if (text.isEmpty() || isWordCharacter(text[text.length - 1])) return null

		val wordBuilder = StringBuilder()
		val articleSets = mutableMapOf(
			ArticleType.NEWS to Pair(newsSet, 0),
			// ArticleType.HELP to Pair(helpSet, 0),
			ArticleType.POLICY to Pair(policySet, 0),
			// ArticleType.JOURNAL to Pair(journalSet, 0),
		)
		var wordCount = 0
		var signalScore = 0
		var noiseScore = 0
		var lastWord = ""
		for (char in text) {
			if (signalChars.contains(char)) signalScore++
			if (noiseChars.contains(char)) noiseScore++
			if (isWordCharacter(char)) {
				wordBuilder.append(char.lowercaseChar())
			} else {
				val word = wordBuilder.toString()
				wordBuilder.clear()
				wordCount++
				val phrase = "$lastWord $word"
				lastWord = word

				if (phrase in noiseWords) noiseScore++

				for ((type, pair) in articleSets) {
					val (signals, score) = pair
					if (phrase in signals) {
						articleSets[type] = Pair(signals, score + 1)
						break
					}
				}
			}
		}

		signalScore += articleSets[ArticleType.NEWS]?.second ?: 0

		if (noiseScore > signalScore) return null

		return ContentInfo(
			text = text,
			wordCount = wordCount,
			typeSets = articleSets.mapValues { it.value.second }
		)
	}
}

private val wordCharacters = setOf('\'', '’', '-')

private fun isWordCharacter(char: Char) = char.isLetterOrDigit() || wordCharacters.contains(char)

private val signalChars = setOf(
	'.', ',', ';', '—', '–', '-', '‘', '’', '“', '”',
	'"', '\'', '?', '!'
)

private val noiseChars = setOf('|')

private val noiseWords = setOf(
	"breaking news", "latest updates", "subscribe now", "trending stories",
	"watch live", "sponsored content", "read more", "click here",
	"special offer", "related articles", "browser preferences"
)

private val newsSet = setOf(
	"officials say", "according to", "report shows", "sources confirm",
	"investigation continues", "witnesses describe", "experts warn",
	"authorities believe", "analysts predict", "confirmed reports",
	"developing story"
)

private val helpSet = setOf(
	"troubleshooting tips", "frequently asked", "contact support",
	"follow instructions", "learn more", "quick fix", "user guide",
	"help center", "technical support", "error message",
)

private val policySet = setOf(
	"personal information", "data collection", "third parties",
	"user consent", "privacy policy", "security measures",
	"cookie usage", "legal requirements", "data protection",
	"information sharing",
)

private val journalSet = setOf(
	"research findings", "data analysis", "previous studies",
	"significant results", "experimental design", "literature review",
	"methodology section", "control group", "study limitations",
	"statistical significance",
)