package org.sturgeon.sweeper.systems

import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.equations.Sine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.math.MathUtils
import org.sturgeon.sweeper.Accessors.PositionAccessor
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.Mappers
import org.sturgeon.sweeper.components.PanelComponent

class IncidentalSystem : IntervalSystem(6f) {

    var moveIn = false

    override fun updateInterval() {

        var panels = engine.getEntitiesFor(Family.all(PanelComponent::class.java).get())
        var panel = panels.get(MathUtils.random(0, panels.size()-1))

        var pc = Mappers.positionMapper.get(panel)

        var pos = pc.x + pc.width/2

        var targetWidth = 8f
        if (moveIn) {
            targetWidth = 65f
        }

        var widthTween = Tween.to(pc, PositionAccessor.WIDTH, 2f).target(targetWidth)
        var xTween = Tween.to(pc, PositionAccessor.POSITION, 2f).target(pos - targetWidth/2, pc.y)
        engine.getSystem(TweenSystem::class.java).addTween(widthTween)
        engine.getSystem(TweenSystem::class.java).addTween(xTween)

        moveIn = !moveIn
    }


}