package org.sturgeon.sweeper

import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.equations.Bounce
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import org.sturgeon.sweeper.Accessors.PositionAccessor
import org.sturgeon.sweeper.components.*
import org.sturgeon.sweeper.entities.Star
import org.sturgeon.sweeper.entities.Station
import org.sturgeon.sweeper.systems.*
import java.util.*

class PlayScreen(var game: SpaceSweeper) : ScreenAdapter() {

    val alwaysSystems = arrayOf(RenderingSystem(), TweenSystem(), MovementSystem(), BoundsCheckSystem())
    val attractSystems:Array<EntitySystem> = arrayOf(StarfieldSystem(0.1f), BigTextSystem(), AddBigTextSystem(30f))

    // lazy init for these so they don't take effect until needed
    val playSystems:Array<EntitySystem> by lazy {
        arrayOf(FiringSystem()
                , AddAsteroidSystem(5f)
                , AddObjectSystem(5f)
                , CollisionSystem(this)
                , LifelineSystem()
                , ScoreSystem()
                , HealthSystem()
                , LevelSystem()
                , MouseSystem(this))
    }

    //private var station = Entity()
    //private var turret = Entity()
    private var station: Station
    private var logo = Entity()
    private var theWorld = Entity()

    private var entitiesToRemove = ArrayList<Entity>()

    val ATTRACT = 1
    val PLAYING = 2
    val GAME_OVER = 3
    val TRANSITION = 4

    var mode = ATTRACT

    companion object {
        var firing = false
    }

    init {
        addSystems(alwaysSystems)
        setAttract()
        addTheWorld()
        //setPlaying()
        addInitialStars()
        station = Station(game.engine)
    }

    override fun render(delta: Float) {
        keyListener()
        game.engine.update(delta)
    }

    private fun setAttract() {
        mode = ATTRACT
        addSystems(attractSystems)

        var t = Texture(Assets.LOGO)
        var pc = PositionComponent(Assets.VIEWPORT_WIDTH/2 - t.width/2, Assets.VIEWPORT_HEIGHT+ 200, t.width.toFloat(), t.height.toFloat())
        pc.scaleX = 0.1f
        pc.scaleY = 0.1f
        logo.add(pc)
        logo.add(VisualComponent(t))
        game.engine.addEntity(logo)
        entitiesToRemove.add(logo)

        var tweenPos = Tween.to(pc, PositionAccessor.POSITION, 2f).target(pc.x, Assets.VIEWPORT_HEIGHT - 200f).ease(Bounce.OUT)
        var tweenScale = Tween.to(pc, PositionAccessor.SCALE, 2f).target(1f, 1f)

        game.engine.getSystem(TweenSystem::class.java).addTween(tweenPos)
        game.engine.getSystem(TweenSystem::class.java).addTween(tweenScale)

        var lastScoreText = Entity()
        lastScoreText.add(PositionComponent(Assets.VIEWPORT_WIDTH/2, 300f))
        lastScoreText.add(TextComponent("Last score : " + World.lastScore, true))
        game.engine.addEntity(lastScoreText)
        entitiesToRemove.add(lastScoreText)

        var highScoreText = Entity()
        highScoreText.add(PositionComponent(Assets.VIEWPORT_WIDTH/2, 350f))
        highScoreText.add(TextComponent("High score : " + World.highScore, true))
        game.engine.addEntity(highScoreText)
        entitiesToRemove.add(highScoreText)

        var startText = Entity()
        startText.add(PositionComponent(Assets.VIEWPORT_WIDTH/2, 100f))
        startText.add(TextComponent("Press space key to start", true))
        game.engine.addEntity(startText)
        entitiesToRemove.add(startText)
    }

    private fun setPlaying() {

        mode = PLAYING

        addSystems(playSystems)
        //addScoller("Clear The Space !")
        //addInitialStars()
        //addTurret()

        World.level = 1
        //addPanels()
        //addRecallButton()
        // add score
        var scoreText = Entity()
        scoreText.add(PositionComponent(Assets.VIEWPORT_WIDTH - 250, Assets.VIEWPORT_HEIGHT - 40))
        scoreText.add(TextComponent("score : " + World.score))
        scoreText.add(ScoreComponent())
        game.engine.addEntity(scoreText)
        entitiesToRemove.add(scoreText)

        // add health
        var healthText = Entity()
        healthText.add(PositionComponent(Assets.VIEWPORT_WIDTH - 250, Assets.VIEWPORT_HEIGHT - 80))
        fun updateHealth() = { "health: " + station.getHealth()  }
        station.add(HealthComponent(World.STATION_HEALTH, { setGameOver() }))
        healthText.add(UpdatingTextComponent("health : " + World.STATION_HEALTH, false, updateHealth() ))
        game.engine.addEntity(healthText)
        entitiesToRemove.add(healthText)

        // add number of astronauts
        var astroText = Entity()
        astroText.add(PositionComponent(Assets.VIEWPORT_WIDTH - 250, Assets.VIEWPORT_HEIGHT - 120))
        fun updateAstro() = { "astro : " + World.astronauts }
        astroText.add(UpdatingTextComponent("astro : " + World.astronauts, false, updateAstro()))
        game.engine.addEntity(astroText)
        entitiesToRemove.add(astroText)
    }

