package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import org.sturgeon.sweeper.components.AsteroidComponent
import org.sturgeon.sweeper.components.BulletComponent
import org.sturgeon.sweeper.components.PlayerComponent
import org.sturgeon.sweeper.components.PositionComponent


class CollisionSystem : EntitySystem() {

    lateinit private var players: ImmutableArray<Entity>
    lateinit private var bullets: ImmutableArray<Entity>
    lateinit private var asteroids: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine?) {

        players = engine!!.getEntitiesFor(Family.all(PlayerComponent::class.java).get())
        bullets = engine!!.getEntitiesFor(Family.all(BulletComponent::class.java).get())
        asteroids = engine!!.getEntitiesFor(Family.all(AsteroidComponent::class.java).get())

    }


    override fun update(deltaTime: Float) {

        //if (players.size() < 1) return
        if (bullets.size() < 1 || asteroids.size() < 1) return

        bulletsAndAsteroids()
    }

    private fun bulletsAndAsteroids() {

        //var playerPC = player.getComponent(PositionComponent::class.java)

        for (asteroid in asteroids) {
            var asteroidPC = asteroid.getComponent(PositionComponent::class.java)
            for (bullet in bullets) {
                var bulletPC = bullet.getComponent(PositionComponent::class.java)
                var r = Rectangle()
                Intersector.intersectRectangles(bulletPC.rect(), asteroidPC.rect(), r)

                if (r.width > 0) {
                    engine.removeEntity(asteroid)
                    engine.removeEntity(bullet)
                }
            }

        }

    }

}