package net.sinender.common.utils

import com.google.gson.*
import net.minestom.server.entity.EntityType
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import java.lang.reflect.Type

class ActionTypeAdapter : JsonSerializer<Action>, JsonDeserializer<Action> {
    val gson = GsonBuilder().create()
    override fun serialize(src: Action?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return gson.toJsonTree(src)
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Action {
        if (json == null || !json.isJsonObject) {
            throw JsonParseException("JsonElement is null")
        }
        val jsonObject = json.asJsonObject
        val enum = jsonObject.get("enum").asString
        val actionClass = ActionEnum.valueOf(enum).clazz
        val action = gson.fromJson(json, actionClass)
        return action
    }

}
