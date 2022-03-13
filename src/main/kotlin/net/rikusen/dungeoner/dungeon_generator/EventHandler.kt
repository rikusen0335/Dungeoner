package net.rikusen.dungeoner.dungeon_generator

/**
 * Just a temporary implementation for C# EventHandler.
 *
 * Reference: https://stackoverflow.com/questions/62289168/kotlin-equivalent-of-c-sharp-events
 */
class EventHandler<T> {
    private val observers = mutableSetOf<(T) -> Unit>()

    operator fun plusAssign(observer: (T) -> Unit) {
        observers.add(observer)
    }

    operator fun minusAssign(observer: (T) -> Unit) {
        observers.remove(observer)
    }

    operator fun invoke(value: T) {
        for (observer in observers)
            observer(value)
    }
}