package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.PlayScreen
import org.sturgeon.sweeper.components.*
import org.sturgeon.sweeper.cos
import org.sturgeon.sweeper.sin


class FiringSystem : IteratingSystem(Family.all(FiringComponent::class.java).get()) {

    private var laserSound:Sound
    private var bulletTime = 1.0f
    private var left = true

    init {
        laserSound = Gdx.audio.newSound(Assets.LASER)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        bulletTime += deltaTime
        var turretAC = entity!!.getComponent(AnimationComponent::class.java)
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            if (bulletTime > 0.25f) {
                bulletTime = 0f
                turretAC.running = true
                laserSound.play()
                shoot(entity)
                PlayScreen.firing = true
            }
        } else {
            turretAC.running = false
            PlayScreen.firing = false
        }
    }

    private fun shoot(entity:Entity?) {
        var bullet = Entity()
        var turretPC = entity!!.getComponent(PositionComponent::class.java)


        var rad:Float = Math.toRadians(turretPC.angle.toDouble()).toFloat()

        var texture = Texture(Assets.BULLET)

        var adjust = 0.2f
        if (left) adjust *= -1

        //var x = (turretPC.x + turretPC.width/2f - texture.width/2) + 90f * cos(rad+adjust) // +0.35f
        //var y = (turretPC.y + turretPC.height/2f - texture.height/2) + 90f * sin(rad+adjust)
        var x = (turretPC.x + turretPC.originX - texture.width/2) + 90f * cos(rad+adjust) // +0.35f
        var y = (turretPC.y + turretPC.originY - texture.height/2) + 90f * sin(rad+adjust)

        var pc = PositionComponent(x, y, texture.width.toFloat(), texture.height.toFloat())
        pc.originX = 8f
        pc.originY = 8f
        bullet.add(pc)
        bullet.add(VisualComponent(texture, 1000))

        bullet.add(MovementComponent(200f * cos(rad), 200f * sin(rad)))
        bullet.add(BoundsCheckComponent())
        bullet.add(BulletComponent())

        engine.addEntity(bullet)
        left = !left
    }

}