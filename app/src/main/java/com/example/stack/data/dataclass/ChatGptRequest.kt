package com.example.stack.data.dataclass

import com.squareup.moshi.Json

data class ChatGptRequest(
    @Json(name = "model")
    val model: String = "text-davinci-003",
    @Json(name = "prompt")
    val prompt: String,
    @Json(name = "temperature")
    val temperature: Double = 0.5,
    @Json(name = "max_tokens")
    val maxTokens: Int = 1000,
    @Json(name = "top_p")
    val topP: Double = 1.0,
    @Json(name = "frequency_penalty")
    val frequencyPenalty: Double = 0.0,
    @Json(name = "presence_penalty")
    val presencePenalty: Double = 0.0
)


data class ChatGptResponse(
    @Json(name = "warning")
    val warning: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "object")
    val objectName: String,
    @Json(name = "created")
    val created: Long,
    @Json(name = "model")
    val model: String,
    @Json(name = "choices")
    val choices: List<ChatGptChoice>,
    @Json(name = "usage")
    val usage: ChatGptUsage
)

data class ChatGptChoice(
    @Json(name = "text")
    val text: String,
    @Json(name = "index")
    val index: Int,
    @Json(name = "logprobs")
    val logProbs: String?,
    @Json(name = "finish_reason")
    val finishReason: String
)

data class ChatGptUsage(
    @Json(name = "prompt_tokens")
    val promptTokens: Int,
    @Json(name = "completion_tokens")
    val completionTokens: Int,
    @Json(name = "total_tokens")
    val totalTokens: Int
)
