package org.sturgeon.sweeper.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import org.sturgeon.sweeper.Assets
import org.sturgeon.sweeper.Mappers
import org.sturgeon.sweeper.components.*
import org.sturgeon.sweeper.entities.Particle
import java.util.*

class RenderingSystem: EntitySystem() {

    private val batch: SpriteBatch by lazy { SpriteBatch() }
    private val camera: OrthographicCamera by lazy { OrthographicCamera(Assets.VIEWPORT_WIDTH, Assets.VIEWPORT_HEIGHT) }
    private val shapeRenderer by lazy { ShapeRenderer() }

    lateinit var viewport: Viewport
    lateinit private var textures: ImmutableArray<Entity>
    lateinit private var allFonts:ImmutableArray<Entity>
    lateinit private var updatingFonts:ImmutableArray<Entity>
    lateinit private var animations: ImmutableArray<Entity>
    lateinit private var particles: ImmutableArray<Entity>
    lateinit private var lines: ImmutableArray<Entity>

    lateinit private var queue: List<Entity>

    private var bitmapFont = BitmapFont()
    private var glyphLayout = GlyphLayout()

    init {
        camera.position.set(Assets.VIEWPORT_WIDTH/2, Assets.VIEWPORT_HEIGHT/2, 0f)
        viewport = FitViewport(Assets.VIEWPORT_WIDTH, Assets.VIEWPORT_HEIGHT, camera)
        createFont()
        //bitmapFont.data.setScale(2.0f)
    }

    override fun addedToEngine(engine: Engine?) {
        textures = engine!!.getEntitiesFor(Family.all(VisualComponent::class.java).get())
        allFonts = engine!!.getEntitiesFor(Family.all(TextComponent::class.java).get())
        updatingFonts = engine!!.getEntitiesFor(Family.all(UpdatingTextComponent::class.java).get())
        animations = engine!!.getEntitiesFor(Family.all(AnimationComponent::class.java, PositionComponent::class.java).get())
        particles = engine!!.getEntitiesFor(Family.all(ParticleComponent::class.java).get())
        lines = engine!!.getEntitiesFor(Family.all(LineComponent::class.java).get())
        //queue = textures.sortedBy { it.getComponent(VisualComponent::class.java).zOrder }
    }

    override fun update(deltaTime: Float) {

        queue = textures.sortedBy { it.getComponent(VisualComponent::class.java).zOrder }

        var frontFonts = allFonts.filter { font -> font.getComponent(TextComponent::class.java).front }
        var backFonts = allFonts.filter { font -> !font.getComponent(TextComponent::class.java).front }

        camera.update()
        batch.projectionMatrix = camera.combined

        batch.begin()
        drawFonts(backFonts)
        drawTextures(queue)
        drawLines()
        drawAnimations(deltaTime)
        drawPes(deltaTime)
        drawFonts(frontFonts)

        batch.end()
    }


    fun drawLines() {
        for (line in lines) {
            var lc = line.getComponent(LineComponent::class.java)
            batch.end()

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(172/255f, 50/255f,50/255f, 1.0f)
            //shapeRenderer.set
            //shapeRenderer.line(lc.lineStart.x, lc.lineStart.y, lc.lineEnd.x, lc.lineEnd.y);
            shapeRenderer.rectLine(lc.lineStart.x, lc.lineStart.y, lc.lineEnd.x, lc.lineEnd.y, 3f)
            shapeRenderer.end();

            batch.begin()
        }
    }

    /*
    fun debugRect(station:Entity) {
        var shapeRenderer = ShapeRenderer()

        if (station != null) {
            batch.end()

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(pc.x + 380, pc.y + 50, 500f, 95f);
            shapeRenderer.end();

            batch.begin()
        }
    }
*/

    private fun drawAnimations(deltaTime: Float) {
        for (animation in animations) {
            var ac = Mappers.animationMapper.get(animation)
            var pc = Mappers.positionMapper.get(animation)

            if (ac.running)
                ac.stateTime += deltaTime

            var currentFrame = ac.anim.getKeyFrame(ac.stateTime, ac.loop)

            //batch.draw(currentFrame, pc.x, pc.y)
            batch.draw(currentFrame,            // texture region
                    pc.x, pc.y,                 // x, y position
                    pc.originX(), pc.originY(), // origin
                    pc.width, pc.height,        // width and height
                    pc.scaleX, pc.scaleY,       // scale
                    pc.angle)                   // angle
        }
    }

    private fun drawTextures(q: List<Entity>) {
        for (texture in q) {
            var vc = Mappers.visualMapper.get(texture)

            var pc = Mappers.positionMapper.get(texture)

            //SpriteBatch.draw(textureRegion, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
            //batch.draw(vc.region, pc.x, pc.y)
            batch.setColor(batch.color.r, batch.color.g, batch.color.b, vc.alpha)
            batch.draw(vc.region,               // texture region
                    pc.x, pc.y,                 // x, y position
                    pc.originX(), pc.originY(), // origin
                    pc.width, pc.height,        // width and height
                    pc.scaleX, pc.scaleY,                     // scale
                    pc.angle)                   //angle
        }
    }

    fun drawPes(deltaTime:Float) {
        //var toRemove = ArrayList<ParticleEffect>()
        for (particle in particles) {
            if (particle is Particle) {
                var particleEffect = particle.particleEffect
                particleEffect.update(deltaTime)
                particleEffect.draw(batch)

                if (particleEffect.isComplete) {
                    engine.removeEntity(particle)
                }
            }
        }
    }

    private fun drawFonts(fonts: List<Entity>) {

        for (font in fonts) {
            var tx = Mappers.textMapper.get(font)
            var pc = Mappers.positionMapper.get(font)

            bitmapFont.data.setScale(tx.scale)
            bitmapFont.color = tx.colour

            drawText(tx.text, tx.center, pc.x, pc.y)
        }

        for (font in updatingFonts) {
            var tx = Mappers.updatingTextMapper.get(font)
            var pc = Mappers.positionMapper.get(font)

            bitmapFont.data.setScale(1.0f)
            bitmapFont.color = Color.WHITE

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

    // yargle...
    fun getTextWidth(txt:String, scale: Float):Float {
        bitmapFont.data.setScale(scale)
        glyphLayout.setText(bitmapFont, txt)
        return glyphLayout.width
    }

    fun createFont() {
        var t = Texture(Assets.FONT_IMAGE)
        bitmapFont = BitmapFont(Assets.FONT_FILE, TextureRegion(t))
        bitmapFont.color = Color.WHITE
    }

    public fun unproject(x: Float, y: Float):Vector3 {
        var touchPoint = Vector3(x, y, 0f)
        var viewPoint = camera.unproject(touchPoint,
                viewport.screenX.toFloat(), viewport.screenY.toFloat(),
                viewport.screenWidth.toFloat(),viewport.screenHeight.toFloat())
        return viewPoint
    }

    // Hacky, need to keep the viewport in sync
    fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}