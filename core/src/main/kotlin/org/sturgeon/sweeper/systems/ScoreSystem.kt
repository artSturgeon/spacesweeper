package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import org.sturgeon.sweeper.Mappers
import org.sturgeon.sweeper.World
import org.sturgeon.sweeper.components.ScoreComponent


class ScoreSystem : IntervalIteratingSystem(Family.all(ScoreComponent::class.java).get(), 0.1f) {

    var lastScore = 0
    var currentScore = 0

    override fun processEntity(entity: Entity?) {
        if (lastScore == World.score) return

        currentScore++
        var tx = Mappers.textMapper.get(entity)
        tx.text = "Score : " + currentScore

        if (currentScore == World.score) lastScore = currentScore
    }
}