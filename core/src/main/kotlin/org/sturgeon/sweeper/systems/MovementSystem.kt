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

        pc.angle += mc.velocityAngular * deltaTime

        // check if there's a target
        var tc = Mappers.targetMapper.get(entity)
        if (tc != null && !tc.targetReached) {
            if (tc.target.dst(pc.x, pc.y) <= 5) {
                mc.velocityX = 0f
                mc.velocityY = 0f
                tc.targetReached = true
                tc.targetReachedCallback()
            }
        }
    }
}