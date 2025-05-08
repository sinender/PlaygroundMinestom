package net.sinender.utils

import com.google.gson.*
import net.minestom.server.entity.EntityType
import java.lang.reflect.Type

class EntityTypeTypeAdapter : JsonSerializer<EntityType>, JsonDeserializer<EntityType> {
    override fun serialize(src: EntityType?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.name())
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): EntityType? {
        return json?.asString?.let { EntityType.values().find { e -> e.name() == it } }
    }
}