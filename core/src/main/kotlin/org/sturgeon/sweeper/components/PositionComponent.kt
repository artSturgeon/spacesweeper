package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle

class PositionComponent(x: Float, y: Float, width: Float = 100f, height: Float = 100f) : Component {
    var x: Float = x
    var y: Float = y
    var width: Float = width
    var height: Float = height
    var angle: Float = 0f

    var originX = -1f
    var originY = -1f

    fun originX(): Float {
        if (originX >= 0)
            return originX
        return width/2
    }

    fun originY(): Float {
        if (originY >= 0)
            return originY
        return height/2
    }

    fun rect() : Rectangle {
        return  Rectangle(x, y, width, height)
    }


}