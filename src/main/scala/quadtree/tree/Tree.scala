package quadtree.tree

/**
 * A quadtree node covering the sub-matrix `[firstRow, firstRow + height)` x
 * `[firstCol, firstCol + width)` of the source image. Each node is either an internal
 * [[Node]] with two or four children (one per quadrant) or a leaf holding a uniform
 * pixel value.
 */
sealed trait Tree {
  def firstRow: Int
  def firstCol: Int
  def width: Int
  def height: Int
}

final case class Node(firstRow: Int, firstCol: Int, width: Int, height: Int, children: Array[Tree]) extends Tree {
  override def toString: String = children.map(_.toString).mkString("N(", ",", ")")
}

/** A uniform-color leaf for RGB (PPM) images. */
final case class RgbLeaf(firstRow: Int, firstCol: Int, width: Int, height: Int, rgb: Array[Int]) extends Tree {
  override def toString: String = s"L($firstRow,$firstCol,$height,$width,[${rgb(0)},${rgb(1)},${rgb(2)}])"
}

/** A uniform-value leaf for grayscale/bitmap (PGM/PBM) images. */
final case class GrayLeaf(firstRow: Int, firstCol: Int, width: Int, height: Int, value: Int) extends Tree {
  override def toString: String = s"L($firstRow,$firstCol,$height,$width,$value)"
}
