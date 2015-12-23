package org.sturgeon.sweeper

import aurelienribon.tweenengine.Tween
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Texture
import org.sturgeon.sweeper.components.*
import org.sturgeon.sweeper.systems.*

class PlayScreen(var game: SpaceSweeper) : ScreenAdapter() {

    var alwaysSystems = arrayOf(RenderingSystem(), TweenSystem(), MovementSystem(), BoundsCheckSystem())
    var attractSystems:Array<EntitySystem> = arrayOf(StarfieldSystem(0.1f))
    var playSystems = arrayOf(FiringSystem()
                    ,AddAsteroidSystem(1f)
                    ,CollisionSystem()
                    ,ScoreSystem(), HealthSystem())
    //var gameOverSystems = arrayOf()

    private var turret:Entity

    val ATTRACT = 1
    val PLAYING = 2
    val GAME_OVER = 3

    var mode = ATTRACT

    companion object {
        var firing = false
    }

    init {
        turret = Entity()
        //setPlaying()
        setAttract()
    }

    //private val TURRET_ID = 100

    override fun render(delta: Float) {
        keyListener()
        game.engine.update(delta)
    }

    private fun setAttract() {

        mode = ATTRACT
        addSystems(attractSystems)

        var logo = Entity()
        var t = Texture(Assets.LOGO)
        var pc = PositionComponent(1100f, 100f, t.width.toFloat(), t.height.toFloat())

        logo.add(pc)
        logo.add(VisualComponent(t))
        game.engine.addEntity(logo)

        var tween = Tween.to(pc, 1, 2f).target(Assets.VIEWPORT_WIDTH/2 - t.width/2, Assets.VIEWPORT_HEIGHT - 200f)
        game.engine.getSystem(TweenSystem::class.java).addTween(tween)


        var startText = Entity()
        startText.add(PositionComponent(Assets.VIEWPORT_WIDTH/2, 100f))
        startText.add(TextComponent("Press any key to start", true))
        game.engine.addEntity(startText)

    }

    private fun setPlaying() {

        mode = PLAYING

        addSystems(playSystems)

        // add station
        var station = Entity()
        var t = Texture(Assets.STATION)
        station.add(PositionComponent(Assets.VIEWPORT_WIDTH/2 - t.width/2, Assets.VIEWPORT_HEIGHT/2 - t.height/2, t.width.toFloat(), t.height.toFloat()))
        station.add(VisualComponent(t))
        station.add(PlayerComponent())
        game.engine.addEntity(station)

        // add turret
        var t2 = Texture(Assets.TURRET)
        var pc = PositionComponent(Assets.VIEWPORT_WIDTH/2 - t2.width/2, Assets.VIEWPORT_HEIGHT/2 - t2.height/2, t2.width.toFloat(), t2.height.toFloat())
        pc.originX = 38f
        pc.originY = 38f
        turret.add(pc)
        turret.add(VisualComponent(t2))
        turret.add(FiringComponent())
        game.engine.addEntity(turret)

        // add score
        var scoreText = Entity()
        scoreText.add(PositionComponent(10f, Assets.VIEWPORT_HEIGHT - 40))
        scoreText.add(TextComponent("score : " + World.score))
        scoreText.add(ScoreComponent())
        game.engine.addEntity(scoreText)

        // add health
        var healthText = Entity()
        healthText.add(PositionComponent(10f, Assets.VIEWPORT_HEIGHT - 80))

        fun updateHealth() = { "health: " + station.getComponent(HealthComponent::class.java).health  }
        station.add(HealthComponent(100, { setGameOver() }))
        healthText.add(UpdatingTextComponent("health : " + 100, false, updateHealth() ))
        game.engine.addEntity(healthText)
    }

     fun setGameOver() {
        println("game over")
        mode = GAME_OVER

        game.engine.removeSystem(game.engine.getSystem(MovementSystem::class.java))
        game.engine.removeSystem(game.engine.getSystem(CollisionSystem::class.java))

        var gameOverText = Entity()
        gameOverText.add(PositionComponent(Assets.VIEWPORT_WIDTH/2, Assets.VIEWPORT_HEIGHT/2))
        gameOverText.add(TextComponent("Game Over !", true))
        game.engine.addEntity(gameOverText)

        var startText = Entity()
        startText.add(PositionComponent(Assets.VIEWPORT_WIDTH/2, 100f))
        startText.add(TextComponent("Press any key to start", true))
        game.engine.addEntity(startText)

    }

    private fun addSystems(systems:Array<EntitySystem>) {

        for (system in game.engine.systems) {
            game.engine.removeSystem(system)
        }
        game.engine.removeAllEntities()

        for (system in alwaysSystems) {
            game.engine.addSystem(system)
        }

        for (system in systems) {
            game.engine.addSystem(system)
        }

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

        if (mode == ATTRACT) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                setPlaying()
            }
        } else if (mode == GAME_OVER) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                setAttract()
            }
        }


        /*
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            shoot()
        }
        */
    }



}