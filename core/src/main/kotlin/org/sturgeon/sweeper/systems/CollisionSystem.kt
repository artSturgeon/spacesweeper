package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import javafx.scene.shape.HLineTo
import org.sturgeon.sweeper.PlayScreen
import org.sturgeon.sweeper.World
import org.sturgeon.sweeper.components.*


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
        if (players.size() > 0 && asteroids.size() > 0) stationAndAsteroids()
        if (bullets.size() > 0 && asteroids.size() > 0) bulletsAndAsteroids()

    }


    private fun stationAndAsteroids() {
        var station = players.get(0)
        var stationPC = station.getComponent(PositionComponent::class.java)

        for (asteroid in asteroids) {
            var asteroidPC = asteroid.getComponent(PositionComponent::class.java)
            var r = Rectangle()
            Intersector.intersectRectangles(stationPC.rect(), asteroidPC.rect(), r)
            if (r.width > 0) {
                engine.removeEntity(asteroid)
                var hc = station.getComponent(HealthComponent::class.java)
                hc.health -= 50

                if (hc.health <= 0) {
                    // game over
                    hc.func()
                }
            }
        }
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
                    World.score += 10
                    engine.removeEntity(asteroid)
                    engine.removeEntity(bullet)
                }
            }

        }

    }

}