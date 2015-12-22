package org.sturgeon.sweeper

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import org.sturgeon.sweeper.components.*
import org.sturgeon.sweeper.systems.*


class PlayScreen(var game: SpaceSweeper) : ScreenAdapter() {
    companion object {
        var firing = false
    }
    private var turret:Entity

    init {
        turret = Entity()
        createWorld()
    }

    //private val TURRET_ID = 100


    override fun render(delta: Float) {
        keyListener()
        game.engine.update(delta)
    }

    private fun createWorld() {

        addSystems()
        var station = Entity()
        var t = Texture(Assets.STATION)

        station.add(PositionComponent(Assets.VIEWPORT_WIDTH/2 - t.width/2, Assets.VIEWPORT_HEIGHT/2 - t.height/2, t.width.toFloat(), t.height.toFloat()))
        station.add(VisualComponent(t))
        game.engine.addEntity(station)


        var t2 = Texture(Assets.TURRET)

        var pc = PositionComponent(Assets.VIEWPORT_WIDTH/2 - t2.width/2, Assets.VIEWPORT_HEIGHT/2 - t2.height/2, t2.width.toFloat(), t2.height.toFloat())
        pc.originX = 38f
        pc.originY = 38f
        turret.add(pc)
        turret.add(VisualComponent(t2))
        turret.add(FiringComponent())
        game.engine.addEntity(turret)

    }

    private fun addSystems() {
        game.engine.addSystem(RenderingSystem())
        game.engine.addSystem(MovementSystem())
        game.engine.addSystem(FiringSystem())
        game.engine.addSystem(AddAsteroidSystem(1f))
        game.engine.addSystem(CollisionSystem())
    }

    private fun keyListener() {
        if (!firing) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                var pc = turret.getComponent(PositionComponent::class.java)
                pc.angle += 2f
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                var pc = turret.getComponent(PositionComponent::class.java)
                pc.angle -= 2f
            }
        }
        /*
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            shoot()
        }
        */
    }



}