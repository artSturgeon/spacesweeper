package org.sturgeon.sweeper.systems

import aurelienribon.tweenengine.Timeline
import aurelienribon.tweenengine.Tween
import com.badlogic.ashley.core.EntitySystem
import aurelienribon.tweenengine.TweenManager
import org.sturgeon.sweeper.Accessors.PositionAccessor
import org.sturgeon.sweeper.Accessors.VisualAccessor
import org.sturgeon.sweeper.components.PositionComponent
import org.sturgeon.sweeper.components.VisualComponent

class TweenSystem : EntitySystem() {

    private val tweenManager = TweenManager()

    init {
        Tween.registerAccessor(PositionComponent::class.java, PositionAccessor())
        Tween.registerAccessor(VisualComponent::class.java, VisualAccessor())
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