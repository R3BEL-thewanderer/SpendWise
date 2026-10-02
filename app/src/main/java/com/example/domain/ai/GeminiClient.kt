package com.example.domain.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {

    private const val TAG = "SpendWiseGemini"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Checks if a real Gemini API key is configured.
     */
    fun isKeyConfigured(): Boolean {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotBlank() && !key.equals("MY_GEMINI_API_KEY", ignoreCase = true)
        } catch (_: Throwable) {
            false
        }
    }

    /**
     * Calls Gemini 3.5 Flash with the user's query and verified financial context.
     */
    suspend fun generateFinancialAdvice(
        systemContext: String,
        userQuery: String
    ): String? = withContext(Dispatchers.IO) {
        if (!isKeyConfigured()) return@withContext null

        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            val url = "$BASE_URL/$MODEL:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply { put("text", "$systemContext\n\nUser Question: $userQuery") })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val generationConfig = JSONObject().apply {
                    put("temperature", 0.4)
                    put("maxOutputTokens", 400)
                }
                put("generationConfig", generationConfig)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(TAG, "Gemini API error code: ${response.code}")
                    return@withContext null
                }
                val respString = response.body?.string() ?: return@withContext null
                val respJson = JSONObject(respString)
                val candidates = respJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", null)
                    }
                }
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Gemini call exception: ${e.message}")
            null
        }
    }
}
