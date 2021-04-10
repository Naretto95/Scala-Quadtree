import Array._
import scala.collection.mutable.HashMap

import ImageReaderWriterPackage._
import ClassificationKmeansPackage._
import ImageInterfacePackage._
import ClassificationHuffmanPackage._
import CodageHuffmanPackage._

var reader:ImageReaderWriter = new ImageReaderWriter() /* on crée un lecteur d'image ppm */

/* ----------------------------------------------------tests pour le k-means ------------------------------------------------*/
/*
var ck = new ClassificationKmeans()
reader.Read("./1331923674.ppm") /* on lit l'image */
reader.setPixelsP3(ck.kmeans(reader.pixelsP3,5));
reader.Write("retourClassificationKmeans5couleurs20iterations.ppm") /* on écrit la sortie */ 
*/


/*--------------------------------------- tests pour la classification inspirée de Huffman --------------------------------------*/
/*
var cch = new ClassificationHuffman()
reader.Read("./1331923674.ppm") /* on lit l'image */
reader.setPixelsP3(cch.huffman(reader.pixelsP3));
reader.Write("retourClassificationHuffman.ppm") /* on écrit la sortie */ 
*/



/*----------------------------------------------tests pour le codage de Huffman ---------------------------------------------------*/
/*
var cdh = new CodageHuffman()
reader.Read("./1331923674.ppm") /* on lit l'image */
cdh.huffmanEncode(reader.pixelsP3,reader.depth)
*/


/* --------------------------------------------tests du package ImageInterfacePackage ------------------------------------------------ */
var ii = new ImageInterface()

/* test de l'inversion de couleur */
/*
reader.Read("tempete.ppm")
reader.setPixelsP3(ii.inverseColorPPM(reader.pixelsP3,reader.depth))
reader.Write("retourInversementDeCouleurs.ppm")
*/

/* test de l'inversion de couleur pour PGM ou PBM */
/*
reader.Read("image2.pgm")
reader.setPixelsP1P2(ii.inverseColor(reader.pixelsP1P2,reader.depth))
reader.Write("retourInversementDeCouleursPGMouPBM.pgm")
*/

/* test de la rotation droite */
/*
reader.Read("tempete.ppm")
reader.setPixelsP3(ii.rotateDroite(reader.pixelsP3))
reader.Write("retourRotationDroite.ppm")
*/

/* test de la rotation gauche */
/*
reader.Read("tempete.ppm")
reader.setPixelsP3(ii.rotateGauche(reader.pixelsP3))
reader.Write("retourRotationGauche.ppm")
*/

/* test pour le mirroir vertical */
/*
reader.Read("tempete.ppm")
reader.setPixelsP3(ii.mirroirVerticale(reader.pixelsP3))
reader.Write("retourMirroirVertical.ppm")
*/

/* test pour le mirroir horizontal */
/*
reader.Read("tempete.ppm")
reader.setPixelsP3(ii.mirroirHorizontale(reader.pixelsP3))
reader.Write("retourMirroirHorizontal.ppm")
*/

/* test du QuadTree */
/*
reader.Read("tempete.ppm")
reader.setPixelsP3(ii.buildMatrix(ii.buildTree(reader.pixelsP3))) /* on construit l'arbre */
reader.Write("retourQuadTree.ppm")
*/

