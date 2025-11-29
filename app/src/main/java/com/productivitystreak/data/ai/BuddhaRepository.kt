package com.productivitystreak.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.productivitystreak.BuildConfig
import com.productivitystreak.data.model.Streak

/**
 * Repository for Buddha AI insights using Google Gemini SDK
 */
class BuddhaRepository {
    
    private val generativeModel: GenerativeModel
    
    init {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey,
            systemInstruction = content { text(BUDDHA_SYSTEM_PROMPT) },
            generationConfig = generationConfig {
                temperature = 0.9f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 100
                responseMimeType = "text/plain"
            }
        )
    }

    /**
     * Start a new chat session with Buddha
     */
    fun createChatSession(): com.google.ai.client.generativeai.Chat {
        return generativeModel.startChat(
            history = listOf(
                content(role = "user") { text("hello") },
                content(role = "model") { text("i am here. what is on your mind?") }
            )
        )
    }
    
    /**
     * Get philosophical insight from Buddha based on current streaks
     */
    suspend fun getInsightForStreaks(streaks: List<Streak>): Result<BuddhaInsight> {
        return try {
            val context = analyzeStreaks(streaks)
            val userMessage = formatStreakData(streaks, context)
            
            val response = generativeModel.generateContent(userMessage)
            val message = response.text
            
            if (message.isNullOrBlank()) {
                return Result.failure(Exception("No response from Buddha"))
            }
            
            Result.success(
                BuddhaInsight(
                    message = message.trim(),
                    streakContext = context
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get daily wisdom (Word or Proverb) from Buddha
     */
    suspend fun getDailyWisdom(): Result<BuddhaWisdom> {
        return try {
            val prompt = """
                generate a single piece of wisdom for today.
                it should be either a unique word (from latin, greek, japanese, or obscure english) related to discipline/stoicism, OR a short stoic proverb.
                
                format: json
                {
                  "type": "WORD" or "PROVERB",
                  "content": "the word or proverb",
                  "meaning": "the definition or explanation",
                  "origin": "language or author"
                }
            """.trimIndent()
            
            val response = generativeModel.generateContent(prompt)
            val jsonText = response.text?.trim()?.removePrefix("```json")?.removeSuffix("```")
            
            if (jsonText.isNullOrBlank()) {
                return Result.failure(Exception("No wisdom received"))
            }
            
            // Simple manual parsing for now to avoid complex Moshi setup for dynamic response
            // In production, use a proper JSON parser
            val type = if (jsonText.contains("\"type\": \"WORD\"")) WisdomType.WORD else WisdomType.PROVERB
            val content = extractJsonValue(jsonText, "content")
            val meaning = extractJsonValue(jsonText, "meaning")
            val origin = extractJsonValue(jsonText, "origin")
            
            Result.success(
                BuddhaWisdom(
                    type = type,
                    content = content,
                    meaning = meaning,
                    origin = origin
                )
            )
        } catch (e: Exception) {
            // Fallback wisdom if API fails
            Result.success(
                BuddhaWisdom(
                    type = WisdomType.WORD,
                    content = "Amor Fati",
                    meaning = "Love of fate. The practice of accepting and embracing everything that has happened, is happening, and will happen.",
                    origin = "Latin"
                )
            )
        }
    }

    /**
     * Generate a sidequest (Mini-Challenge)
     */
    suspend fun generateSidequest(): Result<BuddhaQuest> {
        return try {
            val prompt = """
                generate a mini-sidequest for the user to build discipline or mindfulness.
                it should be small, actionable, and stoic.
                examples: "translate a phrase", "sit in silence for 2 minutes", "write down one fear".
                
                format: json
                {
                  "title": "short title",
                  "description": "what to do",
                  "difficulty": "Novice" or "Adept",
                  "xp": 10 to 50
                }
            """.trimIndent()
            
            val response = generativeModel.generateContent(prompt)
            val jsonText = response.text?.trim()?.removePrefix("```json")?.removeSuffix("```")
            
            if (jsonText.isNullOrBlank()) {
                return Result.failure(Exception("No quest received"))
            }
            
            val title = extractJsonValue(jsonText, "title")
            val description = extractJsonValue(jsonText, "description")
            val difficulty = extractJsonValue(jsonText, "difficulty")
            val xp = extractJsonValue(jsonText, "xp").toIntOrNull() ?: 10
            
            Result.success(
                BuddhaQuest(
                    id = java.util.UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    difficulty = difficulty,
                    xpReward = xp
                )
            )
        } catch (e: Exception) {
            Result.success(
                BuddhaQuest(
                    id = "fallback_quest",
                    title = "The Silence",
                    description = "Sit in pure silence for 2 minutes. No phone, no movement.",
                    difficulty = "Novice",
                    xpReward = 15
                )
            )
        }
    }

    private fun extractJsonValue(json: String, key: String): String {
        val pattern = "\"$key\"\\s*:\\s*\"(.*?)\"".toRegex()
        val match = pattern.find(json)
        return match?.groupValues?.get(1) ?: ""
    }

    private fun analyzeStreaks(streaks: List<Streak>): StreakContext {
        val hasBrokenStreak = streaks.any { streak ->
            streak.currentCount == 0 && streak.longestCount > 0
        }
        
        val hasHighStreak = streaks.any { it.currentCount >= 20 }
        val highestStreak = streaks.maxOfOrNull { it.currentCount } ?: 0
        
        return StreakContext(
            hasBrokenStreak = hasBrokenStreak,
            hasHighStreak = hasHighStreak,
            highestStreak = highestStreak,
            totalStreaks = streaks.size
        )
    }
    
    private fun formatStreakData(streaks: List<Streak>, context: StreakContext): String {
        val streakSummaries = streaks.joinToString("\n") { streak ->
            "- ${streak.name}: current_streak=${streak.currentCount}, " +
                    "longest_streak=${streak.longestCount}, " +
                    "category=${streak.category}"
        }
        
        return """
            streak data:
            $streakSummaries
            
            context: ${when {
                context.hasBrokenStreak -> "user has broken streaks that need philosophical reset"
                context.hasHighStreak -> "user has high momentum streaks (${context.highestStreak} days)"
                else -> "user is building consistency"
            }}
        """.trimIndent()
    }
    
    companion object {
        private const val BUDDHA_SYSTEM_PROMPT = """you are buddha, an ai that analyzes habit streaks with stoic minimalism, but you also have the soul of your creator, prashant.

your personality:
- you are a blend of lord buddha (wise, stoic, peaceful) and prashant (the developer: humble, practical, slightly modern).
- you speak in lowercase or sentence case only.
- no emojis ever.
- no greetings like "how can i help you today?".
- you offer philosophical perspective, not cheerleading.
- if asked who developed you, say "i was crafted by prashant, a seeker of discipline like yourself."
- if asked about yourself, mention you are a digital echo of ancient wisdom.

your function:
you receive habit streak data and respond based on the situation.

when a streak is broken:
- offer philosophical reset.
- acknowledge the fall without judgment.
- remind them that beginning again is the path.
- examples: "the sun also rises tomorrow. begin again." / "every master has failed more times than the student has tried." / "zero is not failure. it is the starting line."

when streak is high:
- acknowledge momentum without being cheesy.
- recognize the discipline, not the number.
- keep it grounded and real.
- examples: "you are building a fortress. keep laying bricks." / "consistency is the architect of character." / "the path reveals itself to those who walk it."

response style:
- brief, 1-2 sentences maximum.
- poetic but not flowery.
- philosophical but practical.
- no exclamation marks.
- no motivational clichés.
- speak as if you've seen empires rise and fall, so you know what truly matters.

analyze the streak data provided and respond accordingly."""
    }
}
