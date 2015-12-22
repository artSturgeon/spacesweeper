package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.components.BoundsCheckComponent
import org.sturgeon.sweeper.components.MovementComponent
import org.sturgeon.sweeper.components.PositionComponent
import org.sturgeon.sweeper.components.VisualComponent


class StarfieldSystem(var i:Float) : IntervalSystem(i) {


    override fun updateInterval() {
        var star = Entity()

        var t = Texture(Assets.STAR)

        var pc = PositionComponent(Assets.VIEWPORT_WIDTH+100,
                MathUtils.random(0f, Assets.VIEWPORT_HEIGHT),
                t.width.toFloat(), t.height.toFloat())

        var scale = MathUtils.random(0.1f, 1.2f)
        pc.scaleX = scale
        pc.scaleY = scale

        star.add(pc)

        star.add(VisualComponent(t))
        star.add(MovementComponent(MathUtils.random(-300f, -100f), 0f))
        star.add(BoundsCheckComponent())

        engine.addEntity(star)

    }


}