package newsref.db

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import kotlin.text.set

// "../.env"

//fun readEnvFromDirectory(path: String): Environment =
//    DotenvEnvironment(dotenv() { directory = path })
//
//fun readEnvFromFile(path: String, file: String): Environment = dotenv() {
//    filename = file
//    directory = path
//}.let { DotenvEnvironment(it) }
//
//fun readEnvFromText(text: String): Environment = MapEnvironment(text)
//
//interface Environment {
//    fun read(key: String): String
//}
//
//class MapEnvironment(content: String): Environment {
//    private val map = mutableMapOf<String, String>()
//
//    init {
//        val lines = content.split('\n')
//        for (line in lines) {
//            val values = line.trim().split('=')
//            if (values.size != 2) continue
//            map[values[0]] = values[1]
//        }
//    }
//
//    override fun read(key: String) = map.getValue(key)
//}
//
//class DotenvEnvironment(private val dotenv: Dotenv): Environment {
//    override fun read(key: String): String = dotenv[key]
//}