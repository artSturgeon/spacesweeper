package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import org.sturgeon.sweeper.Mappers
import org.sturgeon.sweeper.components.MovementComponent
import org.sturgeon.sweeper.components.PositionComponent


class MovementSystem : IteratingSystem(Family.all(MovementComponent::class.java, PositionComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        var pc = Mappers.positionMapper.get(entity)
        var mc = Mappers.movementMapper.get(entity)

        pc.x += mc.velocityX * deltaTime
        pc.y += mc.velocityY * deltaTime
    }
}