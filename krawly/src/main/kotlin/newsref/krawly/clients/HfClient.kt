package newsref.krawly.clients

import io.github.cdimascio.dotenv.dotenv

class HfClient(
	model: String
) {
	private val client = AiClient(
		url = "https://api-inference.huggingface.co/models/$model"
	)

	private val token =  dotenv() { directory = "../.env" }["HF_KEY"]

	fun request(text: String) {
		
	}
}

data class HfEmbeddingsResponse(
	val status: Int,
	val error: String,
	val vector: FloatArray
)