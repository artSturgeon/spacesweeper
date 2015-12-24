package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component

class MovementComponent(x: Float, y: Float, angular:Float = 0f) : Component {
    var velocityX = x
    var velocityY = y
    val velocityAngular = angular
}