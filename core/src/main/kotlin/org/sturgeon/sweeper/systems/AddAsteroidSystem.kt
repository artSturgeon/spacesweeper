package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.components.*


class AddAsteroidSystem(var i:Float) : EntitySystem() {

    var interval = i
    private var currentTime = 0f

    override fun update(deltaTime: Float) {
        currentTime += deltaTime
        if (currentTime >= interval) {
            addAsteroid()
            currentTime = 0f
        }
    }

    fun addAsteroid() {
        var asteroid = Entity()

        var t = Texture(Assets.ASTEROID)

        var pc = PositionComponent(Assets.VIEWPORT_WIDTH + 100,
                MathUtils.random(100f, Assets.VIEWPORT_HEIGHT),
                t.width.toFloat(), t.height.toFloat())
        var scale = MathUtils.random(0.3f, 1.0f)
        pc.apply { scaleX = scale; scaleY = scale }

        asteroid.add(pc)
        asteroid.add(VisualComponent(t))
        asteroid.add(BoundsCheckComponent())
        asteroid.add(MovementComponent(MathUtils.random(-100f, -25f), 0f, MathUtils.random(-50f, 50f)))
        asteroid.add(AsteroidComponent())
        asteroid.add(CollisionComponent())
        engine.addEntity(asteroid)
    }

}