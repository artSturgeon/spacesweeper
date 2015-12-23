package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component

class TextComponent(t:String, c:Boolean = false ) : Component {
    var text = t
    var center = c

}