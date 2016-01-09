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
import org.sturgeon.sweeper.Accessors.PositionAccessor
import org.sturgeon.sweeper.components.*
import org.sturgeon.sweeper.entities.Star
import org.sturgeon.sweeper.entities.Station
import org.sturgeon.sweeper.systems.*
import java.util.*

class PlayScreen(var game: SpaceSweeper) : ScreenAdapter() {

    val alwaysSystems = arrayOf(RenderingSystem(), TweenSystem(), MovementSystem(), BoundsCheckSystem())
    val attractSystems:Array<EntitySystem> = arrayOf(StarfieldSystem(0.1f), BigTextSystem(30f))

    // lazy init for these so they don't take effect until needed
    val playSystems:Array<EntitySystem> by lazy {
        arrayOf(FiringSystem()
                , AddAsteroidSystem(1f)
                , AddObjectSystem(2f)
                , CollisionSystem(this)
                , LifelineSystem()
                , ScoreSystem()
                , HealthSystem(), MouseSystem(this))
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
        station = Station(game.engine)
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


        station.addPanels()
        station.addRecallButton { recallClicked() }
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
        game.engine.removeSystem(game.engine.getSystem(CollisionSystem::class.java))
        game.engine.removeSystem(game.engine.getSystem(ScoreSystem::class.java))
        game.engine.removeSystem(game.engine.getSystem(IncidentalSystem::class.java))
        game.engine.removeSystem(game.engine.getSystem(AddAsteroidSystem::class.java))

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

        // World smaller
        var worldPC = theWorld.getComponent(PositionComponent::class.java)
        var worldMove = Tween.to(worldPC, PositionAccessor.POSITION, 2f).target(worldPC.x, worldPC.y - 100)
        game.engine.getSystem(TweenSystem::class.java).addTween(worldMove)

        // Station in
        station.tweenIn({ setPlaying() })
    }

    fun gameOverToAttract() {
        for (entity in entitiesToRemove)
            game.engine.removeEntity(entity)
        entitiesToRemove.clear()
        station.dispose()
        //game.engine.removeEntity(station)
        //game.engine.removeEntity(turret)

        // move the world back up
        var worldPC = theWorld.getComponent(PositionComponent::class.java)
        var worldMove = Tween.to(worldPC, PositionAccessor.POSITION, 2f).target(worldPC.x, worldPC.y + 100)
        game.engine.getSystem(TweenSystem::class.java).addTween(worldMove)

        game.engine.getSystem(MovementSystem::class.java).setProcessing(true)
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



    fun recallClicked() {
        // get the spaceman back home!
        println("recall clicked")
        // yikes!!
        // you know things are getting late in the day when I start
        //  putting comments in like yikes...
        var astronauts = game.engine.getEntitiesFor(Family.all(AstronautComponent::class.java, AliveComponent::class.java,
                ConnectedComponent::class.java).get())
        println("astronauts size: " + astronauts.size())
        var lines = game.engine.getEntitiesFor(Family.all(LineComponent::class.java).get())
        println("lines size: " + lines.size())
        if (astronauts.size() > 0 && lines.size() > 0) {
            println("I will recall the astronaut")
            var astronaut = astronauts.get(0)
            var line = lines.get(0)
            var mc = astronaut.getComponent(MovementComponent::class.java)
            if (mc == null) mc = MovementComponent(0f, 0f)
            astronaut.add(mc)
            var lineStart = line.getComponent(LineComponent::class.java).lineStart
            var lineEnd = line.getComponent(LineComponent::class.java).lineEnd
            // work out vectors to target
            var l = lineStart.cpy().sub(lineEnd).nor().scl(100f, 100f)
            mc.velocityX = l.x
            mc.velocityY = l.y
            astronaut.add(TargetComponent(lineStart, { World.station.dockAstronaut() }))
        }
    }

    fun addInitialStars() {
        for (i in 1..30) {
            var star = Star(MathUtils.random(0f, Assets.VIEWPORT_WIDTH), MathUtils.random(0f, Assets.VIEWPORT_HEIGHT))
            game.engine.addEntity(star)
        }
    }


/*
    public fun testWire() {
        var astronauts = game.engine.getEntitiesFor(Family.all(AstronautComponent::class.java).get())
        if (astronauts.size() == 1) {
            var astronautPC = astronauts.get(0).getComponent(PositionComponent::class.java)
            var stationPC = station.getComponent(PositionComponent::class.java)
            var r = Rectangle(stationPC.x + stationPC.width, stationPC.y + stationPC.height/2, astronautPC.x, astronautPC.y)


            game.engine.getSystem(RenderingSystem::class.java).lines.clear()
            game.engine.getSystem(RenderingSystem::class.java).lines.add(r)
        }
    }
*/
    fun addScoller(msg:String) {
        var scroller = Entity().apply {
            add(PositionComponent(Assets.VIEWPORT_WIDTH, 500f))
            add(MovementComponent(-700f, 0f))
            add(TextComponent(msg).apply {
                scale = 16.0f
                colour = Color(223/255f, 113/255f, 38/255f, 0.75f)
                front = false
            })
            add(BoundsCheckComponent(8000f))
        }
        game.engine.addEntity(scroller)

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
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)
                        || Gdx.input.isKeyPressed(Input.Keys.Q)) {
                    station.turretLeft(2f)
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                        || Gdx.input.isKeyPressed(Input.Keys.W)
                ) {
                    station.turrentRight(2f)
                }
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

    override fun resize(width: Int, height: Int) {
        game.engine.getSystem(RenderingSystem::class.java).resize(width, height)
    }
}
