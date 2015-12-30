package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.components.*


class AddAsteroidSystem(var i:Float) : IntervalSystem(i) {

    override fun updateInterval() {
        var asteroid = Entity()

        var t = Texture(Assets.ASTEROID)

        asteroid.add(PositionComponent(Assets.VIEWPORT_WIDTH + 100,
                MathUtils.random(100f, Assets.VIEWPORT_HEIGHT),
                t.width.toFloat(), t.height.toFloat()))

        asteroid.add(VisualComponent(t))
        asteroid.add(BoundsCheckComponent())
        asteroid.add(MovementComponent(MathUtils.random(-100f, -25f), 0f, MathUtils.random(-50f, 50f)))
        asteroid.add(AsteroidComponent())

        engine.addEntity(asteroid)
    }

}