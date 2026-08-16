package quadtree.tree

/**
 * Builds and rebuilds a quadtree from/to a 2D pixel matrix.
 *
 * A matrix is recursively split into quadrants until every pixel in a region is
 * (approximately) the same color, at which point the region collapses into a leaf.
 * Odd dimensions are handled by giving the extra row to the top quadrants and the
 * extra column to the left quadrants; a region with a single row or column yields a
 * two-child node instead of four.
 */
object QuadTreeCodec {

  private val RgbTolerance = 50
  private val GrayTolerance = 1

  def buildRgbTree(matrix: Array[Array[Array[Int]]]): Tree =
    buildRgb(matrix, 0, 0, matrix(0).length, matrix.length)

  def buildGrayTree(matrix: Array[Array[Int]]): Tree =
    buildGray(matrix, 0, 0, matrix(0).length, matrix.length)

  def toRgbMatrix(tree: Tree): Array[Array[Array[Int]]] = {
    val matrix = Array.ofDim[Int](tree.height, tree.width, 3)
    fillRgb(matrix, tree)
    matrix
  }

  def toGrayMatrix(tree: Tree): Array[Array[Int]] = {
    val matrix = Array.ofDim[Int](tree.height, tree.width)
    fillGray(matrix, tree)
    matrix
  }

  private def isUniformRgb(matrix: Array[Array[Array[Int]]], row: Int, col: Int, width: Int, height: Int): Boolean =
    if (width <= 0 || height <= 0) false
    else {
      val reference = matrix(row)(col)
      matrix.slice(row, row + height).forall(_.slice(col, col + width).forall { pixel =>
        pixel.zip(reference).forall { case (a, b) => (a - b).abs < RgbTolerance }
      })
    }

  private def isUniformGray(matrix: Array[Array[Int]], row: Int, col: Int, width: Int, height: Int): Boolean =
    if (width <= 0 || height <= 0) false
    else {
      val reference = matrix(row)(col)
      matrix.slice(row, row + height).forall(_.slice(col, col + width).forall(v => (v - reference).abs < GrayTolerance))
    }

  /** Splits a `width` x `height` region into quadrants, giving the extra row/column (if any) to the top/left. */
  private def split(width: Int, height: Int): (Int, Int, Int, Int) = {
    val leftWidth = width / 2 + width % 2
    val rightWidth = width - leftWidth
    val topHeight = height / 2 + height % 2
    val bottomHeight = height - topHeight
    (leftWidth, rightWidth, topHeight, bottomHeight)
  }

  private def buildRgb(matrix: Array[Array[Array[Int]]], row: Int, col: Int, width: Int, height: Int): Tree =
    if (isUniformRgb(matrix, row, col, width, height)) {
      RgbLeaf(row, col, width, height, matrix(row)(col))
    } else {
      val (leftWidth, rightWidth, topHeight, bottomHeight) = split(width, height)
      (rightWidth, bottomHeight) match {
        case (0, 0) =>
          RgbLeaf(row, col, 1, 1, matrix(row)(col))
        case (_, 0) =>
          Node(row, col, width, height, Array(
            buildRgb(matrix, row, col, leftWidth, 1),
            buildRgb(matrix, row, col + leftWidth, rightWidth, 1)))
        case (0, _) =>
          Node(row, col, width, height, Array(
            buildRgb(matrix, row, col, 1, topHeight),
            buildRgb(matrix, row + topHeight, col, 1, bottomHeight)))
        case _ =>
          // Top-left, top-right, bottom-right, bottom-left.
          Node(row, col, width, height, Array(
            buildRgb(matrix, row, col, leftWidth, topHeight),
            buildRgb(matrix, row, col + leftWidth, rightWidth, topHeight),
            buildRgb(matrix, row + topHeight, col + leftWidth, rightWidth, bottomHeight),
            buildRgb(matrix, row + topHeight, col, leftWidth, bottomHeight)))
      }
    }

  private def buildGray(matrix: Array[Array[Int]], row: Int, col: Int, width: Int, height: Int): Tree =
    if (isUniformGray(matrix, row, col, width, height)) {
      GrayLeaf(row, col, width, height, matrix(row)(col))
    } else {
      val (leftWidth, rightWidth, topHeight, bottomHeight) = split(width, height)
      (rightWidth, bottomHeight) match {
        case (0, 0) =>
          GrayLeaf(row, col, 1, 1, matrix(row)(col))
        case (_, 0) =>
          Node(row, col, width, height, Array(
            buildGray(matrix, row, col, leftWidth, 1),
            buildGray(matrix, row, col + leftWidth, rightWidth, 1)))
        case (0, _) =>
          Node(row, col, width, height, Array(
            buildGray(matrix, row, col, 1, topHeight),
            buildGray(matrix, row + topHeight, col, 1, bottomHeight)))
        case _ =>
          Node(row, col, width, height, Array(
            buildGray(matrix, row, col, leftWidth, topHeight),
            buildGray(matrix, row, col + leftWidth, rightWidth, topHeight),
            buildGray(matrix, row + topHeight, col + leftWidth, rightWidth, bottomHeight),
            buildGray(matrix, row + topHeight, col, leftWidth, bottomHeight)))
      }
    }

  private def fillRgb(matrix: Array[Array[Array[Int]]], tree: Tree): Unit = tree match {
    case RgbLeaf(row, col, width, height, value) =>
      for (i <- row until row + height; j <- col until col + width; k <- 0 until 3) {
        matrix(i)(j)(k) = value(k)
      }
    case node: Node =>
      node.children.foreach(fillRgb(matrix, _))
    case _ => ()
  }

  private def fillGray(matrix: Array[Array[Int]], tree: Tree): Unit = tree match {
    case GrayLeaf(row, col, width, height, value) =>
      for (i <- row until row + height; j <- col until col + width) {
        matrix(i)(j) = value
      }
    case node: Node =>
      node.children.foreach(fillGray(matrix, _))
    case _ => ()
  }
}
