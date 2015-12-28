package org.sturgeon.sweeper

import aurelienribon.tweenengine.BaseTween
import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenCallback
import aurelienribon.tweenengine.equations.Bounce
import aurelienribon.tweenengine.equations.Sine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import org.sturgeon.sweeper.Accessors.PositionAccessor
import org.sturgeon.sweeper.components.*
import org.sturgeon.sweeper.entities.Star
import org.sturgeon.sweeper.systems.*
import java.util.*

class PlayScreen(var game: SpaceSweeper) : ScreenAdapter() {

    var alwaysSystems = arrayOf(RenderingSystem(), TweenSystem(), MovementSystem(), BoundsCheckSystem())
    var attractSystems:Array<EntitySystem> = arrayOf(StarfieldSystem(0.1f))
    var playSystems = arrayOf(FiringSystem()
                    ,AddAsteroidSystem(1f)
                    ,CollisionSystem()
                    ,ScoreSystem(), HealthSystem())

    private var station = Entity()
    private var turret = Entity()
    private var logo = Entity()
    private var theWorld = Entity()

    private var entitiesToRemove = ArrayList<Entity>()

    val ATTRACT = 1
    val PLAYING = 2
    val GAME_OVER = 3

    var mode = ATTRACT

    companion object {
        var firing = false
    }

    init {
        addSystems(alwaysSystems)
        setAttract()
        //setPlaying()
    }

    override fun render(delta: Float) {
        keyListener()
        game.engine.update(delta)
    }

    private fun setAttract() {

        mode = ATTRACT
        addSystems(attractSystems)

        addInitialStars()
        
        var t = Texture(Assets.LOGO)
        var pc = PositionComponent(Assets.VIEWPORT_WIDTH/2 - t.width/2, Assets.VIEWPORT_HEIGHT+ 200, t.width.toFloat(), t.height.toFloat())
        pc.scaleX = 0.1f
        pc.scaleY = 0.1f
        logo.add(pc)
        logo.add(VisualComponent(t))
        game.engine.addEntity(logo)

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
        startText.add(TextComponent("Press any key to start", true))
        game.engine.addEntity(startText)
        entitiesToRemove.add(startText)

        var worldTex = Texture(Assets.WORLD)
        var worldPC = PositionComponent(Assets.VIEWPORT_WIDTH/2 - worldTex.width/2, -800f,
                worldTex.width.toFloat(), worldTex.height.toFloat())
        theWorld.add(worldPC)
        theWorld.add(VisualComponent(worldTex))
        theWorld.add(MovementComponent(0f, 0f, 6f))
        game.engine.addEntity(theWorld)
    }

    private fun setPlaying() {

        mode = PLAYING

        addSystems(playSystems)

        //addInitialStars()
        addTurret()
        addPanels()
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

        World.lastScore = World.score
        World.score = 0
        if (World.lastScore > World.highScore) World.highScore = World.lastScore

    }

    fun attractToPlaying() {
        // Text off
        for (entity in entitiesToRemove)
            game.engine.removeEntity(entity)

        // Logo out
        var logoPC = logo.getComponent(PositionComponent::class.java)
        var tweenPos = Tween.to(logoPC, PositionAccessor.POSITION, 2f).target(logoPC.x, Assets.VIEWPORT_HEIGHT + 200f).ease(Bounce.OUT)
        var tweenScale = Tween.to(logoPC, PositionAccessor.SCALE, 2f).target(0.1f, 0.1f)
        game.engine.getSystem(TweenSystem::class.java).addTween(tweenPos)
        game.engine.getSystem(TweenSystem::class.java).addTween(tweenScale)

        // World smaller
        var worldPC = theWorld.getComponent(PositionComponent::class.java)
        var worldMove = Tween.to(worldPC, PositionAccessor.POSITION, 2f).target(worldPC.x, worldPC.y - 100)
        game.engine.getSystem(TweenSystem::class.java).addTween(worldMove)

        // Station in
        addStation()
        var stationPC = station.getComponent(PositionComponent::class.java)
        var moveTween = Tween.to(stationPC, PositionAccessor.POSITION, 2f).target(Assets.VIEWPORT_WIDTH/2 - 850, stationPC.y).ease(Sine.OUT)
        game.engine.getSystem(TweenSystem::class.java).addTween(moveTween)

        // Set systems up
        tweenPos.setCallback(object: TweenCallback {
            override fun onEvent(type: Int, src: BaseTween<*>?) {
                when (type) {
                    TweenCallback.COMPLETE -> setPlaying()
                }
            }
        })
    }
    
