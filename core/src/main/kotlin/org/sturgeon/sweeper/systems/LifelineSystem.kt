package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family

import com.badlogic.ashley.utils.ImmutableArray
import org.sturgeon.sweeper.World
import org.sturgeon.sweeper.components.AliveComponent
import org.sturgeon.sweeper.components.AstronautComponent
import org.sturgeon.sweeper.components.LineComponent
import org.sturgeon.sweeper.components.PositionComponent

class LifelineSystem : EntitySystem() {

    lateinit private var astronauts: ImmutableArray<Entity>
    lateinit private var lines: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine?) {
        astronauts = engine!!.getEntitiesFor(Family.all(AstronautComponent::class.java, AliveComponent::class.java).get())
        lines = engine!!.getEntitiesFor(Family.all(LineComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        if (astronauts.size() > 0 && lines.size() > 0) {
            // Set to only one astronaut and one line
            var astronaut = astronauts.get(0)
            var line = lines.get(0)

            var lc = line.getComponent(LineComponent::class.java)
            lc.lineEnd.x = astronaut.getComponent(PositionComponent::class.java).x
            lc.lineEnd.y = astronaut.getComponent(PositionComponent::class.java).y

            var length = lc.lineStart.dst(lc.lineEnd)
            if (length <= 10) {
                // dock
                engine.removeEntity(astronaut)
                engine.removeEntity(line)
            } else if (length > 300) {
                // snap?
                engine.removeEntity(line)
                astronaut.remove(AliveComponent::class.java)
                World.astronauts--
            }


        }
    }
}