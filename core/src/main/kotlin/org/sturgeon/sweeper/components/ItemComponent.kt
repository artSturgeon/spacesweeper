package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component

enum class ItemType {
    STATION_HEALTH,
    FIRE_UP
}

class ItemComponent(type:ItemType) : Component {
    var type = type
}