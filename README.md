# Scala Quadtree

A small Scala/Java toolkit for lossy image compression with quadtrees, plus a couple
of companion algorithms — k-means color quantization and Huffman coding — applied to
plain-text [Netpbm](https://en.wikipedia.org/wiki/Netpbm) images (`.ppm`, `.pgm`, `.pbm`).

Originally written as a functional programming course project; this repository is a
cleaned-up, restructured version of that work built around a proper sbt layout and an
actual CLI. The original write-up (in French) is kept at [docs/Rapport.pdf](docs/Rapport.pdf).

## Features

- **Quadtree compression** — recursively splits an image into quadrants until each
  region is a uniform color, then rebuilds the image from that tree.
- **K-means color quantization** — reduces an image to *k* dominant colors.
- **Huffman coding** — computes the Huffman-coded bit size of an image based on pixel
  intensity frequency.
- **Color clustering** — a hash-based grouping of pixels into color classes, with a
  greedy least-frequent-first merge step.
- **Image transforms** — invert, flip (horizontal/vertical) and rotate (left/right).
- **Netpbm I/O** — reads and writes P1 (PBM), P2 (PGM) and P3 (PPM) files.

## Project structure

```
build.sbt
src/main/java/quadtree/io/PnmImage.java       Netpbm (P1/P2/P3) reader & writer
src/main/scala/quadtree/
  Main.scala                                  CLI entry point
  tree/Tree.scala, QuadTreeCodec.scala         quadtree data type + build/rebuild
  transform/ImageTransforms.scala              invert, flip, rotate
  kmeans/KMeansQuantizer.scala                 k-means color quantization
  huffman/HuffmanCoding.scala                  bit-level Huffman encoding
  huffman/ColorClustering.scala                hash-based color clustering
samples/                                       sample .ppm/.pgm/.pbm images
docs/Rapport.pdf                               original project report (French)
```

## Getting started

### Prerequisites

- JDK 8 or later
- [sbt](https://www.scala-sbt.org/) (which will fetch the matching Scala 2.13
  compiler automatically)

### Build

```bash
sbt compile
```

### Run

The CLI takes an operation, an input file and an output file:

```bash
sbt "run quadtree samples/tempete.ppm out.ppm"
sbt "run kmeans samples/1331923674.ppm out.ppm 6"
sbt "run invert samples/image2.pgm out.pgm"
sbt "run huffman-size samples/tempete.ppm out.ppm"
```

Available operations:

| Operation          | Description                                             |
|--------------------|----------------------------------------------------------|
| `invert`           | Invert colors                                            |
| `flip-horizontal`  | Mirror left-right                                        |
| `flip-vertical`    | Mirror top-bottom                                        |
| `rotate-left`      | Rotate 90° counter-clockwise                              |
| `rotate-right`     | Rotate 90° clockwise                                      |
| `quadtree`         | Compress to a quadtree and rebuild the image              |
| `kmeans <k>`       | Quantize a PPM image to `k` colors (default 8)             |
| `color-cluster`    | Hash-based color-class round trip (PPM)                    |
| `huffman-size`     | Print the Huffman-coded bit size of a PPM image             |

Run `sbt run` with no arguments to print this list from the CLI itself.

### Use as a library

Each algorithm also lives behind a plain object API you can call directly, e.g.:

```scala
import quadtree.io.PnmImage
import quadtree.tree.QuadTreeCodec

val image = new PnmImage()
image.read("samples/tempete.ppm")

val tree = QuadTreeCodec.buildRgbTree(image.rgbPixels)
image.setRgbPixels(QuadTreeCodec.toRgbMatrix(tree))
image.write("out.ppm")
```

## How the quadtree works

Each node covers a rectangular region of the image. If every pixel in that region is
close enough in color, the node becomes a leaf storing that single color. Otherwise
the region is split into up to four quadrants (top-left, top-right, bottom-right,
bottom-left), and the process repeats recursively. Rebuilding the image just walks the
tree and paints each leaf's color back over its region — regions with more uniform
color compress to shallower subtrees.

## Contributing

Issues and pull requests are welcome — fork the repository and open a PR with your
changes.

## License

MIT — see [LICENSE](LICENSE).
