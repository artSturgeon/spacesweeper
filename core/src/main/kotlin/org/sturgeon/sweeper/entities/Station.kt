package org.sturgeon.sweeper.entities

import aurelienribon.tweenengine.BaseTween
import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenCallback
import aurelienribon.tweenengine.equations.Sine
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.sturgeon.sweeper.Accessors.PositionAccessor
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.World
import org.sturgeon.sweeper.components.*
import org.sturgeon.sweeper.systems.IncidentalSystem
import org.sturgeon.sweeper.systems.TweenSystem
import java.util.*

class Station(e: Engine) {
    var station = Entity()
    var turret = Entity()
    var panels = ArrayList<Entity>()
    var recallBtn = Entity()
    var engine = e

    private var entitiesToRemove = ArrayList<Entity>()

    private var astronaut = Entity()
    private var lifeline = Entity()
    private var astronautMoving = false

    init {
        //addStation()
        // ooo keep a reference to the space station!
        World.station = this
    }

    fun addStation() {
        addStationBody()
        addTurret()
        addPanels()
        addRecallButton { recallAstronaut() }
    }

    fun addStationBody() {
        var t = Texture(Assets.STATION)
        //station.add(PositionComponent(Assets.VIEWPORT_WIDTH + 100, Assets.VIEWPORT_HEIGHT / 2 - t.height / 2, t.width.toFloat(), t.height.toFloat()))
        station.add(PositionComponent(Assets.VIEWPORT_WIDTH/2 - 850, Assets.VIEWPORT_HEIGHT / 2 - t.height / 2, t.width.toFloat(), t.height.toFloat()).apply { scaleX = 0f; scaleY = 0f })
        //var stationMoveTween = Tween.to(stationPC, PositionAccessor.POSITION, 2f).target(Assets.VIEWPORT_WIDTH/2 - 850, stationPC.y).ease(Sine.OUT)
        //station.add(PositionComponent(Assets.VIEWPORT_WIDTH/2 - t.width/2, Assets.VIEWPORT_HEIGHT/2 - t.height/2, t.width.toFloat(), t.height.toFloat()))
        station.add(VisualComponent(t))
        station.add(PlayerComponent())
        engine.addEntity(station)
        entitiesToRemove.add(station)
    }

    fun addPanels() {
        addPanel(station.getComponent(PositionComponent::class.java).x + 492, station.getComponent(PositionComponent::class.java).y + 193f)
        addPanel(station.getComponent(PositionComponent::class.java).x + 640, station.getComponent(PositionComponent::class.java).y + 193f)
        addPanel(station.getComponent(PositionComponent::class.java).x + 580, station.getComponent(PositionComponent::class.java).y - 172f)
        engine.addSystem(IncidentalSystem())
    }

    fun addPanel(x:Float, y:Float) {
        var panel = Entity()
        var t = Texture(Assets.SOLAR_PANEL)
        panel.add(PositionComponent(x, y,
                t.width.toFloat(), t.height.toFloat()).apply { scaleX = 0f; scaleY = 0f })
        panel.add(VisualComponent(t, 20))
        panel.add(PanelComponent())
        engine.addEntity(panel)
        entitiesToRemove.add(panel)
        panels.add(panel)
    }

    private fun addTurret() {
        // Texture with all frames
        var firing = Texture(Assets.TURRET_ANIMATION_SINGLE)
        firing.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        // 2d array with frames split by width/height
        val frames = 30
        var tmp = TextureRegion.split(firing, firing.width, firing.height/frames)
        // 1d array with consecutive frames
        var firingFrames = Array<TextureRegion>(frames, { i -> tmp[i][0] })
        // animation, constructor takes varargs, so using Kotlin spread operator *
        var firingAnimation = Animation(0.05f, *firingFrames)

        var width = firingFrames[0].regionWidth
        var height = firingFrames[0].regionHeight

        //var x = station.getComponent(PositionComponent::class.java).x + 825
        var x = Assets.VIEWPORT_WIDTH/2 - 850 + 825
        var y = station.getComponent(PositionComponent::class.java).y + 72

        var pc = PositionComponent(x, y, width.toFloat(), height.toFloat())
        pc.scaleX = 0f
        pc.scaleY = 0f

        pc.originX = 25f;
        pc.originY = height/2f;

        turret.add(pc)
        turret.add(AnimationComponent(firingAnimation))
        turret.add(FiringComponent())
        engine.addEntity(turret)
        entitiesToRemove.add(turret)
    }

    fun addRecallButton(callback: () -> Unit) {

        var t = Texture(Assets.RECALL_BUTTON)
        var pc = PositionComponent(station.getComponent(PositionComponent::class.java).x + 700,
                station.getComponent(PositionComponent::class.java).y + 72,
                t.width.toFloat(), t.height.toFloat())
        pc.scaleX = 0f
        pc.scaleY = 0f
        recallBtn.add(pc)
        recallBtn.add(VisualComponent(t, 1000))
        recallBtn.add(ClickComponent({ callback() }))
        engine.addEntity(recallBtn)
        entitiesToRemove.add(recallBtn)
    }

