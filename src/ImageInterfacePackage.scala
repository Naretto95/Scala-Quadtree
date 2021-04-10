package ImageInterfacePackage

import Array._
import scala.reflect.ClassTag


/* on implémente une structure d'arbre dont chaque noeud possède quatre fils, qui chacun, corresponde à l'arbre d'un des 4 quarts de la matrice du père */
	trait Tree {
		def first_row:Int
		def first_col:Int
		def width:Int
		def height:Int
		override def toString():String
	}

	case class Node(val first_row:Int, val first_col:Int, val width:Int, val height:Int) extends Tree{

		var children:Array[Tree]=Array()

		def setChildren(children:Array[Tree]):Unit={
			this.children = children
		}

		def toStringB(): String = {
			first_row.toString()+","+first_col.toString()+","+width.toString()+","+height.toString()+"\n"
		}

		override def toString():String = {
			val m:Int = children.size;
			m match {
				case 1 => "N("+children(0).toString()+")"
				case 2 => "N("+children(0).toString()+","+children(1).toString()+")"
				case 4 => "N("+children(0).toString()+","+children(1).toString()+","+children(2).toString()+","+children(3).toString()+")"
				case _ => "too much children"   
			}
		}
	}

	/*Une feuille pour les PPM */
	case class Leaf(val first_row:Int, val first_col:Int, val width:Int, val height:Int, val value:Array[Int]) extends Tree{
		override def toString():String={"L("+first_row+","+first_col+","+height+","+width+",["+value(0).toString()+","+value(1).toString()+","+value(2).toString()+"])"}
	}

	/*Une feuille pour les PGM/PBM */
	case class LeafPGMandPBM(val first_row:Int, val first_col:Int, val width:Int, val height:Int, val value:Int) extends Tree{
		override def toString():String={"L("+first_row+","+first_col+","+height+","+width+","+value+")"}
	}

class ImageInterface{

	/*
	La fonction suivante permet de faire une copie profonde sur un tableau à deux dimensions
	et dont les éléments sont des types de bases
	On copie les lignes de i_begin à i_end exclue et les colonnes de j_begin à j_end exclue
	*/
	def deepCopy2D[A:ClassTag](matrix : Array[Array[A]] , i_begin : Int, i_end : Int, j_begin : Int, j_end : Int): Array[Array[A]] = {
		matrix.slice(i_begin,i_end).map(_.slice(j_begin,j_end).clone())
	}

	/*
	Copie une partie de la matrice
	*/
	def submatrix[A:ClassTag](matrix : Array[Array[A]] , i_begin : Int, i_end : Int, j_begin : Int, j_end : Int): Array[Array[A]] = {
		matrix.slice(i_begin,i_end).map(_.slice(j_begin,j_end))
	}

	/*
	A quel point considère-t-on que deux couleurs sont les mêmes sur un codage rgb
	*/
	def almostSameElements(pixel1:Array[Int],pixel2: Array[Int]): Boolean = {
	    val these = pixel1.iterator
	    val those = pixel2.iterator
	    while (these.hasNext && those.hasNext)
	      if ((these.next() - those.next()).abs > 10)
	        return false
	    !these.hasNext && !those.hasNext
	  }

	/*
	Cette fonction permet de vérifier si les éléments d'une matrice sont tous identiques
	*/
	def isSinglePixel(matrix : Array[Array[Array[Int]]],node:Node): Boolean = {
		(node.width>0 && node.height>0) match{
			case false => false
			case _=>{
				val elem = matrix(node.first_row)(node.first_col);
				matrix.slice(node.first_row,node.first_row+node.height)
					.map(_.slice(node.first_col,node.first_col+node.width))
					.flatten.forall(_.corresponds(elem){(a,b)=>{(a-b).abs<50}});
			}
		}
	}

	def isSinglePixelPGMandPBM(matrix : Array[Array[Int]],node:Node): Boolean = {
		(node.width>0 && node.height>0) match{
			case false => false
			case _=>{
				val elem = matrix(node.first_row)(node.first_col);
				var bool = true;
				matrix.slice(node.first_row,node.first_row+node.height)
					.map(_.slice(node.first_col,node.first_col+node.width))
					.flatten.map(e=>bool = bool && ((e-elem).abs<1));
				bool
			}
		}
	}

