package com.example.jikgeunbap.data.repository

import android.content.Context
import com.example.jikgeunbap.domain.model.Restaurant
import com.example.jikgeunbap.domain.repository.FavoriteRepository
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val context: Context
) : FavoriteRepository {

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override suspend fun getFavorites(): List<Restaurant> {
        val jsonStr = prefs.getString(KEY_FAVORITES, "[]") ?: "[]"
        return parseList(JSONArray(jsonStr))
    }

    override suspend fun addFavorite(restaurant: Restaurant) {
        val list = getFavorites().toMutableList()
        if (list.none { it.id == restaurant.id }) {
            list.add(restaurant)
            saveList(list)
        }
    }

    override suspend fun removeFavorite(restaurantId: Long) {
        saveList(getFavorites().filter { it.id != restaurantId })
    }

    override suspend fun isFavorite(restaurantId: Long): Boolean =
        getFavorites().any { it.id == restaurantId }

    private fun parseList(arr: JSONArray): List<Restaurant> =
        (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            val tagsArr = obj.getJSONArray("tags")
            Restaurant(
                id       = obj.getLong("id"),
                name     = obj.getString("name"),
                category = obj.getString("category"),
                distance = obj.getInt("distance"),
                rating   = obj.optDouble("rating", 0.0),
                tags     = (0 until tagsArr.length()).map { j -> tagsArr.getString(j) }
            )
        }

    private fun saveList(list: List<Restaurant>) {
        val arr = JSONArray()
        list.forEach { r ->
            arr.put(JSONObject().apply {
                put("id", r.id)
                put("name", r.name)
                put("category", r.category)
                put("distance", r.distance)
                put("rating", r.rating)
                put("tags", JSONArray().also { ta -> r.tags.forEach { ta.put(it) } })
            })
        }
        prefs.edit().putString(KEY_FAVORITES, arr.toString()).apply()
    }

    companion object {
        private const val PREF_NAME   = "jikgeunbap_favorites"
        private const val KEY_FAVORITES = "favorites"
    }
}
