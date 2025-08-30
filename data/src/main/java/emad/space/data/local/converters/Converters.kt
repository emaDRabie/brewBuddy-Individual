package emad.space.data.local.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

object Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    @JvmStatic
    fun listToJson(list: List<String?>?): String? {
        if (list == null) return null
        return json.encodeToString(ListSerializer(String.serializer().nullable), list)
    }

    @TypeConverter
    @JvmStatic
    fun jsonToList(jsonStr: String?): List<String?>? {
        if (jsonStr == null) return null
        return json.decodeFromString(ListSerializer(String.serializer().nullable), jsonStr)
    }
}