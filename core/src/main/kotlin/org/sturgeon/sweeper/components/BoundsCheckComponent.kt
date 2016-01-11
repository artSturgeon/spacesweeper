package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component

class BoundsCheckComponent(override:Float = 0f, callback: () -> Unit = { }) : Component {
    var override = override
    var callback = callback
}