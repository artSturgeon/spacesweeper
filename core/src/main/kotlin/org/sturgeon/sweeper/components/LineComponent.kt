package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2


class LineComponent(start: Vector2, end: Vector2) : Component {
    var lineStart = start
    var lineEnd = end
}