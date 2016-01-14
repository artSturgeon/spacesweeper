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
import com.badlogic.gdx.audio.Sound
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
    var gesture: GestureDetector

    init {
        gesture = GestureDetector(object : GestureDetector.GestureAdapter() {
            override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
                    var clickPoint = engine.getSystem(RenderingSystem::class.java).unproject(x, y)
                    mouseClicked(clickPoint.x, clickPoint.y, button)
                return super.tap(x, y, count, button)
            }
        })
    }

    override fun removedFromEngine(engine: Engine?) {
        Gdx.input.inputProcessor = null
        super.removedFromEngine(engine)
    }

    override fun addedToEngine(engine: Engine?) {
        Gdx.input.inputProcessor = gesture
        astronauts = engine!!.getEntitiesFor(Family.all(AstronautComponent::class.java, AliveComponent::class.java).get())
        clickers = engine!!.getEntitiesFor(Family.all(ClickComponent::class.java).get())
    }

    fun mouseClicked(x: Float, y: Float, button: Int) {
        if (!clickClickers(x, y)) {
            clickInSpace(x, y)
        }
    }

    fun clickClickers(x: Float, y:Float):Boolean {
        for (clicker in clickers) {
            var pc = clicker.getComponent(PositionComponent::class.java)
            if (pc.rect().contains(x, y)) {
                var cc = clicker.getComponent(ClickComponent::class.java)
                cc.func()
                return true
            }
        }
        return false
    }

    fun clickInSpace(x: Float, y: Float) {
        World.station.releaseOrMoveAstronaut(x, y)
    }



}