    fun addStation() {
        var t = Texture(Assets.STATION)
        station.add(PositionComponent(Assets.VIEWPORT_WIDTH + 100, Assets.VIEWPORT_HEIGHT/2 - t.height/2, t.width.toFloat(), t.height.toFloat()))
        //station.add(PositionComponent(Assets.VIEWPORT_WIDTH/2 - t.width/2, Assets.VIEWPORT_HEIGHT/2 - t.height/2, t.width.toFloat(), t.height.toFloat()))
        station.add(VisualComponent(t))
        station.add(PlayerComponent())
        game.engine.addEntity(station)
    }

    fun addPanels() {
        addPanel(station.getComponent(PositionComponent::class.java).x + 480, station.getComponent(PositionComponent::class.java).y + 193f)
        addPanel(station.getComponent(PositionComponent::class.java).x + 630, station.getComponent(PositionComponent::class.java).y + 193f)
        addPanel(station.getComponent(PositionComponent::class.java).x + 570, station.getComponent(PositionComponent::class.java).y - 172f)
    }

    fun addPanel(x:Float, y:Float) {
        var panel = Entity()
        var t = Texture(Assets.SOLAR_PANEL)
        panel.add(PositionComponent(x, y
                                    ,
                                    t.width.toFloat(), t.height.toFloat()))
        panel.add(VisualComponent(t))
        game.engine.addEntity(panel)
    }

    private fun addTurret() {

        // Texture with all frames
        var firing = Texture(Assets.TURRET_ANIMATION)
        firing.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        // 2d array with frames split by width/height
        var tmp = TextureRegion.split(firing, firing.width, firing.height/10)
        // 1d array with consecutive frames
        var firingFrames = Array<TextureRegion>(10, { i -> tmp[i][0] })
        // animation, constructor takes varargs, so using Kotlin spread operator *
        var firingAnimation = Animation(0.05f, *firingFrames)

        var width = firingFrames[0].regionWidth
        var height = firingFrames[0].regionHeight

        var x = station.getComponent(PositionComponent::class.java).x + 825
        var y = station.getComponent(PositionComponent::class.java).y + 72

        var pc = PositionComponent(x, y, width.toFloat(), height.toFloat())

        pc.originX = 25f;
        pc.originY = 25f;

        turret.add(pc)
        turret.add(AnimationComponent(firingAnimation))
        turret.add(FiringComponent())
        game.engine.addEntity(turret)
    }

    fun addInitialStars() {
        for (i in 1..30) {
            var star = Star(MathUtils.random(0f, Assets.VIEWPORT_WIDTH), MathUtils.random(0f, Assets.VIEWPORT_HEIGHT))
            game.engine.addEntity(star)
        }
    }

    private fun addSystems(systems:Array<EntitySystem>) {
        /*
        for (system in game.engine.systems) {
            game.engine.removeSystem(system)
        }
        game.engine.removeAllEntities()

        for (system in alwaysSystems) {
            game.engine.addSystem(system)
        }
        */
        for (system in systems) {
            game.engine.addSystem(system)
        }

    }

    private fun keyListener() {
        if (mode == PLAYING) {
            if (!firing) {
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    var pc = turret.getComponent(PositionComponent::class.java)
                    pc.angle += 2f
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    var pc = turret.getComponent(PositionComponent::class.java)
                    pc.angle -= 2f
                }
            }
        }

        if (mode == ATTRACT) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                //setPlaying()
                attractToPlaying()
            }
        } else if (mode == GAME_OVER) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                setAttract()
            }
        }

    }

}