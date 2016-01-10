package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import org.sturgeon.sweeper.*
import org.sturgeon.sweeper.components.*


class FiringSystem : IteratingSystem(Family.all(FiringComponent::class.java).get()) {

    private var laserSound:Sound
    private var bulletTime = 100.0f // start high so we can always fire to start with
    private var left = true

    init {
        laserSound = Gdx.audio.newSound(Assets.SND_LASER)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        bulletTime += deltaTime
        var turretAC = entity!!.getComponent(AnimationComponent::class.java)
        if (Gdx.input.isKeyPressed(Input.Keys.Z)
            || Gdx.input.isKeyPressed(Input.Keys.C)) {
            if (bulletTime > World.firing_speed) {
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

        var adjust = 0.1f
        if (left) adjust *= -1

        //var x = (turretPC.x + turretPC.width/2f - texture.width/2) + 90f * cos(rad+adjust) // +0.35f
        //var y = (turretPC.y + turretPC.height/2f - texture.height/2) + 90f * sin(rad+adjust)
        var x = (turretPC.x + turretPC.originX - texture.width/2) + 80f * cos(rad+adjust) // +0.35f
        var y = (turretPC.y + turretPC.originY - texture.height/2) + 80f * sin(rad+adjust)

        var pc = PositionComponent(x, y, texture.width.toFloat(), texture.height.toFloat())
        pc.originX = 6f
        pc.originY = 3f
        pc.angle = turretPC.angle

        bullet.add(pc)
        bullet.add(VisualComponent(texture, 1000))

        bullet.add(MovementComponent(200f * cos(rad), 200f * sin(rad)))
        bullet.add(BoundsCheckComponent())
        bullet.add(BulletComponent())

        engine.addEntity(bullet)
        left = !left
    }

}