    fun getHealth():Int {
        return station.getComponent(HealthComponent::class.java).health
    }

    fun stationHealthUp() {
        var hc = station.getComponent(HealthComponent::class.java)
        hc.health += 10
        if (hc.health > World.STATION_HEALTH) hc.health = World.STATION_HEALTH
    }

    // bzzzz?
    fun add(c: Component) {
        station.add(c)
    }

    fun tweenIn(callback: () -> Unit) {
        var tweenSystem = engine.getSystem(TweenSystem::class.java)
        var stationPC = station.getComponent(PositionComponent::class.java)
        var stationMoveTween = Tween.from(stationPC, PositionAccessor.POSITION, 2f).target(stationPC.x + 2000, stationPC.y).ease(Sine.OUT)
        var stationScaleTween = Tween.to(stationPC, PositionAccessor.SCALE, 1f).target(1.0f, 1.0f)
        tweenSystem.addTween(stationMoveTween)
        tweenSystem.addTween(stationScaleTween)

        var turretPC = turret.getComponent(PositionComponent::class.java)
        var turretMoveTween = Tween.from(turretPC, PositionAccessor.POSITION, 2f).target(turretPC.x + 2000, turretPC.y).ease(Sine.OUT)
        var turretScaleTween = Tween.to(turretPC, PositionAccessor.SCALE, 1f).target(1f, 1f)
        tweenSystem.addTween(turretMoveTween)
        tweenSystem.addTween(turretScaleTween)

        var recallPC = recallBtn.getComponent(PositionComponent::class.java)
        var recallMoveTween = Tween.from(recallPC, PositionAccessor.POSITION, 2f).target(recallPC.x + 2000, recallPC.y).ease(Sine.OUT)
        var recallScaleTween = Tween.to(recallPC, PositionAccessor.SCALE, 1f).target(1f, 1f)
        tweenSystem.addTween(recallMoveTween)
        tweenSystem.addTween(recallScaleTween)

        for (panel in panels) {
            var panelPC = panel.getComponent(PositionComponent::class.java)
            var panelMoveTween = Tween.from(panelPC, PositionAccessor.POSITION, 2f).target(panelPC.x + 2000, panelPC.y).ease(Sine.OUT)
            var panelScaleTween = Tween.to(panelPC, PositionAccessor.SCALE, 1f).target(1.0f, 1.0f)
            tweenSystem.addTween(panelMoveTween)
            tweenSystem.addTween(panelScaleTween)
        }

        // Set systems up
        stationMoveTween.setCallback(object: TweenCallback {
            override fun onEvent(type: Int, src: BaseTween<*>?) {
                when (type) {
                    aurelienribon.tweenengine.TweenCallback.COMPLETE -> callback()
                }
            }
        })
    }

    fun dispose() {
        // clean up station
        println("cleaning up")
        for (entity in entitiesToRemove) {
            engine.removeEntity(entity)
        }
    }

    fun turretLeft(amount:Float) {
        var pc = turret.getComponent(PositionComponent::class.java)
        pc.angle += amount
    }

    fun turrentRight(amount:Float) {
        var pc = turret.getComponent(PositionComponent::class.java)
        pc.angle -= amount
    }

    fun releaseOrMoveAstronaut(x: Float, y: Float) {
        println("release or move astronaut")
        if (World.astronauts > 0) {
            if (astronaut.components.size() <= 0) {
                // uninitialised
                println("all new astronaut")
                addAstronaut()
                addLifeLine()
            } else if (astronaut.getComponent(ConnectedComponent::class.java) == null) {
                // alive but adrift
                println("there was already some astronaut")
                addAstronaut()
                addLifeLine()
            }
            moveAstronaut(x, y)
        }
    }

    fun addAstronaut() {
        println("adding a new astronaut")
        astronaut = Entity()
        var t = Texture(Assets.ASTRONAUT)
        //astronaut.add(pc)
        astronaut.add(VisualComponent(t, 1))
        var recallPC = recallBtn.getComponent(PositionComponent::class.java)
        var pc = PositionComponent(recallPC.x + recallPC.width / 2, recallPC.y + recallPC.height / 2,
                t.width.toFloat(), t.height.toFloat())
        astronaut.add(pc)
        astronaut.add(AstronautComponent())
        astronaut.add(AliveComponent())
        astronaut.add(ConnectedComponent())
        engine.addEntity(astronaut)
        entitiesToRemove.add(astronaut)
    }

