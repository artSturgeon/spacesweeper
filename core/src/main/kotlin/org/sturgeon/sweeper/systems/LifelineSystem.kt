package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family

import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.World
import org.sturgeon.sweeper.components.*

class LifelineSystem : EntitySystem() {

    lateinit private var astronauts: ImmutableArray<Entity>
    lateinit private var lines: ImmutableArray<Entity>
    var snap: Sound

    init {
        snap = Gdx.audio.newSound(Assets.SND_SNAP)
    }

    override fun addedToEngine(engine: Engine?) {
        astronauts = engine!!.getEntitiesFor(Family.all(AstronautComponent::class.java, AliveComponent::class.java, ConnectedComponent::class.java).get())
        lines = engine!!.getEntitiesFor(Family.all(LineComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        if (astronauts.size() > 0 && lines.size() > 0) {
            // Set to only one astronaut and one line
            //println("I see the astronaut")
            var astronaut = astronauts.get(0)
            var line = lines.get(0)

            var lc = line.getComponent(LineComponent::class.java)
            var astronautPC = astronaut.getComponent(PositionComponent::class.java)
            lc.lineEnd.x = astronautPC.x + astronautPC.width/2
            lc.lineEnd.y = astronautPC.y + astronautPC.height/2

            var length = lc.lineStart.dst(lc.lineEnd)

            if (length > World.LIFELINE_LENGTH) {
                // snap?
               snap(astronaut, line)
            }

        }
    }

    fun snap(astronaut: Entity, line:Entity) {
        engine.removeEntity(line)
        astronaut.remove(ConnectedComponent::class.java)
        astronaut.remove(TargetComponent::class.java)
        World.astronauts--
        World.astronautHealth = 0
        snap.play()
        engine.getSystem(BigTextSystem::class.java).addBigText("Lifeline Snapped ! !")
    }
}