package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color

class TextComponent(t:String, c:Boolean = false) : Component {
    var text = t
    var center = c
    var scale = 1.0f
    var colour = Color.WHITE
    var front = true
}