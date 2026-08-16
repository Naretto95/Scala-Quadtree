package quadtree.kmeans

/**
 * A minimal Manhattan-distance k-means color quantizer for RGB images.
 * Each pixel is labeled with its nearest centroid, then centroids are recomputed as
 * the average color of their members; this repeats until convergence or a fixed
 * iteration cap is reached.
 */
object KMeansQuantizer {

  private val MaxIterations = 100

  private def distance(a: Array[Int], b: Array[Int]): Int =
    a.zip(b).map { case (x, y) => (x - y).abs }.sum

  /** Labels every pixel (channel index 3) with the index of its nearest centroid. */
  private def assignClusters(labeled: Array[Array[Array[Int]]], centroids: Array[Array[Int]]): Unit =
    for (row <- labeled; pixel <- row) {
      val rgb = pixel.take(3)
      var closest = 0
      var minDistance = distance(rgb, centroids(0))
      for (c <- 1 until centroids.length) {
        val d = distance(rgb, centroids(c))
        if (d < minDistance) {
          minDistance = d
          closest = c
        }
      }
      pixel(3) = closest
    }

  /** Averages the RGB of the pixels currently assigned to each centroid (0 for empty clusters). */
  private def averageCentroids(labeled: Array[Array[Array[Int]]], centroidCount: Int): Array[Array[Int]] = {
    val sums = Array.ofDim[Int](centroidCount, 3)
    val counts = Array.ofDim[Int](centroidCount)
    for (row <- labeled; pixel <- row) {
      val cluster = pixel(3)
      counts(cluster) += 1
      for (k <- 0 until 3) sums(cluster)(k) += pixel(k)
    }
    for (c <- sums.indices; k <- 0 until 3) {
      if (counts(c) > 0) sums(c)(k) /= counts(c)
    }
    sums
  }

  /** Replaces each pixel's cluster label with its centroid's color. */
  private def toRgb(labeled: Array[Array[Array[Int]]], centroids: Array[Array[Int]]): Array[Array[Array[Int]]] =
    labeled.map(_.map(pixel => centroids(pixel(3))))

  /** Quantizes `matrix` down to `colorCount` colors using k-means clustering. */
  def quantize(matrix: Array[Array[Array[Int]]], colorCount: Int): Array[Array[Array[Int]]] = {
    val labeled = matrix.map(_.map(p => Array(p(0), p(1), p(2), 0)))
    val columns = matrix(0).length

    // Seed centroids by sampling every 100th pixel in row-major order.
    var centroids = Array.tabulate(colorCount) { i =>
      val index = i * 100 + 99
      val p = matrix(index / columns)(index % columns)
      Array(p(0), p(1), p(2))
    }

    var iterations = MaxIterations
    var converged = false
    while ({ iterations -= 1; iterations > 0 && !converged }) {
      assignClusters(labeled, centroids)
      val newCentroids = averageCentroids(labeled, colorCount)
      converged = centroids.corresponds(newCentroids)((a, b) => a.sameElements(b))
      if (!converged) centroids = newCentroids
    }

    assignClusters(labeled, centroids)
    toRgb(labeled, centroids)
  }
}
