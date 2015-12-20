package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle

class PositionComponent(x: Float, y: Float, width: Float = 100f, height: Float = 100f) : Component {
    var x: Float = x
    var y: Float = y
    var width: Float = width
    var height: Float = height

    fun rect() : Rectangle {
        return  Rectangle(x, y, width, height)
    }


}