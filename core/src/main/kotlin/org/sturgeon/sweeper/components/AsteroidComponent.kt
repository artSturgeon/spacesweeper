package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component

class AsteroidComponent(var p:Int = 10, var s:Int = 1) : Component {
    var points = p
    var stage = s
}