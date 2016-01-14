package org.sturgeon.sweeper.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class VisualComponent(t: Texture, z:Int = 10, a:Float = 1f): Component {
    val texture: Texture = t
    val region: TextureRegion by lazy { TextureRegion(texture) }
    var zOrder = z
    var alpha = a
}