package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.PlayScreen
import org.sturgeon.sweeper.components.*
import org.sturgeon.sweeper.cos
import org.sturgeon.sweeper.sin


class FiringSystem : IteratingSystem(Family.all(FiringComponent::class.java).get()) {

    private var bulletTime = 1f

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        bulletTime += deltaTime
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            if (bulletTime > 0.5f) {
                bulletTime = 0f
                shoot(entity)
                PlayScreen.firing = true
            }
        } else {
            PlayScreen.firing = false
        }
    }

    private fun shoot(entity:Entity?) {
        var bullet = Entity()
        var turretPC = entity!!.getComponent(PositionComponent::class.java)
        var rad:Float = Math.toRadians(turretPC.angle.toDouble()).toFloat()

        var texture = Texture(Assets.BULLET)
        var x = (turretPC.x + turretPC.width/2f - texture.width/2) + 38f * cos(rad)
        var y = (turretPC.y + turretPC.height/2f - texture.height/2) + 38f * sin(rad)

        var pc = PositionComponent(x, y, texture.width.toFloat(), texture.height.toFloat())
        pc.originX = 8f
        pc.originY = 8f
        bullet.add(pc)
        bullet.add(VisualComponent(texture))

        bullet.add(MovementComponent(100f * cos(rad), 100f * sin(rad)))
        bullet.add(BoundsCheckComponent())

        engine.addEntity(bullet)
    }

}