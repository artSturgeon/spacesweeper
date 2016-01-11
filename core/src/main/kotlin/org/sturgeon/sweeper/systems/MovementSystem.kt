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
            //var tx = pc.x + pc.width/2
            //var ty = pc.y + pc.height/2
            //var d1 = tc.target.dst(pc.x, pc.y)
            //var d2 = tc.target.dst(tx, ty)
            //println("dst  : " +d1)
            //println("dst 2: " + d2)
            //println("Movement system, distance to target: " + d2)
            if (tc.target.dst(pc.x, pc.y) <= 5) {
                println("reached target")
                mc.velocityX = 0f
                mc.velocityY = 0f
                tc.targetReached = true
                tc.targetReachedCallback()
            }
        }
    }
}