     fun setGameOver() {
        mode = GAME_OVER

        //game.engine.removeSystem(game.engine.getSystem(MovementSystem::class.java))
        game.engine.getSystem(MovementSystem::class.java).setProcessing(false)
         game.engine.getSystem(StarfieldSystem::class.java).setProcessing(false)
        game.engine.removeSystem(game.engine.getSystem(CollisionSystem::class.java))
        game.engine.removeSystem(game.engine.getSystem(ScoreSystem::class.java))
        game.engine.removeSystem(game.engine.getSystem(IncidentalSystem::class.java))
        game.engine.removeSystem(game.engine.getSystem(AddAsteroidSystem::class.java))
        game.engine.removeSystem(game.engine.getSystem(AddObjectSystem::class.java))
        game.engine.removeSystem(game.engine.getSystem(MouseSystem::class.java))

        var gameOverText = Entity()
        gameOverText.add(PositionComponent(Assets.VIEWPORT_WIDTH/2, Assets.VIEWPORT_HEIGHT/2 + 200))
        gameOverText.add(TextComponent("Game Over !", true))
        game.engine.addEntity(gameOverText)
        entitiesToRemove.add(gameOverText)

        var startText = Entity()
        startText.add(PositionComponent(Assets.VIEWPORT_WIDTH/2, 100f))
        startText.add(TextComponent("Press space key", true))
        game.engine.addEntity(startText)
        entitiesToRemove.add(startText)

        World.lastScore = World.score
        World.score = 0
        if (World.lastScore > World.highScore) World.highScore = World.lastScore

         station.destroy()
    }

    fun attractToPlaying() {
        // Text off
        for (entity in entitiesToRemove)
            game.engine.removeEntity(entity)
        entitiesToRemove.clear()
        // Logo out
        var logoPC = logo.getComponent(PositionComponent::class.java)
        var tweenPos = Tween.to(logoPC, PositionAccessor.POSITION, 2f).target(logoPC.x, Assets.VIEWPORT_HEIGHT + 200f).ease(Bounce.OUT)
        var tweenScale = Tween.to(logoPC, PositionAccessor.SCALE, 2f).target(0.1f, 0.1f)
        game.engine.getSystem(TweenSystem::class.java).addTween(tweenPos)
        game.engine.getSystem(TweenSystem::class.java).addTween(tweenScale)

        // any existing asteroids/objects out
        var things = game.engine.getEntitiesFor(Family.one(AsteroidComponent::class.java,
                ItemComponent::class.java).get())
        for (thing in things) {
            thing.remove(CollisionComponent::class.java)
            var mc = thing.getComponent(MovementComponent::class.java)
            mc.velocityX = -300f
        }

        // World smaller
        var worldPC = theWorld.getComponent(PositionComponent::class.java)
        var worldMove = Tween.to(worldPC, PositionAccessor.POSITION, 2f).target(worldPC.x, worldPC.y - 100)
        game.engine.getSystem(TweenSystem::class.java).addTween(worldMove)

        // Station in
        station.addStation()
        station.tweenIn({ setPlaying() })
    }

    fun gameOverToAttract() {
        for (entity in entitiesToRemove)
            game.engine.removeEntity(entity)
        entitiesToRemove.clear()
        //station.dispose()
        //game.engine.removeEntity(station)
        //game.engine.removeEntity(turret)

        // move the world back up
        var worldPC = theWorld.getComponent(PositionComponent::class.java)
        var worldMove = Tween.to(worldPC, PositionAccessor.POSITION, 2f).target(worldPC.x, worldPC.y + 100)
        game.engine.getSystem(TweenSystem::class.java).addTween(worldMove)

        game.engine.getSystem(MovementSystem::class.java).setProcessing(true)
        game.engine.getSystem(StarfieldSystem::class.java).setProcessing(true)
        setAttract()
    }

    fun addTheWorld() {
        var worldTex = Texture(Assets.WORLD)
        var worldPC = PositionComponent(Assets.VIEWPORT_WIDTH/2 - worldTex.width/2, -800f,
                worldTex.width.toFloat(), worldTex.height.toFloat())
        theWorld.add(worldPC)
        theWorld.add(VisualComponent(worldTex))
        theWorld.add(MovementComponent(0f, 0f, 6f))
        game.engine.addEntity(theWorld)
    }

    fun addInitialStars() {
        for (i in 1..30) {
            var star = Star(MathUtils.random(0f, Assets.VIEWPORT_WIDTH), MathUtils.random(0f, Assets.VIEWPORT_HEIGHT))
            game.engine.addEntity(star)
        }
    }

    private fun addSystems(systems:Array<EntitySystem>) {
        for (system in systems) {
            game.engine.addSystem(system)
        }

    }

    private fun keyListener() {
        if (mode == PLAYING) {
            if (!firing) {
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)
                        || Gdx.input.isKeyPressed(Input.Keys.Q)) {
                    station.turretLeft(2f)
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                        || Gdx.input.isKeyPressed(Input.Keys.W)
                ) {
                    station.turrentRight(2f)
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                pauseSystems()
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                resumeSystems()
            }
        }

        if (mode == ATTRACT) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                //setPlaying()
                mode = TRANSITION
                attractToPlaying()
            }
        } else if (mode == GAME_OVER) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                mode = TRANSITION
                gameOverToAttract()
            }
        }

    }

    fun pauseSystems() {
        for (system in game.engine.systems) {
            if (system.javaClass != RenderingSystem::class.java)
                system.setProcessing(false)
        }
    }

    fun resumeSystems() {
        for (system in game.engine.systems) {
            system.setProcessing(true)
        }
    }


    override fun resize(width: Int, height: Int) {
        game.engine.getSystem(RenderingSystem::class.java).resize(width, height)
    }
}
