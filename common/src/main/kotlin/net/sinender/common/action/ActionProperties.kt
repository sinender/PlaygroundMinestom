package net.sinender.common.action

import org.reflections.Reflections
import kotlin.reflect.KClass

object ActionProperties {
    val entries = mutableListOf<ActionProperty<*>>()

    init {
        val reflections = Reflections("net.sinender.common.action.properties")
        val propertyClasses = reflections.getSubTypesOf(ActionProperty::class.java)
        for (propertyClass in propertyClasses) {
            try {
                val property = propertyClass.getDeclaredConstructor().newInstance()
                entries.add(property)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun <T : ActionProperty<*>> getActionProperty(annotation: KClass<out Annotation>): T? {
        for (entry in entries) {
            if (entry.annotation == annotation) {
                return entry as T
            }
        }
        return null
    }
}