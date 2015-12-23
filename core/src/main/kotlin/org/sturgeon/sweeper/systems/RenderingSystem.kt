package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.Mappers
import org.sturgeon.sweeper.components.TextComponent
import org.sturgeon.sweeper.components.UpdatingTextComponent
import org.sturgeon.sweeper.components.VisualComponent

class RenderingSystem: EntitySystem() {

    private val batch: SpriteBatch by lazy { SpriteBatch() }
    private val camera: OrthographicCamera by lazy { OrthographicCamera(Assets.VIEWPORT_WIDTH, Assets.VIEWPORT_HEIGHT) }
    lateinit private var viewport: Viewport
    lateinit private var textures: ImmutableArray<Entity>
    lateinit private var fonts:ImmutableArray<Entity>
    lateinit private var updatingFonts:ImmutableArray<Entity>
    lateinit private var queue: List<Entity>

    private var bitmapFont = BitmapFont()
    private var glyphLayout = GlyphLayout()

    init {
        camera.position.set(Assets.VIEWPORT_WIDTH/2, Assets.VIEWPORT_HEIGHT/2, 0f)
        viewport = FitViewport(Assets.VIEWPORT_WIDTH, Assets.VIEWPORT_HEIGHT, camera)
        bitmapFont.data.setScale(2.0f)
    }

    override fun addedToEngine(engine: Engine?) {
        textures = engine!!.getEntitiesFor(Family.all(VisualComponent::class.java).get())
        fonts = engine!!.getEntitiesFor(Family.all(TextComponent::class.java).get())
        updatingFonts = engine!!.getEntitiesFor(Family.all(UpdatingTextComponent::class.java).get())
        queue = textures.sortedBy { it.getComponent(VisualComponent::class.java).zOrder }
    }

    override fun update(deltaTime: Float) {

        camera.update()
        batch.projectionMatrix = camera.combined

        batch.begin()

        drawTextures(queue)
        drawFonts()

        batch.end()

    }

    private fun drawTextures(q: List<Entity>) {
        for (texture in textures) {
            var vc = Mappers.visualMapper.get(texture)

            var pc = Mappers.positionMapper.get(texture)
            //SpriteBatch.draw(textureRegion, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
            //batch.draw(vc.region, pc.x, pc.y)
            batch.draw(vc.region,               // texture region
                    pc.x, pc.y,                 // x, y position
                    pc.originX(), pc.originY(), // origin
                    pc.width, pc.height,        // width and height
                    pc.scaleX, pc.scaleY,                     // scale
                    pc.angle)                   //angle
        }
    }

    private fun drawFonts() {
        for (font in fonts) {
            var tx = Mappers.textMapper.get(font)
            var pc = Mappers.positionMapper.get(font)

            drawText(tx.text, tx.center, pc.x, pc.y)
        }

        for (font in updatingFonts) {
            var tx = Mappers.updatingTextMapper.get(font)
            var pc = Mappers.positionMapper.get(font)

            var txt = tx.updateText()
            drawText(txt, tx.center, pc.x, pc.y)
        }
    }

    private fun drawText(txt:String, center:Boolean, x:Float, y:Float) {
        glyphLayout.setText(bitmapFont, txt)

        var pcx = x
        if (center) pcx -= glyphLayout.width / 2

        bitmapFont.draw(batch, glyphLayout, pcx, y)
    }




}