	/*
	Cette fonction récursive permet de transformer une matrice en arbre selon l'algorithme QuadTree.
	N'ayant pas forcément une matrice dont les dimensions sont des puissance de deux on adopte le découpage précisé ci-dessous.

	La fonction suivante donne à un noeud ses fils et avec chaque fils les coordonnées de la sous-matrice lui correspondant.
	Chaque noeud a deux ou quatre fils ou est transformé en feuille (on renvoie une feuille au lieu du noeud passé en paramètre).
	Le découpage en sous-matrices a lieu tel que suit :

	Si la matrice n'est pas vide et si elle contient au moins deux lignes et au moins deux colonnes,
	alors on peut la casser en quatre sous-matrices de la manière qui suit 
	{
				une matrice pour le coin supérieur gauche
				une matrice pour le coin supérieur droit
				une matrice pour le coin inférieur gauche
				une matrice pour le coin inférieur droit	
	}
	Si la matrice n'a qu'une ligne et plus de deux colonnes ou qu'ne colonne et plus de deux lignes, le noeud en question n'aura que deux fils
	dont le premier sera toujours associé au coin supérieur gauche de la matrice.

	Hint : on précise que si le nombre de lignes est impair, on donne une ligne de plus aux coins supérieurs.
	de même si le nombre de colonnes est impair, on donne la colonne supplémentaire aux coins de gauche.

	On retourne un array contenant les quatre sous-matrices dans l'ordre Haut gauche, Haut droite, Bas droite, Bas gauche.

	*/
	def matrixToTree(matrix : Array[Array[Array[Int]]], node:Node): Tree = {
		if (isSinglePixel(matrix,node)){
			new Leaf(node.first_row,node.first_col,node.width,node.height,matrix(node.first_row)(node.first_col))
		}else{
			val new_width:Int = (node.width/2)+(node.width%2);
			val right_width:Int = node.width-new_width;
			val new_height:Int = (node.height/2)+(node.height%2);
			val down_height: Int = node.height-new_height;
			Array(right_width,down_height) match {
				case Array(0,0) => return new Leaf(node.first_row,node.first_col,1,1,matrix(node.first_row)(node.first_col))
				case Array(_,0) => node.setChildren(Array(matrixToTree(matrix,new Node(node.first_row,node.first_col,new_width,1)),matrixToTree(matrix,new Node(node.first_row,node.first_col+new_width,right_width,1))))
				case Array(0,_) => node.setChildren(Array(matrixToTree(matrix,new Node(node.first_row,node.first_col,1,new_height)),matrixToTree(matrix,new Node(node.first_row+new_height,node.first_col,1,down_height))))
				case _ => node.setChildren(Array(matrixToTree(matrix,new Node(node.first_row,node.first_col,new_width,new_height)),matrixToTree(matrix,new Node(node.first_row,node.first_col+new_width,right_width,new_height)),matrixToTree(matrix,new Node(node.first_row+new_height,node.first_col+new_width,right_width,down_height)),matrixToTree(matrix,new Node(node.first_row+new_height,node.first_col,new_width,down_height))))
			}
			node
		}
	}

	def matrixToTreePGMandPBM(matrix : Array[Array[Int]], node:Node): Tree = {
		if (isSinglePixelPGMandPBM(matrix,node)){
			new LeafPGMandPBM(node.first_row,node.first_col,node.width,node.height,matrix(node.first_row)(node.first_col))
		}else{
			val new_width:Int = (node.width/2)+(node.width%2);
			val right_width:Int = node.width-new_width;
			val new_height:Int = (node.height/2)+(node.height%2);
			val down_height: Int = node.height-new_height;
			Array(right_width,down_height) match {
				case Array(0,0) => return new LeafPGMandPBM(node.first_row,node.first_col,1,1,matrix(node.first_row)(node.first_col))
				case Array(_,0) => node.setChildren(Array(matrixToTreePGMandPBM(matrix,new Node(node.first_row,node.first_col,new_width,1)),matrixToTreePGMandPBM(matrix,new Node(node.first_row,node.first_col+new_width,right_width,1))))
				case Array(0,_) => node.setChildren(Array(matrixToTreePGMandPBM(matrix,new Node(node.first_row,node.first_col,1,new_height)),matrixToTreePGMandPBM(matrix,new Node(node.first_row+new_height,node.first_col,1,down_height))))
				case _ => node.setChildren(Array(matrixToTreePGMandPBM(matrix,new Node(node.first_row,node.first_col,new_width,new_height)),matrixToTreePGMandPBM(matrix,new Node(node.first_row,node.first_col+new_width,right_width,new_height)),matrixToTreePGMandPBM(matrix,new Node(node.first_row+new_height,node.first_col+new_width,right_width,down_height)),matrixToTreePGMandPBM(matrix,new Node(node.first_row+new_height,node.first_col,new_width,down_height))))
			}
			node
		}
	}

	def buildTree(matrix : Array[Array[Array[Int]]]): Tree = {
		matrixToTree(matrix,Node(0,0,matrix(0).size,matrix.size))
	}

	def buildTreePGMandPBM(matrix : Array[Array[Int]]): Tree = {
		matrixToTreePGMandPBM(matrix,Node(0,0,matrix(0).size,matrix.size))
	}

