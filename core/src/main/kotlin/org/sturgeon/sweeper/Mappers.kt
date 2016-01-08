package org.sturgeon.sweeper

import com.badlogic.ashley.core.ComponentMapper
import org.sturgeon.sweeper.components.*

/**
 * Created by henri on 20/12/2015.
 */
object Mappers {
    val visualMapper = ComponentMapper.getFor(VisualComponent::class.java)
    val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    val movementMapper = ComponentMapper.getFor(MovementComponent::class.java)
    val textMapper = ComponentMapper.getFor(TextComponent::class.java)
    val updatingTextMapper = ComponentMapper.getFor(UpdatingTextComponent::class.java)
    val animationMapper = ComponentMapper.getFor(AnimationComponent::class.java)
    val boundsCheckMapper = ComponentMapper.getFor(BoundsCheckComponent::class.java)
    val playerMapper = ComponentMapper.getFor(PlayerComponent::class.java)
    val targetMapper = ComponentMapper.getFor(TargetComponent::class.java)
}