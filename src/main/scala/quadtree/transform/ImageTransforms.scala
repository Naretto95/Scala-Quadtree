package quadtree.transform

import scala.reflect.ClassTag

/** Whole-image geometric and color transforms, applied directly on the pixel matrix. */
object ImageTransforms {

  def invertRgb(matrix: Array[Array[Array[Int]]], depth: Int): Array[Array[Array[Int]]] =
    matrix.map(_.map(_.map(depth - _)))

  def invertGray(matrix: Array[Array[Int]], depth: Int): Array[Array[Int]] =
    matrix.map(_.map(depth - _))

  /** Mirrors the image left-to-right (reverses column order within each row). */
  def flipHorizontal[A: ClassTag](matrix: Array[Array[A]]): Array[Array[A]] =
    matrix.map(_.reverse)

  /** Mirrors the image top-to-bottom (reverses row order). */
  def flipVertical[A: ClassTag](matrix: Array[Array[A]]): Array[Array[A]] =
    matrix.reverse

  /** Rotates the image 90 degrees clockwise. */
  def rotateRight[A: ClassTag](matrix: Array[Array[A]]): Array[Array[A]] = {
    val rows = matrix.length
    val cols = matrix(0).length
    val output = Array.ofDim[A](cols, rows)
    for (i <- 0 until rows; j <- 0 until cols) {
      output(j)(rows - 1 - i) = matrix(i)(j)
    }
    output
  }

  /** Rotates the image 90 degrees counter-clockwise. */
  def rotateLeft[A: ClassTag](matrix: Array[Array[A]]): Array[Array[A]] = {
    val rows = matrix.length
    val cols = matrix(0).length
    val output = Array.ofDim[A](cols, rows)
    for (i <- 0 until rows; j <- 0 until cols) {
      output(j)(i) = matrix(i)(cols - 1 - j)
    }
    output
  }
}
