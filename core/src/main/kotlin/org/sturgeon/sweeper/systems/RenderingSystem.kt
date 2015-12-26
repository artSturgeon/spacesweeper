package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
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

    var stateTime = 0f
    var firingAnimation: Animation

    init {
        camera.position.set(Assets.VIEWPORT_WIDTH/2, Assets.VIEWPORT_HEIGHT/2, 0f)
        viewport = FitViewport(Assets.VIEWPORT_WIDTH, Assets.VIEWPORT_HEIGHT, camera)
        bitmapFont.data.setScale(2.0f)

        // Texture with all frames
        var firing = Texture(Assets.TURRET_ANIMATION)
        // 2d array with frames split by width/height
        var tmp = TextureRegion.split(firing, firing.width, firing.height/10)
        // 1d array with consecutive frames
        var firingFrames = Array<TextureRegion>(10, { i -> tmp[i][0] })
        // animation, constructor takes varargs, so using Kotlin spread operator *
        firingAnimation = com.badlogic.gdx.graphics.g2d.Animation(0.02f, *firingFrames)
    }


    override fun addedToEngine(engine: Engine?) {
        textures = engine!!.getEntitiesFor(Family.all(VisualComponent::class.java).get())
        fonts = engine!!.getEntitiesFor(Family.all(TextComponent::class.java).get())
        updatingFonts = engine!!.getEntitiesFor(Family.all(UpdatingTextComponent::class.java).get())
        //queue = textures.sortedBy { it.getComponent(VisualComponent::class.java).zOrder }
    }

    override fun update(deltaTime: Float) {
        stateTime += deltaTime
        queue = textures.sortedBy { it.getComponent(VisualComponent::class.java).zOrder }

        camera.update()
        batch.projectionMatrix = camera.combined

        var currentFrame = firingAnimation.getKeyFrame(stateTime, true)

        batch.begin()

        drawTextures(queue)
        drawFonts()

        batch.draw(currentFrame, 100f, 100f)

        batch.end()

    }

    private fun drawTextures(q: List<Entity>) {
        for (texture in q) {
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