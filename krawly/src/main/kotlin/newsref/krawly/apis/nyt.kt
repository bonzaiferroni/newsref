package newsref.krawly.apis

import kotlinx.serialization.Serializable

//@Serializable
//data class Article(
//    val news_desk: String,
//    val section_name: String,
//    val byline: Byline,
//    val type_of_material: String,
//    val _id: String,
//    val word_count: Int,
//    val uri: String
//)
//
//@Serializable
//data class Byline(
//    val original: String,
//    val person: List<Person>,
//    val organization: String
//)
//
//@Serializable
//data class Headline(
//    val main: String,
//    val kicker: String,
//    val content_kicker: String,
//    val print_headline: String,
//    val name: String,
//    val seo: String,
//    val sub: String
//)
//
//@Serializable
//data class Keyword(
//    val name: String,
//    val value: String,
//    val rank: Int,
//    val major: String
//)
//
//@Serializable
//data class Multimedia(
//    val rank: Int,
//    val subtype: String,
//    val caption: String,
//    val credit: String,
//    val type: String,
//    val url: String,
//    val height: Int,
//    val width: Int,
//    val legacy: Legacy,
//    val crop_name: String
//)
//
//@Serializable
//data class Legacy(
//    val xlarge: String,
//    val xlargewidth: Int,
//    val xlargeheight: Int
//)
//
//@Serializable
//data class Person(
//    val firstname: String,
//    val middlename: String,
//    val lastname: String,
//    val qualifier: String,
//    val title: String,
//    val role: String,
//    val organization: String,
//    val rank: Int
//)