package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component

class HealthComponent(h:Int, f: () -> Unit) : Component {
    var health = h
    var func: () -> Unit = f
}