package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import org.sturgeon.sweeper.World
import org.sturgeon.sweeper.components.HealthComponent
import org.sturgeon.sweeper.components.TextComponent


class HealthSystem : IteratingSystem(Family.all(HealthComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        var tx = entity!!.getComponent(TextComponent::class.java)
        //tx.text = "Health: " + World.health

    }

}