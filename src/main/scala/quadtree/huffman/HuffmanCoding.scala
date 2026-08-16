package quadtree.huffman

import scala.collection.mutable.PriorityQueue

/**
 * A textbook Huffman encoder for image pixel intensities: it builds a prefix-free
 * code from the frequency of each intensity value (0..depth) across all channels,
 * then encodes the image as a flat bit sequence.
 */
object HuffmanCoding {

  private sealed trait CodeTree {
    def frequency: Int
    var code: Array[Boolean] = Array.empty
  }

  private case class Branch(frequency: Int, left: CodeTree, right: CodeTree) extends CodeTree
  private case class Leaf(value: Int, frequency: Int) extends CodeTree

  private def buildTree(queue: PriorityQueue[CodeTree]): CodeTree =
    if (queue.size == 1) queue.dequeue()
    else {
      val right = queue.dequeue()
      val left = queue.dequeue()
      queue += Branch(left.frequency + right.frequency, left, right)
      buildTree(queue)
    }

  private def assignCodes(tree: CodeTree, codes: Array[Array[Boolean]]): Unit = tree match {
    case leaf: Leaf => codes(leaf.value) = leaf.code
    case branch: Branch =>
      branch.left.code = branch.code :+ false
      branch.right.code = branch.code :+ true
      assignCodes(branch.left, codes)
      assignCodes(branch.right, codes)
  }

  private def encodeBits(matrix: Array[Array[Array[Int]]], codes: Array[Array[Boolean]]): Array[Boolean] = {
    val maxCodeLength = codes.map(_.length).max
    // Upper-bound allocation: every value could in principle use the longest code.
    val bits = new Array[Boolean](matrix.length * matrix(0).length * maxCodeLength * 3)
    var position = 0
    matrix.flatten.flatten.foreach { value =>
      val code = codes(value)
      code.copyToArray(bits, position)
      position += code.length
    }
    bits
  }

  /** Encodes an RGB image as a flat Huffman-coded bit sequence, given the channel value depth (max intensity). */
  def encode(matrix: Array[Array[Array[Int]]], depth: Int): Array[Boolean] = {
    val frequencies = Array.ofDim[Int](depth + 1)
    matrix.flatten.flatten.foreach(frequencies(_) += 1)

    val queue = new PriorityQueue[CodeTree]()(Ordering.by[CodeTree, Int](_.frequency).reverse)
    frequencies.zipWithIndex.foreach { case (freq, value) => queue += Leaf(value, freq) }

    val tree = buildTree(queue)
    val codes = Array.fill(depth + 1)(Array.empty[Boolean])
    assignCodes(tree, codes)
    encodeBits(matrix, codes)
  }
}
