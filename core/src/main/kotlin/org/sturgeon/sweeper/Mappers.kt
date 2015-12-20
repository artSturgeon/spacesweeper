package org.sturgeon.sweeper

import com.badlogic.ashley.core.ComponentMapper
import org.sturgeon.sweeper.components.PositionComponent
import org.sturgeon.sweeper.components.VisualComponent

/**
 * Created by henri on 20/12/2015.
 */
object Mappers {
    val visualMapper = ComponentMapper.getFor(VisualComponent::class.java)
    val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
}