package org.ender_development.catalyx.client.tesr

import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.tiles.BaseTile
import org.ender_development.catalyx.tiles.TESRTile
import org.ender_development.catalyx.tiles.helper.IPortRenderer
import org.ender_development.catalyx.utils.RenderUtils
import kotlin.math.sqrt

object IORenderer2 : AbstractTESRenderer() {
	override fun render(
		tileEntity: BaseTile,
		x: Double,
		y: Double,
		z: Double,
		partialTicks: Float,
		destroyStage: Int,
		alpha: Float
	) {
		val provider = (tileEntity as? IPortRenderer) ?: return

		provider.getPortState().forEach { (side, state) ->
			if(state == TESRTile.IOType.DEFAULT)
				return@forEach

			val texture = ResourceLocation(Reference.MODID, "textures/blocks/io/${state.name.lowercase()}.png")

			GlStateManager.pushMatrix()
			GlStateManager.translate(x, y + 0.5, z)
			CubeSquareProjector.renderSquareOnFace(side.getCubeFace())
			GlStateManager.popMatrix()
		}
	}
}

fun EnumFacing.getCubeFace(): CubeFace {
	return when(this) {
		EnumFacing.NORTH -> CubeFace.NORTH
		EnumFacing.SOUTH -> CubeFace.SOUTH
		EnumFacing.EAST -> CubeFace.EAST
		EnumFacing.WEST -> CubeFace.WEST
		EnumFacing.UP -> CubeFace.TOP
		EnumFacing.DOWN -> CubeFace.BOTTOM
	}
}

// This stuff is straight from some of my old school notes. Please help me translate that to minecraft 1.12.2 code later.
// I have no idea if BlockPos works the same way as this does.

data class IOVec(val x: Float, val y: Float, val z: Float) {
	constructor(pos: BlockPos) : this(pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat())

	operator fun plus(other: IOVec): IOVec {
		return IOVec(x + other.x, y + other.y, z + other.z)
	}

	operator fun minus(other: IOVec): IOVec {
		return IOVec(x - other.x, y - other.y, z - other.z)
	}

	operator fun times(scalar: Float): IOVec {
		return IOVec(x * scalar, y * scalar, z * scalar)
	}

	fun normalize(): IOVec {
		val length = sqrt(x * x + y * y + z * z)
		return if(length > 0)
			IOVec(x / length, y / length, z / length)
		else
			this
	}

	fun cross(other: IOVec): IOVec {
		return IOVec(
			y * other.z - z * other.y,
			z * other.x - x * other.z,
			x * other.y - y * other.x
		)
	}

	fun invert(): IOVec {
		return IOVec(-x, -y, -z)
	}
}

data class Matrix4(val m: FloatArray) {
	companion object {
		fun identity(): Matrix4 {
			return Matrix4(
				floatArrayOf(
					1f, 0f, 0f, 0f,
					0f, 1f, 0f, 0f,
					0f, 0f, 1f, 0f,
					0f, 0f, 0f, 1f
				)
			)
		}

		fun translation(x: Float, y: Float, z: Float) = Matrix4(
			floatArrayOf(
				1f, 0f, 0f, x,
				0f, 1f, 0f, y,
				0f, 0f, 1f, z,
				0f, 0f, 0f, 1f
			)
		)

		fun lookAt(forward: IOVec, up: IOVec): Matrix4 {
			val f = forward.normalize()
			val u = up.normalize()
			val r = f.cross(u).normalize()
			val correctedUp = r.cross(f).normalize()

			return Matrix4(
				floatArrayOf(
					r.x, correctedUp.x, -f.x, 0f,
					r.y, correctedUp.y, -f.y, 0f,
					r.z, correctedUp.z, -f.z, 0f,
					0f, 0f, 0f, 1f
				)
			)
		}
	}

