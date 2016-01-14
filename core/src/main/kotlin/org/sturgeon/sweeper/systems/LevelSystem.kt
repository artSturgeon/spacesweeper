package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.EntitySystem
import org.sturgeon.sweeper.World


class LevelSystem: EntitySystem() {

    //val levelScores = arrayOf(200, 500, 1000)

    override fun update(deltaTime: Float) {
        if (World.score > World.level * 100) {
            World.level ++
            levelUp()
        }
    }

    fun levelUp() {
        // make asteroids more frequent
        var sys = engine.getSystem(AddAsteroidSystem::class.java)
        sys.interval -= 0.5f
        if (sys.interval <= 0.5f) sys.interval = 0.5f

        // and faster
        World.asteroidSpeedMin -= 25
        World.asteroidSpeedMax -= 25
        World.asteroidSpeedY += 5

        // less chance of an object
        World.objectChance -= World.objectDecrement
        if (World.objectChance < 20) World.objectChance = 20

        // add some nice big text
        engine.getSystem(BigTextSystem::class.java).addBigText("Level " + World.level + " ! !")
    }

}