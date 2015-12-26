package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation

class AnimationComponent(anim: Animation, speed:Float) : Component {
    var anim = anim
    var animSpeed = speed
}