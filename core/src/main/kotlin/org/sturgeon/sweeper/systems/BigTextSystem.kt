package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.components.BoundsCheckComponent
import org.sturgeon.sweeper.components.MovementComponent
import org.sturgeon.sweeper.components.PositionComponent
import org.sturgeon.sweeper.components.TextComponent

class BigTextSystem(i:Float) : IntervalSystem(i) {

    override fun updateInterval() {
        if (MathUtils.random(1,100) < 25) {
            var msg = Assets.SOME_TEXT.get(MathUtils.random(0, Assets.SOME_TEXT.size - 1))
            addBigText(msg)
        }
    }

    fun addBigText(msg: String) {
        engine.addEntity(Entity().apply {
            add(PositionComponent(Assets.VIEWPORT_WIDTH, 500f))
            add(MovementComponent(-850f, 0f))

            add(TextComponent(msg).apply {
                scale = 16.0f
                colour = Color(223 / 255f, 113 / 255f, 38 / 255f, 0.75f)
                front = false
            })
            add(BoundsCheckComponent(8000f))
        })
    }
}