     fun moveAstronaut(x: Float, y: Float) {
         println("moving astronaut")
         var astronautPC = astronaut.getComponent(PositionComponent::class.java)
         var origin = Vector2(astronautPC.x, astronautPC.y)
         // alter the target or we won't have the correct velocity vector later
         var target = Vector2(x - astronautPC.width/2 , y - astronautPC.height/2)
         // Add a target
        var moved = { astronautMoving = false }
        astronaut.add(TargetComponent(target, moved))
        // and a velocity
        var velo = target.cpy().sub(origin).nor().scl(100f, 100f)
        astronaut.add(MovementComponent(velo.x, velo.y))
    }

    fun recallAstronaut() {
        if (astronaut.components.size() > 0 && lifeline.components.size() > 0) {
            println("I will recall the astronaut")
            var pc = astronaut.getComponent(PositionComponent::class.java)
            var mc = astronaut.getComponent(MovementComponent::class.java)
            if (mc == null) {
                mc = MovementComponent(0f, 0f)
                astronaut.add(mc)
            }
            var lineStart = lifeline.getComponent(LineComponent::class.java).lineStart
            var lineEnd = lifeline.getComponent(LineComponent::class.java).lineEnd
            // work out vectors to target
            var l = lineStart.cpy().sub(lineEnd).nor().scl(100f, 100f)
            mc.velocityX = l.x
            mc.velocityY = l.y
            var rotate = Tween.to(recallBtn.getComponent(PositionComponent::class.java), PositionAccessor.ANGLE, 2f).target(360f)
            engine.getSystem(TweenSystem::class.java).addTween(rotate)
            // target is offset for astronaut origin
            // it isn't neat but at least it's consistent!
            astronaut.add(TargetComponent(Vector2(lineStart.x - pc.width/2, lineStart.y - pc.height/2), { World.station.dockAstronaut() }))
        }
    }

    fun dockAstronaut() {
        println("dock astronaut")
        astronaut.removeAll()
        engine.removeEntity(astronaut)
        engine.removeEntity(lifeline)
        entitiesToRemove.remove(astronaut)
    }

    fun addLifeLine() {
        println("adding lifeline")
        lifeline = Entity()
        var recallBtnPC = recallBtn.getComponent(PositionComponent::class.java)
        var astronautPC = astronaut.getComponent(PositionComponent::class.java)
        var lineStart = Vector2(recallBtnPC.x + recallBtnPC.width/2, recallBtnPC.y + recallBtnPC.height / 2)
        var lineEnd = Vector2(astronautPC.x + astronautPC.width, astronautPC.y + astronautPC.height)
        lifeline.add(LineComponent(lineStart, lineEnd))
        engine.addEntity(lifeline)
        entitiesToRemove.add(lifeline)
    }

    /*
    fun clickAstronaut(x: Float, y: Float) {
        println("clickAstronaut")
        astronautMoving = true

        var pc: PositionComponent

        if (astronauts.size <= 0) return

        var astronaut: Entity

        if (astronauts.size() == 0) {
            var players = engine.getEntitiesFor(Family.all(PlayerComponent::class.java).get())
            var station = players.get(0)
            var stationPC = station.getComponent(PositionComponent::class.java)

            // create a new astronaut and send him

            //engine.addEntity(astronaut)

            // add the lifeline
            /*
            var lifeline = Entity()
            var lineStart = Vector2(stationPC.x + stationPC.width, stationPC.y + stationPC.height / 2)
            var lineEnd = Vector2(pc.x, pc.y)
            lifeline.add(LineComponent(lineStart, lineEnd))
            engine.addEntity(lifeline)
            */
            engine.addSystem(LifelineSystem())
            //var move = Tween.to(pc, PositionAccessor.POSITION, 2f).target(x, y)
        } else {
            astronaut = astronauts.get(0)
            pc = astronaut.getComponent(PositionComponent::class.java)
        }

        // add some velocity to for astronaut to target
        // pc.x, pc.y = where astronaut is now
        // x, y = target
        //var origin = Vector2(pc.x, pc.y)
        //var target = Vector2(x, y)

        var moved = { astronautMoving = false }
        //astronaut.add(TargetComponent(target, moved))

        //var velo = target.cpy().sub(origin).nor().scl(100f, 100f)
        // Now we start to wonder why we didn't use vectors for everything in the first place!

        //astronaut.add(MovementComponent(velo.x, velo.y))

        /*
        var move = Tween.to(pc, PositionAccessor.POSITION, v3 / 200f).target(x, y)
        move.setCallback(object: TweenCallback {
            override fun onEvent(type: Int, src: BaseTween<*>?) {
                when (type) {
                    TweenCallback.COMPLETE -> {
                        moving = false
                        //TODO: remove this!
                        //ps.testWire()
                    }
                }
            }
        })
        engine.getSystem(TweenSystem::class.java).addTween(move)
        */
    }
*/
}