package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import org.sturgeon.sweeper.Mappers
import org.sturgeon.sweeper.World
import org.sturgeon.sweeper.components.ScoreComponent


class ScoreSystem : IntervalIteratingSystem(Family.all(ScoreComponent::class.java).get(), 0.02f) {

    var lastScore = 0
    var currentScore = 0

    override fun processEntity(entity: Entity?) {
        if (lastScore == World.score) return

        currentScore++
        var tx = Mappers.textMapper.get(entity)
        tx.text = "Score : " + currentScore

        if (currentScore == World.score) lastScore = currentScore
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        lastScore = 0
        currentScore = 0
    }
}