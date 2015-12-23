package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component


class UpdatingTextComponent(t:String, c:Boolean, f:() -> String): Component {
    var text = t
    var center = c
    var updateText = f
    //f:() -> String
}