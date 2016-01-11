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
        var bc = Mappers.boundsCheckMapper.get(entity)

        var border = BORDER + bc.override

        if (pc.x > Assets.VIEWPORT_WIDTH + border
                || pc.x < -border
                || pc.y > Assets.VIEWPORT_HEIGHT + border
                || pc.y < -border) {
            engine.removeEntity(entity)
            // optional callback when something goes out of bounds
            bc.callback()
        }

    }

}