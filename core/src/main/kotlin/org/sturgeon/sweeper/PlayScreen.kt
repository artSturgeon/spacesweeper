package org.sturgeon.sweeper

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Texture
import org.sturgeon.sweeper.components.PositionComponent
import org.sturgeon.sweeper.components.VisualComponent
import org.sturgeon.sweeper.systems.RenderingSystem


class PlayScreen(var game: SpaceSweeper) : ScreenAdapter() {

    init {
        createWorld()
    }

    override fun render(delta: Float) {
        game.engine.update(delta)
    }

    private fun createWorld() {

        game.engine.addSystem(RenderingSystem())

        var player = Entity()
        player.add(PositionComponent(0f, 0f))
        player.add(VisualComponent(Texture(Assets.PLAYER)))
        game.engine.addEntity(player)
    }

}