	operator fun times(other: Matrix4): Matrix4 {
		val result = FloatArray(16)
		for(i in 0..3) {
			for(j in 0..3) {
				result[i * 4 + j] = 0f
				for(k in 0..3) {
					result[i * 4 + j] += this.m[i * 4 + k] * other.m[k * 4 + j]
				}
			}
		}
		return Matrix4(result)
	}
}

enum class CubeFace(val normal: IOVec, val center: IOVec, val upVector: IOVec) {
	NORTH(IOVec(0f, 0f, -1f), IOVec(0f, 0f, -0.5f), IOVec(0f, 1f, 0f)),
	SOUTH(IOVec(0f, 0f, 1f), IOVec(0f, 0f, 0.5f), IOVec(0f, 1f, 0f)),
	EAST(IOVec(1f, 0f, 0f), IOVec(0.5f, 0f, 0f), IOVec(0f, 1f, 0f)),
	WEST(IOVec(-1f, 0f, 0f), IOVec(-0.5f, 0f, 0f), IOVec(0f, 1f, 0f)),
	TOP(IOVec(0f, 1f, 0f), IOVec(0f, 0.5f, 0f), IOVec(0f, 0f, -1f)),
	BOTTOM(IOVec(0f, -1f, 0f), IOVec(0f, -0.5f, 0f), IOVec(0f, 0f, 1f))
}

object CubeSquareProjector {
	// Define your square vertices (assuming centered at origin, facing +Z)
	private val baseSquareVertices = floatArrayOf(
		-0.3f, -0.3f, 0.01f,  // Bottom-left
		0.3f, -0.3f, 0.01f,  // Bottom-right
		0.3f, 0.3f, 0.01f,  // Top-right
		-0.3f, 0.3f, 0.01f   // Top-left
	)

	private val squareIndices = intArrayOf(
		0, 1, 2,
		2, 3, 0
	)

	fun getTransformationMatrix(face: CubeFace): Matrix4 {
		// Create translation to move square to face center
		val translation = Matrix4.translation(face.center.x, face.center.y, face.center.z)

		// Create rotation to align square with face orientation
		// The square should face outward from the cube face
		val rotation = Matrix4.lookAt(face.normal, face.upVector)

		return translation * rotation
	}

	fun getProjectedSquareVertices(face: CubeFace): FloatArray {
		val transform = getTransformationMatrix(face)
		val projectedVertices = FloatArray(baseSquareVertices.size)

		// Transform each vertex
		for(i in baseSquareVertices.indices step 3) {
			val vertex = transformVertex(
				baseSquareVertices[i],
				baseSquareVertices[i + 1],
				baseSquareVertices[i + 2],
				transform
			)
			projectedVertices[i] = vertex.x
			projectedVertices[i + 1] = vertex.y
			projectedVertices[i + 2] = vertex.z
		}

		return projectedVertices
	}

	private fun transformVertex(x: Float, y: Float, z: Float, matrix: Matrix4): IOVec {
		val m = matrix.m
		return IOVec(
			m[0] * x + m[1] * y + m[2] * z + m[3],
			m[4] * x + m[5] * y + m[6] * z + m[7],
			m[8] * x + m[9] * y + m[10] * z + m[11]
		)
	}

	fun renderSquareOnFace(face: CubeFace) {
		val vertices = getProjectedSquareVertices(face)
		RenderUtils.BUFFER_BUILDER.begin(7, DefaultVertexFormats.POSITION)
		RenderUtils.BUFFER_BUILDER.reverseVertexData(vertices)
		RenderUtils.TESSELLATOR.draw()
	}
}

fun BufferBuilder.addVertexData(vertices: FloatArray) {
	for(i in vertices.indices step 3) {
		this.pos(vertices[i].toDouble(), vertices[i + 1].toDouble(), vertices[i + 2].toDouble()).endVertex()
	}
}

fun BufferBuilder.reverseVertexData(vertices: FloatArray) {
	for(i in vertices.size - 3 downTo 0 step 3) {
		this.pos(vertices[i].toDouble(), vertices[i + 1].toDouble(), vertices[i + 2].toDouble()).endVertex()
	}
}
