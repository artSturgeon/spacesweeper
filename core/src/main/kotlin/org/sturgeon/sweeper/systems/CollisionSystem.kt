package org.sturgeon.sweeper.systems

import aurelienribon.tweenengine.BaseTween
import aurelienribon.tweenengine.Timeline
import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenCallback
import aurelienribon.tweenengine.equations.Bounce
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import javafx.scene.shape.HLineTo
import org.sturgeon.sweeper.Accessors.PositionAccessor
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.PlayScreen
import org.sturgeon.sweeper.World
import org.sturgeon.sweeper.components.*
import org.sturgeon.sweeper.entities.Particle


class CollisionSystem(ps:PlayScreen) : EntitySystem() {

    lateinit private var players: ImmutableArray<Entity>
    lateinit private var bullets: ImmutableArray<Entity>
    lateinit private var asteroids: ImmutableArray<Entity>
    lateinit private var astronauts: ImmutableArray<Entity>
    lateinit private var items: ImmutableArray<Entity>

    var explosion: Sound
    var ps = ps

    init {
        explosion = Gdx.audio.newSound(Assets.SND_EXPLOSION)
    }

    override fun addedToEngine(engine: Engine?) {
        players = engine!!.getEntitiesFor(Family.all(PlayerComponent::class.java).get())
        bullets = engine!!.getEntitiesFor(Family.all(BulletComponent::class.java).get())
        asteroids = engine!!.getEntitiesFor(Family.all(AsteroidComponent::class.java, CollisionComponent::class.java).get())
        astronauts = engine!!.getEntitiesFor(Family.all(AstronautComponent::class.java, AliveComponent::class.java).get())
        items = engine!!.getEntitiesFor(Family.all(ItemComponent::class.java, CollisionComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        //if (players.size() < 1) return
        if (players.size() > 0 && asteroids.size() > 0) stationAndAsteroids()
        if (bullets.size() > 0 && asteroids.size() > 0) bulletsAndAsteroids()
        if (astronauts.size() > 0 && asteroids.size() > 0) astronautAndAsteroids()
        if (astronauts.size() > 0 && items.size() > 0) astronautAndItems()
    }

    var shaking = false

    private fun stationAndAsteroids() {
        var station = players.get(0)
        var stationPC = station.getComponent(PositionComponent::class.java)
        // station collision rect needs to be smaller
        var stationRect = Rectangle(stationPC.x + 380, stationPC.y + 50, 500f, 95f)
        for (asteroid in asteroids) {
            var asteroidPC = asteroid.getComponent(PositionComponent::class.java)
            var r = Rectangle()
            Intersector.intersectRectangles(stationRect, asteroidPC.rect(), r)
            if (r.width > 0) {
                // shake it!
                //var tweenPos = Tween.to(pc, PositionAccessor.POSITION, 2f).target(pc.x, Assets.VIEWPORT_HEIGHT - 200f).ease(Bounce.OUT)
                //TODO: move this to a World class?
                if (!shaking) {
                    shaking = true
                    shakeStation(stationPC)
                }

                // splode
                var particle = Particle(asteroidPC.x + asteroidPC.width/2,
                        asteroidPC.y + asteroidPC.height/2, Assets.PART_ALL)
                engine.addEntity(particle)
                explosion.play()
                engine.removeEntity(asteroid)

                var hc = station.getComponent(HealthComponent::class.java)
                hc.health -= World.ASTEROID_DAMAGE

                if (hc.health <= 0) {
                    // game over
                    hc.func()
                }
            }
        }
    }

    fun astronautAndAsteroids() {
        // all astronauts collide
        for (astronaut in astronauts) {
            var astronautPC = astronaut.getComponent(PositionComponent::class.java)
            var r: Rectangle
            for (asteroid in asteroids) {
                var asteroidPC = asteroid.getComponent(PositionComponent::class.java)
                r = Rectangle()
                Intersector.intersectRectangles(astronautPC.rect(), asteroidPC.rect(), r)
                if (r.width > 0) {
                    var asteroidMC = asteroid.getComponent(MovementComponent::class.java)
                    var astronautMC = astronaut.getComponent(MovementComponent::class.java)
                    if (astronautMC == null) {
                        astronautMC = MovementComponent(0f, 0f)
                        astronaut.add(asteroidMC)
                    }
                    astronautMC.velocityX = asteroidMC.velocityX
                    engine.removeEntity(asteroid)
                }
            }
        }
    }

    fun astronautAndItems() {
        // only connected astronauts can collect
        var connected = astronauts.filter { astronaut -> astronaut.getComponent(ConnectedComponent::class.java) != null }
        //var astronaut = astronauts.get(0)
        for (astronaut in connected) {
            var astronautPC = astronaut.getComponent(PositionComponent::class.java)
            var r: Rectangle// = Rectangle()

            for (item in items) {
                var itemPC = item.getComponent(PositionComponent::class.java)
                r = Rectangle()
                Intersector.intersectRectangles(itemPC.rect(), astronautPC.rect(), r)
                if (r.width > 0) {
                    when (item.getComponent(ItemComponent::class.java).type) {
                        ItemType.STATION_HEALTH -> {
                            //??
                            // This should call back somewhere?
                            engine.removeEntity(item)
                            World.station.stationHealthUp()
                        }
                        ItemType.FIRE_UP -> {
                            engine.removeEntity(item)
                            World.firing_speed -= 0.5f
                        }
                    }
                }
            }
        }
    }

    fun shakeStation(pc: PositionComponent) {
        var startX = pc.x
        var startY = pc.y
        var shakeX = Tween.to(pc, PositionAccessor.POSITION, 0.05f).target(startX - 5, startY + 5)
        var shakeX2 = Tween.to(pc, PositionAccessor.POSITION, 0.05f).target(startX + 5, startY - 5)
        var shakeX3 = Tween.to(pc, PositionAccessor.POSITION, 0.05f).target(startX, startY)

        var t = Timeline.createSequence()
        t.push(shakeX).push(shakeX2).push(shakeX3)
        t.repeat(1, 0.1f)

        t.setCallback(object: TweenCallback {
            override fun onEvent(type: Int, src: BaseTween<*>?) {
                when (type) {
                    TweenCallback.COMPLETE -> shaking = false
                }
            }
        })

        engine.getSystem(TweenSystem::class.java).addTimeline(t)
    }

    private fun bulletsAndAsteroids() {
        //var playerPC = player.getComponent(PositionComponent::class.java)
        for (asteroid in asteroids) {
            var asteroidPC = asteroid.getComponent(PositionComponent::class.java)
            var asteroidMC = asteroid.getComponent(MovementComponent::class.java)
            var asteroidAC = asteroid.getComponent(AsteroidComponent::class.java)
            for (bullet in bullets) {
                var bulletPC = bullet.getComponent(PositionComponent::class.java)
                var r = Rectangle()
                Intersector.intersectRectangles(bulletPC.rect(), asteroidPC.rect(), r)

                if (r.width > 0) {
                    World.score += asteroidAC.points
                    var particle = Particle(asteroidPC.x + asteroidPC.width/2,
                            asteroidPC.y + asteroidPC.height/2, Assets.PART_ASTEROID)
                    engine.addEntity(particle)
                    engine.removeEntity(asteroid)
                    engine.removeEntity(bullet)
                    if (asteroidAC.stage == 1) asteroidShot(asteroidPC, asteroidMC)
                }
            }
        }
    }

    // Is this the right place for this method?
    private fun asteroidShot(asteroidPC:PositionComponent, asteroidMC:MovementComponent) {

        val texNames = arrayOf(Assets.ASTEROID_SEGMENT_1, Assets.ASTEROID_SEGMENT_2, Assets.ASTEROID_SEGMENT_3)

        for (texName in texNames) {
            var segment = Entity()
            var texture = Texture(texName)
            var pc = PositionComponent(asteroidPC.x, asteroidPC.y, texture.width.toFloat(), texture.height.toFloat())
            pc.apply { scaleX = asteroidPC.scaleX; scaleY = asteroidPC.scaleY }
            segment.add(pc)
            segment.add(VisualComponent(texture))
            segment.add(MovementComponent(asteroidMC.velocityX + MathUtils.random(-50, 50), asteroidMC.velocityY + MathUtils.random(-50, 50)))
            segment.add(AsteroidComponent(20, 2))
            segment.add(CollisionComponent())
            engine.addEntity(segment)
        }

    }



}