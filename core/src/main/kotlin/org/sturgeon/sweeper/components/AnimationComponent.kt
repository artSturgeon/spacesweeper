package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation

class AnimationComponent(anim: Animation, running:Boolean = false) : Component {
    var anim = anim
    var running = running
    var stateTime = 0f
    var loop = true
}