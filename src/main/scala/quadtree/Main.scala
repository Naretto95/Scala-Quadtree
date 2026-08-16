package quadtree

import quadtree.huffman.{ColorClustering, HuffmanCoding}
import quadtree.io.PnmImage
import quadtree.kmeans.KMeansQuantizer
import quadtree.transform.ImageTransforms
import quadtree.tree.QuadTreeCodec

/**
 * Command-line demo for the image operations in this project.
 *
 * Usage: sbt "run <operation> <input-file> <output-file> [args...]"
 */
object Main {

  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      printUsage()
      return
    }

    val operation = args(0)
    val inputPath = args(1)
    val outputPath = args(2)

    val image = new PnmImage()
    image.read(inputPath)
    val isRgb = image.rgbPixels != null

    operation match {
      case "invert" =>
        if (isRgb) image.setRgbPixels(ImageTransforms.invertRgb(image.rgbPixels, image.depth))
        else image.setGrayscalePixels(ImageTransforms.invertGray(image.grayscalePixels, image.depth))

      case "flip-horizontal" =>
        if (isRgb) image.setRgbPixels(ImageTransforms.flipHorizontal(image.rgbPixels))
        else image.setGrayscalePixels(ImageTransforms.flipHorizontal(image.grayscalePixels))

      case "flip-vertical" =>
        if (isRgb) image.setRgbPixels(ImageTransforms.flipVertical(image.rgbPixels))
        else image.setGrayscalePixels(ImageTransforms.flipVertical(image.grayscalePixels))

      case "rotate-left" =>
        if (isRgb) image.setRgbPixels(ImageTransforms.rotateLeft(image.rgbPixels))
        else image.setGrayscalePixels(ImageTransforms.rotateLeft(image.grayscalePixels))

      case "rotate-right" =>
        if (isRgb) image.setRgbPixels(ImageTransforms.rotateRight(image.rgbPixels))
        else image.setGrayscalePixels(ImageTransforms.rotateRight(image.grayscalePixels))

      case "quadtree" =>
        if (isRgb) image.setRgbPixels(QuadTreeCodec.toRgbMatrix(QuadTreeCodec.buildRgbTree(image.rgbPixels)))
        else image.setGrayscalePixels(QuadTreeCodec.toGrayMatrix(QuadTreeCodec.buildGrayTree(image.grayscalePixels)))

      case "kmeans" =>
        val colorCount = if (args.length > 3) args(3).toInt else 8
        image.setRgbPixels(KMeansQuantizer.quantize(image.rgbPixels, colorCount))

      case "color-cluster" =>
        image.setRgbPixels(ColorClustering.reduce(image.rgbPixels))

      case "huffman-size" =>
        val bits = HuffmanCoding.encode(image.rgbPixels, image.depth)
        println(s"Huffman-coded size: ${bits.length} bits (${bits.length / 8} bytes)")
        return

      case other =>
        println(s"Unknown operation \"$other\"")
        printUsage()
        return
    }

    image.write(outputPath)
    println(s"Wrote $outputPath")
  }

  private def printUsage(): Unit =
    println(
      """Usage: run <operation> <input-file> <output-file> [args...]
        |
        |Operations:
        |  invert                 invert colors
        |  flip-horizontal        mirror left-right
        |  flip-vertical          mirror top-bottom
        |  rotate-left            rotate 90 degrees counter-clockwise
        |  rotate-right           rotate 90 degrees clockwise
        |  quadtree               compress to a quadtree and rebuild the image
        |  kmeans <colorCount>    quantize a PPM image (default 8 colors)
        |  color-cluster          hash-based color-class round trip (PPM)
        |  huffman-size           print the Huffman-coded bit size (PPM)
        |""".stripMargin)
}
