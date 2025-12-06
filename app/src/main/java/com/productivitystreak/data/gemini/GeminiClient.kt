package com.productivitystreak.data.gemini

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.productivitystreak.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class WordOfTheDayResponse(
    val word: String,
    val definition: String,
    val example: String,
    val type: String? = null,
    val pronunciation: String? = null
)

@JsonClass(generateAdapter = true)
data class TeachingPlanResponse(
    val word: String,
    val definition: String,
    val analogy: String?,
    val mnemonic: String?,
    val example: String?,
    val practice: List<String>?
)

data class TeachingLesson(
    val word: String,
    val definition: String,
    val analogy: String,
    val mnemonic: String,
    val example: String,
    val practicePrompts: List<String>
)

class GeminiClient private constructor(private val context: android.content.Context) {

    private val model: GenerativeModel? = run {
        val apiKey = com.productivitystreak.data.config.ApiKeyManager.getApiKey(context)
        if (apiKey.isBlank()) {
            Log.w(TAG, "Gemini API key not configured - AI features will use fallback responses")
            null
        } else {
            Log.d(TAG, "Gemini API initialized successfully")
            GenerativeModel(
                modelName = MODEL_NAME,
                apiKey = apiKey
            )
        }
    }

    private val cache = com.productivitystreak.data.ai.AIResponseCache.getInstance()
    private val rateLimiter = com.productivitystreak.data.ai.RateLimiter.getInstance()

