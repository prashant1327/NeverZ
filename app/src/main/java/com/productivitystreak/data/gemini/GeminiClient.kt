package com.productivitystreak.data.gemini

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.productivitystreak.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiClient private constructor() {

    private val model: GenerativeModel? = BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() }?.let { key ->
        GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = key
        )
    }

    suspend fun generateMotivationPrompt(prompt: String): String = withContext(Dispatchers.IO) {
        val generativeModel = model ?: return@withContext "Keep going! You're doing great."
        val response = generativeModel.generateContent(content { text(prompt) })
        response.text?.trim().takeUnless { it.isNullOrEmpty() } ?: "Keep going! You're doing great."
    }

    suspend fun generateHabitSuggestions(interests: String): List<String> = withContext(Dispatchers.IO) {
        val generativeModel = model ?: return@withContext emptyList()
        val prompt = "Suggest 5 simple, daily habits for someone interested in: $interests. Format as a simple list, one per line, no numbering or bullets."
        try {
            val response = generativeModel.generateContent(content { text(prompt) })
            response.text?.lines()
                ?.map { it.trim().removePrefix("- ").removePrefix("* ").trim() }
                ?.filter { it.isNotBlank() }
                ?.take(5)
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun generateWordOfTheDay(interests: String = "general knowledge"): com.productivitystreak.ui.state.vocabulary.VocabularyWord? = withContext(Dispatchers.IO) {
        val generativeModel = model ?: return@withContext null
        val prompt = """
            Generate a unique, sophisticated 'Word of the Day' for someone interested in $interests.
            Return ONLY a valid JSON object with the following fields:
            {
                "word": "The Word",
                "definition": "A concise definition.",
                "example": "A sentence using the word.",
                "type": "noun/verb/adjective",
                "pronunciation": "/phonetic/"
            }
            Do not include markdown formatting like ```json. Just the raw JSON string.
        """.trimIndent()

        try {
            val response = generativeModel.generateContent(content { text(prompt) })
            val json = response.text?.trim()?.removePrefix("```json")?.removePrefix("```")?.removeSuffix("```")?.trim() ?: return@withContext null
            
            // Simple manual parsing to avoid adding Gson/Moshi dependency inside this class if possible, 
            // or better, use a regex or simple string manipulation if the structure is guaranteed.
            // But since we have Moshi in the app, let's just return the raw values or a map.
            // For safety/speed, let's do a quick regex parse.
            
            val word = extractJsonField(json, "word")
            val definition = extractJsonField(json, "definition")
            val example = extractJsonField(json, "example")
            val type = extractJsonField(json, "type") // We might not use this yet in the entity but good to have
            // We'll map it to VocabularyWord. Note: VocabularyWord might not have 'type' or 'pronunciation' fields yet.
            // We should check VocabularyWord definition. For now, we fit it into existing fields.
            
            if (word.isNotBlank() && definition.isNotBlank()) {
                com.productivitystreak.ui.state.vocabulary.VocabularyWord(
                    word = word,
                    definition = definition,
                    example = example,
                    addedToday = false // It's a suggestion, not added yet
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun generateBuddhaInsight(context: String = "general"): String = withContext(Dispatchers.IO) {
        val generativeModel = model ?: return@withContext "The obstacle is the way."
        val prompt = "Give me a short, profound, stoic or buddhist insight about $context. Max 20 words. No quotes."
        try {
            val response = generativeModel.generateContent(content { text(prompt) })
            response.text?.trim() ?: "The obstacle is the way."
        } catch (e: Exception) {
            "The obstacle is the way."
        }
    }

    private fun extractJsonField(json: String, field: String): String {
        val pattern = "\"$field\"\\s*:\\s*\"(.*?)\"".toRegex()
        return pattern.find(json)?.groupValues?.get(1) ?: ""
    }

    companion object {
        private const val MODEL_NAME = "models/gemini-2.5-flash"

        @Volatile
        private var instance: GeminiClient? = null

        fun getInstance(): GeminiClient = instance ?: synchronized(this) {
            instance ?: GeminiClient().also { instance = it }
        }
    }
}
