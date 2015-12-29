package org.sturgeon.sweeper.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import org.sturgeon.sweeper.components.ParticleComponent

class Particle(x: Float, y: Float, partFile: FileHandle) : Entity() {
    var particleEffect = ParticleEffect()

    init {
        //var pe = ParticleEffect()
        particleEffect.load(partFile, Gdx.files.internal(""))

        for (emitter in particleEffect.emitters)
            emitter.setPosition(x, y)

        particleEffect.start()
        add(ParticleComponent())
    }
}