    suspend fun generateTeachingLesson(word: String, learnerContext: String?): TeachingLesson = withContext(Dispatchers.IO) {
        val fallback = fallbackTeachingLesson(word)
        val generativeModel = model ?: return@withContext fallback
        val focus = learnerContext?.takeIf { it.isNotBlank() } ?: "general mastery"
        val prompt = """
            You are a high-agency vocabulary coach. Teach the word "$word" to a busy professional who cares about $focus.
            Respond with ONLY valid JSON:
            {
              "word": "...",
              "definition": "short, modern explanation",
              "analogy": "memorable comparison or metaphor",
              "mnemonic": "a sticky memory trick",
              "example": "a vivid sentence",
              "practice": ["micro exercise 1", "micro exercise 2"]
            }
            Keep language mature and pragmatic. No markdown.
        """.trimIndent()

        return@withContext try {
            val response = generativeModel.generateContent(content { text(prompt) })
            val json = response.text?.cleanJsonBlock() ?: return@withContext fallback
            // Extract JSON object if wrapped in other text
            val jsonStart = json.indexOf("{")
            val jsonEnd = json.lastIndexOf("}")
            val cleanJson = if (jsonStart != -1 && jsonEnd != -1) {
                json.substring(jsonStart, jsonEnd + 1)
            } else {
                json
            }
            
            val adapter = moshi.adapter(TeachingPlanResponse::class.java)
            val plan = adapter.fromJson(cleanJson) ?: return@withContext fallback
            TeachingLesson(
                word = plan.word.ifBlank { word },
                definition = plan.definition.trim(),
                analogy = plan.analogy?.trim().takeUnless { it.isNullOrEmpty() }
                    ?: "Imagine $word as a strategic lever you pull to shift the situation in your favor.",
                mnemonic = plan.mnemonic?.trim().takeUnless { it.isNullOrEmpty() }
                    ?: "Think '$word' = '${word.take(3).uppercase()} advantage'.",
                example = plan.example?.trim().takeUnless { it.isNullOrEmpty() }
                    ?: "${word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} became the anchor of her weekly briefing.",
                practicePrompts = plan.practice?.takeIf { it.isNotEmpty() }
                    ?: listOf(
                        "Use '$word' in a sentence that describes today's priority.",
                        "Teach '$word' to a teammate in under 30 seconds."
                    )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate teaching lesson", e)
            fallback
        }
    }

    private fun fallbackTeachingLesson(word: String) = TeachingLesson(
        word = word,
        definition = "Use '$word' to describe deliberate, precise action—it's the opposite of winging it.",
        analogy = "Think of '$word' as the zoom lens you twist to bring an idea into crisp focus.",
        mnemonic = "Tie '$word' to '${word.take(3).uppercase()}': three letters, three beats to remember it.",
        example = "She chose '$word' framing so the client could internalize the concept instantly.",
        practicePrompts = listOf(
            "Write a two-sentence lesson using '$word' for a junior teammate.",
            "Record a 15-second voice note where you apply '$word' to your current project."
        )
    )

    private fun String.cleanJsonBlock(): String = this.trim()
        .removePrefix("```json")
        .removePrefix("```")
        .removeSuffix("```")
        .trim()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    suspend fun generateMotivationPrompt(prompt: String): String = withContext(Dispatchers.IO) {
        val generativeModel = model ?: return@withContext "Keep going! You're doing great."
        val response = generativeModel.generateContent(content { text(prompt) })
        response.text?.trim().takeUnless { it.isNullOrEmpty() } ?: "Keep going! You're doing great."
    }

    suspend fun generateHabitSuggestions(interests: String): List<String> = withContext(Dispatchers.IO) {
        val generativeModel = model ?: return@withContext getFallbackHabitSuggestions(interests)
        
        // Check rate limit
        if (!rateLimiter.acquirePermit()) {
            Log.w(TAG, "Rate limit exceeded for habit suggestions")
            return@withContext getFallbackHabitSuggestions(interests)
        }
        
        val prompt = """
            Generate 5 specific, actionable daily habits for someone interested in: $interests
            
            Requirements:
            - Each habit should take 5-30 minutes
            - Be specific and measurable
            - Focus on consistency over intensity
            - Make them practical for busy professionals
            - No generic advice like "exercise more"
            
            Format: One habit per line, no numbering or bullets
            
            Example:
            Write 3 sentences in a journal about today's key insight
            Read 10 pages from a book in your field
            Practice 5 minutes of focused breathing
        """.trimIndent()
        
        try {
            val response = generateWithRetry(generativeModel, prompt, maxRetries = 2)
            val habits = response?.lines()
                ?.map { it.trim().removePrefix("- ").removePrefix("* ").removePrefix("•").trim() }
                ?.filter { it.isNotBlank() && it.length > 10 }
                ?.take(5)
                ?: emptyList()
            
            if (habits.size >= 3) {
                return@withContext habits
            } else {
                return@withContext getFallbackHabitSuggestions(interests)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate habit suggestions", e)
            getFallbackHabitSuggestions(interests)
        }
    }
    
    private fun getFallbackHabitSuggestions(interests: String): List<String> {
        val categoryMap = mapOf(
            "fitness" to listOf(
                "Do 20 push-ups first thing in the morning",
                "Take a 15-minute walk during lunch break",
                "Stretch for 5 minutes before bed",
                "Drink 8 glasses of water throughout the day",
                "Track your meals in a simple food journal"
            ),
            "reading" to listOf(
                "Read 10 pages from a book before breakfast",
                "Summarize one article you read in 3 sentences",
                "Spend 15 minutes reading industry news",
                "Listen to an audiobook during your commute",
                "Share one interesting fact you learned today"
            ),
            "productivity" to listOf(
                "Plan tomorrow's top 3 priorities before bed",
                "Time-block your calendar for deep work",
                "Review and clear your inbox to zero",
                "Take a 5-minute break every hour",
                "Write down 3 wins from today"
            ),
            "mindfulness" to listOf(
                "Practice 5 minutes of focused breathing",
                "Write 3 things you're grateful for",
                "Meditate for 10 minutes in the morning",
                "Do a body scan before sleep",
                "Take 3 conscious breaths before meals"
            ),
            "learning" to listOf(
                "Learn one new word and use it in a sentence",
                "Watch a 10-minute educational video",
                "Practice a skill for 20 minutes",
                "Teach someone something you learned",
                "Review your notes from yesterday"
            )
        )
        
        // Find best match
        val category = categoryMap.keys.find { interests.contains(it, ignoreCase = true) }
        return category?.let { categoryMap[it] } ?: listOf(
            "Spend 15 minutes on focused work related to $interests",
            "Journal about your progress in $interests for 5 minutes",
            "Practice a skill in $interests for 20 minutes",
            "Read or watch content about $interests for 10 minutes",
            "Share something you learned about $interests with someone"
        )
    }

    suspend fun generateWordOfTheDay(interests: String = "general knowledge", forceRefresh: Boolean = false): com.productivitystreak.ui.state.vocabulary.VocabularyWord? = withContext(Dispatchers.IO) {
        val cacheKey = "word_of_day_$interests"
        
        // Check cache first
        if (!forceRefresh) {
            cache.get<com.productivitystreak.ui.state.vocabulary.VocabularyWord>(cacheKey)?.let {
                Log.d(TAG, "Returning cached Word of the Day")
                return@withContext it
            }
        }
        
        val generativeModel = model ?: return@withContext null
        
        // Check rate limit
        if (!rateLimiter.acquirePermit()) {
            Log.w(TAG, "Rate limit exceeded for Word of the Day")
            return@withContext cache.get<com.productivitystreak.ui.state.vocabulary.VocabularyWord>(cacheKey)
        }
        
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
            val json = response.text?.trim()
                ?.removePrefix("```json")
                ?.removePrefix("```")
                ?.removeSuffix("```")
                ?.trim() 
                ?: return@withContext null
            
            // Use Moshi for proper JSON parsing
            val adapter = moshi.adapter(WordOfTheDayResponse::class.java)
            val wordResponse = adapter.fromJson(json) ?: return@withContext null
            
            val result = com.productivitystreak.ui.state.vocabulary.VocabularyWord(
                word = wordResponse.word,
                definition = wordResponse.definition,
                example = wordResponse.example,
                addedToday = false
            )
            
            // Cache the result
            cache.put(cacheKey, result, com.productivitystreak.data.ai.AIResponseCache.WORD_OF_DAY_TTL, java.util.concurrent.TimeUnit.MILLISECONDS)
            Log.d(TAG, "Cached Word of the Day")
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate word of the day", e)
            null
        }
    }

    suspend fun generateBuddhaInsight(context: String = "general", forceRefresh: Boolean = false): String = withContext(Dispatchers.IO) {
        val cacheKey = "buddha_insight_$context"
        
        // Check cache first
        if (!forceRefresh) {
            cache.get<String>(cacheKey)?.let {
                Log.d(TAG, "Returning cached Buddha insight")
                return@withContext it
            }
        }
        
        val generativeModel = model ?: return@withContext getFallbackBuddhaInsight()
        
        // Check rate limit
        if (!rateLimiter.acquirePermit()) {
            Log.w(TAG, "Rate limit exceeded for Buddha insight")
            return@withContext cache.get<String>(cacheKey) ?: getFallbackBuddhaInsight()
        }
        
        val prompt = """
            You are a wise Buddhist monk and stoic philosopher. 
            Generate a profound, actionable insight about: $context
            
            Guidelines:
            - Keep it under 20 words
            - Be encouraging but not preachy
            - Use metaphors from nature or daily life
            - Make it practical and actionable
            - No generic platitudes
            - No quotation marks
            
            Example style: "Like water shaping stone, small daily actions carve your destiny."
        """.trimIndent()
        
        try {
            val response = generateWithRetry(generativeModel, prompt, maxRetries = 2)
            val result = response?.trim()?.removeSurrounding("\"") ?: getFallbackBuddhaInsight()
            
            // Validate response quality
            if (result.length < 10 || result.contains("error", ignoreCase = true)) {
                return@withContext getFallbackBuddhaInsight()
            }
            
            // Cache the result
            cache.put(cacheKey, result, com.productivitystreak.data.ai.AIResponseCache.BUDDHA_INSIGHT_TTL, java.util.concurrent.TimeUnit.MILLISECONDS)
            Log.d(TAG, "Cached Buddha insight: $result")
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate Buddha insight", e)
            getFallbackBuddhaInsight()
        }
    }
    
    private fun getFallbackBuddhaInsight(): String {
        val insights = listOf(
            "The obstacle is the way. Transform resistance into growth.",
            "Like bamboo, bend with challenges but never break your core.",
            "Small steps daily compound into extraordinary journeys.",
            "Your consistency today shapes your character tomorrow.",
            "Progress whispers while perfection shouts. Listen to whispers.",
            "The path reveals itself to those who take the first step.",
            "Discipline is choosing what you want most over what you want now.",
            "Every streak begins with a single committed day.",
            "Your future self is watching. Make them proud today.",
            "Mastery is patience applied consistently over time."
        )
        return insights.random()
    }
    
    private suspend fun generateWithRetry(
        model: GenerativeModel,
        prompt: String,
        maxRetries: Int = 3
    ): String? {
        repeat(maxRetries) { attempt ->
            try {
                val response = model.generateContent(content { text(prompt) })
                return response.text
            } catch (e: Exception) {
                Log.w(TAG, "Attempt ${attempt + 1} failed: ${e.message}")
                if (attempt < maxRetries - 1) {
                    kotlinx.coroutines.delay(1000L * (attempt + 1)) // Exponential backoff
                }
            }
        }
        return null
    }

    suspend fun generateJournalFeedback(entry: String): String = withContext(Dispatchers.IO) {
        val generativeModel = model ?: return@withContext getFallbackJournalFeedback()
        
        // Check rate limit
        if (!rateLimiter.acquirePermit()) {
            Log.w(TAG, "Rate limit exceeded for journal feedback")
            return@withContext getFallbackJournalFeedback()
        }
        
        val prompt = """
            You are a wise, compassionate mentor combining Buddhist wisdom and Stoic philosophy.
            
            Journal entry: "$entry"
            
            Provide a personalized 1-2 sentence reflection that:
            - Acknowledges their experience with empathy
            - Offers a practical insight or reframe
            - Uses metaphors from nature or daily life
            - Encourages growth without being preachy
            - Avoids generic praise like "Good job" or "Keep it up"
            
            Example style: "Your awareness of this pattern is the first crack in the wall. Now, like water finding its path, let action flow through that opening."
        """.trimIndent()
        
        try {
            val response = generateWithRetry(generativeModel, prompt, maxRetries = 2)
            val result = response?.trim() ?: getFallbackJournalFeedback()
            
            // Validate response quality
            if (result.length < 20 || result.contains("error", ignoreCase = true)) {
                return@withContext getFallbackJournalFeedback()
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate journal feedback", e)
            getFallbackJournalFeedback()
        }
    }
    
    private fun getFallbackJournalFeedback(): String {
        val feedbacks = listOf(
            "Your reflection shows self-awareness. That clarity is the compass for your next step.",
            "Notice how you're observing your patterns. This distance creates space for change.",
            "The fact you're writing this means you're already in motion. Trust the process.",
            "Your honesty here is courage in action. Small truths compound into transformation.",
            "This moment of reflection is planting seeds. Water them with consistent action.",
            "You're mapping your inner landscape. Each entry makes the path clearer.",
            "The struggle you describe is the friction that shapes you. Lean into it.",
            "Your awareness of this challenge is already shifting it. Keep observing.",
            "This entry captures a pivot point. Your future self will thank you for noticing.",
            "The patterns you see here are invitations to evolve. Accept them."
        )
        return feedbacks.random()
    }

    companion object {
        private const val TAG = "GeminiClient"
        private const val MODEL_NAME = "gemini-2.0-flash-exp-0827"

        @Volatile
        private var instance: GeminiClient? = null

        fun getInstance(context: android.content.Context): GeminiClient = instance ?: synchronized(this) {
            instance ?: GeminiClient(context.applicationContext).also { instance = it }
        }
    }
}
