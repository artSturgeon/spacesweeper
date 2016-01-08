package org.sturgeon.sweeper.systems

import aurelienribon.tweenengine.BaseTween
import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenCallback
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.sturgeon.sweeper.Accessors.PositionAccessor
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.PlayScreen
import org.sturgeon.sweeper.World
import org.sturgeon.sweeper.components.*

class MouseSystem(ps: PlayScreen) : EntitySystem() {
    //TODO: remove this!
    // Plus probably refactor this...
    var ps = ps
    lateinit private var astronauts: ImmutableArray<Entity>
    lateinit private var clickers: ImmutableArray<Entity>
    private var moving = false

    init {
        Gdx.input.inputProcessor = GestureDetector(object : GestureDetector.GestureAdapter() {
            override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
                if (!moving) {
                    // slightly iffy...
                    var clickPoint = engine.getSystem(RenderingSystem::class.java).unproject(x, y)
                    mouseClicked(clickPoint.x, clickPoint.y, button)
                }
                return super.tap(x, y, count, button)
            }
        })
    }

    override fun addedToEngine(engine: Engine?) {
        astronauts = engine!!.getEntitiesFor(Family.all(AstronautComponent::class.java, AliveComponent::class.java).get())
        clickers = engine!!.getEntitiesFor(Family.all(ClickComponent::class.java).get())
    }

    fun mouseClicked(x: Float, y: Float, button: Int) {
        if (!clickClickers(x, y)) {
            clickAstronaut(x, y)
        }
    }

    fun clickClickers(x: Float, y:Float):Boolean {
        for (clicker in clickers) {
            var pc = clicker.getComponent(PositionComponent::class.java)
            if (pc.rect().contains(x, y)) {
                println("clicker!")
                var cc = clicker.getComponent(ClickComponent::class.java)
                cc.func()
                return true
            }
        }
        return false
    }

    fun clickAstronaut(x: Float, y: Float) {

        moving = true
        var pc: PositionComponent

        if (World.astronauts <= 0) return

        var astronaut: Entity

        if (astronauts.size() == 0) {
            var players = engine.getEntitiesFor(Family.all(PlayerComponent::class.java).get())
            var station = players.get(0)
            var stationPC = station.getComponent(PositionComponent::class.java)

            // create a new astronaut and send him
            astronaut = Entity()
            var t = Texture(Assets.ASTRONAUT)
            pc = PositionComponent(stationPC.x + stationPC.width - 100, stationPC.y + stationPC.height/2,
                    t.width.toFloat(), t.height.toFloat())
            astronaut.add(pc)
            astronaut.add(VisualComponent(t, 1))
            astronaut.add(AstronautComponent())
            astronaut.add(AliveComponent())
            engine.addEntity(astronaut)

            // add the lifeline
            var lifeline = Entity()
            var lineStart = Vector2(stationPC.x + stationPC.width, stationPC.y + stationPC.height / 2)
            var lineEnd = Vector2(pc.x, pc.y)
            lifeline.add(LineComponent(lineStart, lineEnd))
            engine.addEntity(lifeline)

            engine.addSystem(LifelineSystem())
            //var move = Tween.to(pc, PositionAccessor.POSITION, 2f).target(x, y)
        } else {
            astronaut = astronauts.get(0)
            pc = astronaut.getComponent(PositionComponent::class.java)
        }

        // add some velocity to for astronaut to target
        // pc.x, pc.y = where astronaut is now
        // x, y = target
        var origin = Vector2(pc.x, pc.y)
        var target = Vector2(x, y)

        var moved = { moving = false }
        astronaut.add(TargetComponent(target, moved))

        var velo = target.cpy().sub(origin).nor().scl(100f, 100f)
        // Now we start to wonder why we didn't use vectors for everything in the first place!
        astronaut.add(MovementComponent(velo.x, velo.y))

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

}