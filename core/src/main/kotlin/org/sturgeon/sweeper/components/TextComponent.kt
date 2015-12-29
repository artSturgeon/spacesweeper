package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color

class TextComponent(t:String, c:Boolean = false, s:Float = 1.0f, col:Color = Color.WHITE) : Component {
    var text = t
    var center = c
    var scale = s
    var colour = col
}