package org.sturgeon.sweeper.systems

import aurelienribon.tweenengine.Timeline
import aurelienribon.tweenengine.Tween
import com.badlogic.ashley.core.EntitySystem
import aurelienribon.tweenengine.TweenManager
import org.sturgeon.sweeper.Accessors.PositionAccessor
import org.sturgeon.sweeper.components.PositionComponent

class TweenSystem : EntitySystem() {

    private val tweenManager = TweenManager()

    init {
        Tween.registerAccessor(PositionComponent::class.java, PositionAccessor())
    }

    fun addTween(t: Tween) {
        t.start(tweenManager)
    }

    fun addTimeline(t: Timeline) {
        t.start(tweenManager)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        tweenManager.update(deltaTime)
    }

}