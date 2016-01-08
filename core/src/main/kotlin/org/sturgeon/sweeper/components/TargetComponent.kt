package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class TargetComponent(t: Vector2, tr: () -> Unit) : Component {
    var target = t
    var targetReached = tr
}