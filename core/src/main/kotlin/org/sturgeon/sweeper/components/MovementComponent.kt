package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component

class MovementComponent(x: Float, y: Float) : Component {
    var velocityX: Float = x
    var velocityY: Float = y
}