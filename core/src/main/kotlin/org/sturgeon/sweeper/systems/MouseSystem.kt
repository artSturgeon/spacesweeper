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
import org.sturgeon.sweeper.Accessors.PositionAccessor
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.components.AstronautComponent
import org.sturgeon.sweeper.components.PlayerComponent
import org.sturgeon.sweeper.components.PositionComponent
import org.sturgeon.sweeper.components.VisualComponent

class MouseSystem : EntitySystem() {

    lateinit private var astronauts: ImmutableArray<Entity>
    private var moving = false

    init {
        Gdx.input.inputProcessor = GestureDetector(object : GestureDetector.GestureAdapter() {
            override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
                if (!moving)
                    mouseClicked(x, y, button)
                return super.tap(x, y, count, button)
            }
        })
    }

    override fun addedToEngine(engine: Engine?) {
        astronauts = engine!!.getEntitiesFor(Family.all(AstronautComponent::class.java).get())
    }

    fun mouseClicked(x: Float, y: Float, button: Int) {
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
            engine.addEntity(astronaut)

            var move = Tween.to(pc, PositionAccessor.POSITION, 2f).target(x, y)
        } else {
            pc = astronauts.get(0).getComponent(PositionComponent::class.java)
        }

        var move = Tween.to(pc, PositionAccessor.POSITION, 2f).target(x, y)
        move.setCallback(object: TweenCallback {
            override fun onEvent(type: Int, src: BaseTween<*>?) {
                when (type) {
                    TweenCallback.COMPLETE -> moving = false
                }
            }
        })
        engine.getSystem(TweenSystem::class.java).addTween(move)
    }



}