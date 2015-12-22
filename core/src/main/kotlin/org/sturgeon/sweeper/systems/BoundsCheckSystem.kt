package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.Mappers
import org.sturgeon.sweeper.components.BoundsCheckComponent

class BoundsCheckSystem : IteratingSystem(Family.all(BoundsCheckComponent::class.java).get()) {

    private val BORDER = 400

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        var pc = Mappers.positionMapper.get(entity)

        if (pc.x > Assets.VIEWPORT_WIDTH + BORDER
                || pc.x < -BORDER
                || pc.y > Assets.VIEWPORT_HEIGHT + BORDER
                || pc.y < -BORDER) {
            engine.removeEntity(entity)
        }

    }

}