	/*
	Cette fonction transforme l'arbre en matrice inverse
	*/
	def treeToMatrix(matrix:Array[Array[Array[Int]]],tree:Tree):Unit={
		tree match {
			case Leaf(first_row,first_col,width,height,value) => {val count = LazyList.iterate(0)(_ + 1).iterator;matrix.slice(first_row,first_row+height).map(_.slice(first_col,first_col+width).map(e=>e.mapInPlace(_=>value(count.next()%3))))}
			//case Leaf(first_row,first_col,width,height,value) => {for(i <- first_row until first_row+height){for(j <- first_col until first_col+width){for(k <- 0 until 3){matrix(i)(j)(k)=value(k)}}}}
			case Node(first_row,first_col,width,height) => (tree.asInstanceOf[Node]).children.map(child => treeToMatrix(matrix,child))
		}
	}

	def treeToMatrixPGMandPBM(matrix:Array[Array[Int]],tree:Tree):Unit={
		tree match {
			//case LeafPGMandPBM(first_row,first_col,width,height,value) => {matrix.slice(first_row,first_row+height).map(_.slice(first_col,first_col+width).map(_=>value))}
			case LeafPGMandPBM(first_row,first_col,width,height,value) => {for(i <- first_row until first_row+height){for(j <- first_col until first_col+width){matrix(i)(j)=value}}}
			case Node(first_row,first_col,width,height) => (tree.asInstanceOf[Node]).children.map(child => treeToMatrixPGMandPBM(matrix,child))
		}
	}

	def buildMatrix(tree:Tree):Array[Array[Array[Int]]]={
		var matrix: Array[Array[Array[Int]]] = Array.ofDim[Int](tree.height,tree.width,3);
		treeToMatrix(matrix,tree);
		matrix
	}

	def buildMatrixPGMandPBM(tree:Tree):Array[Array[Int]]={
		var matrix: Array[Array[Int]] = Array.ofDim[Int](tree.height,tree.width);
		treeToMatrixPGMandPBM(matrix,tree);
		matrix
	}

	/*
	Méthodes pour inverser les couleurs
	*/
	def inverseColorPPM(matrix:Array[Array[Array[Int]]],depth:Int) = {
	  matrix.map(_.map(_.map(p => depth-p)))
	}


	def inverseColor(matrix:Array[Array[Int]],depth:Int) = {
	  matrix.map(_.map(p => depth-p));
	}

	def mirroirHorizontale[A:ClassTag](matrix:Array[Array[A]]):Array[Array[A]] = {
		for (i <- 0 until matrix.size/2){
		  for (j <- 0 until matrix(i).size){
		      var tmp : A = matrix(i)(j);
		      matrix(i)(j) = matrix(matrix.size-1-i)(j);
		      matrix(matrix.size-1-i)(j) = tmp;
		  }
		}
		matrix
	}
	/* Méthode pour inverser l'image Verticalement */
	def mirroirVerticale[A:ClassTag](matrix:Array[Array[A]]):Array[Array[A]] = {
		for (i <- 0 until matrix.size){
		  for (j <- 0 until matrix(i).size/2){
		      var tmp :A= matrix(i)(j);
		      matrix(i)(j) = matrix(i)((matrix(i).size-1)-j);
		      matrix(i)((matrix(i).size-1)-j) = tmp;
		  }
		}
		matrix
	}

	def rotateDroite[A:ClassTag](matrix:Array[Array[A]]):Array[Array[A]]={
	    var output = ofDim[A](matrix(0).size,matrix.size)
	    for (i<- 0 until matrix.size){
	        for(n<- 0 until matrix(i).size){
	            output(n)(matrix.size-1-i) = matrix(i)(n);
	        }
	    };
	    output
	}

	def rotateGauche[A:ClassTag](matrix:Array[Array[A]]):Array[Array[A]]={
	    var output = ofDim[A](matrix(0).size,matrix.size)
	    for (i<- 0 until matrix.size){
	        for(n<- 0 until matrix(i).size){
	            output(n)(i) = matrix(i)(matrix(0).size-1-n);
	        }
	    };
	    output
	}

	/*
	var reader:ImageReaderWriter = new ImageReaderWriter() /* on crée un lecteur d'image ppm */
	reader.Read("./tempete.ppm") /* on lit l'image */
	//var reverseColorMatrix = inverseColorPPM(reader.pixelsP3,reader.depth)
	//var matrix: Array[Array[Array[Int]]] = Array(Array(Array(1,2,3),Array(4,5,6),Array(7,8,9)),Array(Array(1,2,35),Array(13,14,15),Array(16,17,18)),Array(Array(19,20,21),Array(22,23,24),Array(25,26,27)),Array(Array(28,29,30),Array(31,32,33),Array(34,35,36)),Array(Array(37,38,39),Array(40,41,42),Array(43,44,45)))
	//var tree:Tree=buildTree(reader.pixelsP3) /* on construit l'arbre */
	//var matrixBack = buildMatrix(tree)
	reader.depth
	reader.setPixelsP3(mirroirHorizontalePPM(inverseColorPPM(reader.pixelsP3,reader.depth)))
	reader.Write("retour.ppm")
	*/
}
