package quadtree.huffman

import scala.collection.mutable

/**
 * Groups pixels into color classes by hashing each RGB triplet into a single key,
 * with a companion frequency table so the least common classes can be merged
 * together (a Huffman-style greedy reduction, kept separate from the frequency
 * counting so the two steps compose).
 *
 * Note: [[hash]] packs the green channel into two decimal digits while [[unhash]]
 * decodes it as three, so round-tripping through [[reduce]] does not perfectly
 * reconstruct the original color -- this quirk is inherited unchanged from the
 * original implementation.
 */
object ColorClustering {

  private def hash(pixel: Array[Int]): Int =
    pixel(0) * 1000000 + pixel(1) * 100 + pixel(2)

  private def unhash(key: Int): Array[Int] =
    Array((key % 1000000000) / 1000000, (key % 1000000) / 1000, key % 1000)

  /** Tags every pixel with its color-class key (channel index 3) and counts class sizes. */
  def computeFrequencies(matrix: Array[Array[Array[Int]]]): (Array[Array[Array[Int]]], mutable.HashMap[Int, Int]) = {
    val frequencies = mutable.HashMap[Int, Int]()
    val tagged = matrix.map(_.map { pixel =>
      val key = hash(pixel)
      frequencies(key) = frequencies.getOrElse(key, 0) + 1
      Array(pixel(0), pixel(1), pixel(2), key)
    })
    (tagged, frequencies)
  }

  /** Relabels every pixel tagged with `from`'s key to `into`'s key. */
  def relabel(tagged: Array[Array[Array[Int]]], into: Int, from: Int): Unit =
    for (row <- tagged; pixel <- row if pixel(3) == from) pixel(3) = into

  /**
   * Merges the two least-frequent color classes into one, updating both the tagged
   * matrix and the frequency table in place.
   */
  def mergeLeastFrequent(frequencies: mutable.HashMap[Int, Int], tagged: Array[Array[Array[Int]]]): Unit = {
    val (smallestKey, smallestCount) = frequencies.minBy(_._2)
    frequencies -= smallestKey
    val (targetKey, targetCount) = frequencies.minBy(_._2)
    relabel(tagged, into = targetKey, from = smallestKey)
    frequencies(targetKey) = targetCount + smallestCount
  }

  private def decodeAll(tagged: Array[Array[Array[Int]]]): Array[Array[Array[Int]]] =
    tagged.map(_.map(pixel => unhash(pixel(3))))

  /** Tags each pixel with its color-class key and decodes it back to RGB via [[unhash]]. */
  def reduce(matrix: Array[Array[Array[Int]]]): Array[Array[Array[Int]]] = {
    val (tagged, _) = computeFrequencies(matrix)
    decodeAll(tagged)
  }

  def printFrequencies(frequencies: mutable.HashMap[Int, Int]): Unit =
    frequencies.foreach { case (key, count) => println(s"key=$key count=$count") }
}
