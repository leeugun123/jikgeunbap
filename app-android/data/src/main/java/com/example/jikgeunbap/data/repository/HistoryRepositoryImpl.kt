package com.example.jikgeunbap.data.repository

import android.content.Context
import com.example.jikgeunbap.domain.model.HistoryEntry
import com.example.jikgeunbap.domain.model.Restaurant
import com.example.jikgeunbap.domain.repository.HistoryRepository
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val context: Context
) : HistoryRepository {

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override suspend fun getHistory(): List<HistoryEntry> {
        val arr = JSONArray(prefs.getString(KEY_HISTORY, "[]") ?: "[]")
        return (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            val tagsArr = obj.getJSONArray("tags")
            HistoryEntry(
                restaurant = Restaurant(
                    id       = obj.getLong("id"),
                    name     = obj.getString("name"),
                    category = obj.getString("category"),
                    distance = obj.getInt("distance"),
                    rating   = obj.optDouble("rating", 0.0),
                    tags     = (0 until tagsArr.length()).map { j -> tagsArr.getString(j) }
                ),
                recommendedAt = obj.getLong("recommendedAt")
            )
        }
    }

    override suspend fun addHistory(entry: HistoryEntry) {
        val list = getHistory().toMutableList().also { it.add(entry) }
        val trimmed = if (list.size > 50) list.drop(list.size - 50) else list
        val arr = JSONArray()
        trimmed.forEach { h ->
            arr.put(JSONObject().apply {
                put("id", h.restaurant.id)
                put("name", h.restaurant.name)
                put("category", h.restaurant.category)
                put("distance", h.restaurant.distance)
                put("rating", h.restaurant.rating)
                put("tags", JSONArray().also { ta -> h.restaurant.tags.forEach { ta.put(it) } })
                put("recommendedAt", h.recommendedAt)
            })
        }
        prefs.edit().putString(KEY_HISTORY, arr.toString()).apply()
    }

    override suspend fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    companion object {
        private const val PREF_NAME  = "jikgeunbap_history"
        private const val KEY_HISTORY = "history"
    }
}
