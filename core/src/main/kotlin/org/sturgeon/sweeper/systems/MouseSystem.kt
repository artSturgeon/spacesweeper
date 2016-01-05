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
import com.badlogic.gdx.math.Vector2
import org.sturgeon.sweeper.Accessors.PositionAccessor
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.PlayScreen
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
        astronauts = engine!!.getEntitiesFor(Family.all(AstronautComponent::class.java).get())
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

        if (astronauts.size() == 0) {
            var players = engine.getEntitiesFor(Family.all(PlayerComponent::class.java).get())
            var station = players.get(0)
            var stationPC = station.getComponent(PositionComponent::class.java)

            // create a new astronaut and send him
            var astronaut = Entity()
            var t = Texture(Assets.ASTRONAUT)
            pc = PositionComponent(stationPC.x + stationPC.width - 100, stationPC.y + stationPC.height/2,
                    t.width.toFloat(), t.height.toFloat())
            astronaut.add(pc)
            astronaut.add(VisualComponent(t, 1))
            astronaut.add(AstronautComponent())
            engine.addEntity(astronaut)

            //var move = Tween.to(pc, PositionAccessor.POSITION, 2f).target(x, y)
        } else {
            pc = astronauts.get(0).getComponent(PositionComponent::class.java)
        }

        // distance
        var v = Vector2(pc.x, pc.y)
        var v2 = Vector2(x, y)
        var v3 = v.dst(v2)

        var move = Tween.to(pc, PositionAccessor.POSITION, v3 / 200f).target(x, y)
        move.setCallback(object: TweenCallback {
            override fun onEvent(type: Int, src: BaseTween<*>?) {
                when (type) {
                    TweenCallback.COMPLETE -> {
                        moving = false
                        //TODO: remove this!
                        ps.testWire()
                    }
                }
            }
        })
        engine.getSystem(TweenSystem::class.java).addTween(move)
    }

}