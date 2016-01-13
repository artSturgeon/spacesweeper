package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.EntitySystem
import org.sturgeon.sweeper.World


class LevelSystem: EntitySystem() {

    //val levelScores = arrayOf(200, 500, 1000)

    override fun update(deltaTime: Float) {
        if (World.score > World.level * 100) {
            println("level up, level: " + World.level + ", score: " + World.score)
            World.level ++
            levelUp()
        }
    }

    fun levelUp() {
        var sys = engine.getSystem(AddAsteroidSystem::class.java)
        sys.interval -= 0.5f
        if (sys.interval <= 0.5f) sys.interval = 0.5f
        engine.getSystem(BigTextSystem::class.java).addBigText("Level " + World.level + " ! !")
    }

}