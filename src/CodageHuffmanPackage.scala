package CodageHuffmanPackage

import Array._
import scala.collection.mutable.PriorityQueue

class CodageHuffman{
	trait HuffManTree{
		val frequency:Int
		var code: Array[Boolean] = Array[Boolean]()
		override def toString(): String = {frequency.toString()}
	}

	case class HuffManNode(val frequency:Int, val leftSon: HuffManTree,val rightSon:HuffManTree) extends HuffManTree{
	}

	case class HuffManLeaf(val value: Int,val frequency:Int) extends HuffManTree{
		override def toString(): String = {"("+value+","+frequency+")"}
	}

	def func(tree:HuffManTree): Int = {tree.frequency}

	def buidTree(queue:PriorityQueue[HuffManTree]): HuffManTree = {
		queue.size match {
			case 1 => queue.dequeue()
			case _ => {
				val rightSon:HuffManTree=queue.dequeue();
				val leftSon:HuffManTree=queue.dequeue();
				queue+=new HuffManNode(leftSon.frequency+rightSon.frequency,leftSon,rightSon);
				buidTree(queue)
			}
		}
	}

	def fillCodes(queue:PriorityQueue[(Int,Int)],codes:Array[Array[Boolean]]):Array[Array[Boolean]]={
		queue.size match {
			case 0 => codes
			case _ => {
				val e = queue.dequeue();
				fillCodes(queue,codes.zipWithIndex.map(x=>{if(x._2==e._1 || queue.exists(p=>{p._1==x._2})){{e._1==x._2}+:x._1}else{x._1}}))
			}
		}
	}

	def getCodeFromTree(tree:HuffManTree,codes:Array[Array[Boolean]]): Unit = {
		tree match {
			case l:HuffManLeaf =>  codes(l.value)=l.code
			case n:HuffManNode =>{
				n.leftSon.code = n.code:+false;
				n.rightSon.code = n.code:+true;
				getCodeFromTree(n.leftSon,codes);
				getCodeFromTree(n.rightSon,codes)
			} 
		}
	}

	def encode(matrix:Array[Array[Array[Int]]],codes:Array[Array[Boolean]]): Array[Boolean] = {
		var maxs:Int=0;
		codes.foreach(x=>{maxs = maxs.max(x.size)});
		var imageCode = new Array[Boolean](matrix.size*matrix(0).size*maxs*3);
		var acc:Int=0;
		matrix.flatten.flatten.foreach(x=>{
			val l=codes(x).size;
			for(i<- acc until acc+l){imageCode(i)=codes(x)(i-acc)};
			acc+=l
		});
		imageCode
	}


	def huffmanEncode(matrix:Array[Array[Array[Int]]],depth:Int): Array[Boolean] = {
		var frequences = Array.ofDim[Int](depth+1);
		matrix.flatten.flatten.foreach(frequences(_)+=1);
		var queue:PriorityQueue[HuffManTree] = new PriorityQueue[HuffManTree]()(Ordering.by(func).reverse)
		frequences.zipWithIndex.foreach(x=>queue+=(new HuffManLeaf(x._2,x._1)));
		val tree= buidTree(queue);
		var codes: Array[Array[Boolean]] = Array.ofDim[Array[Boolean]](depth+1);
		codes.mapInPlace{_=>Array[Boolean]()};
		getCodeFromTree(tree,codes);
		encode(matrix,codes)
	}

}
/*

var reader:ImageReaderWriter = new ImageReaderWriter() /* on crée un lecteur d'image ppm */
reader.Read("./1331923674.ppm") /* on lit l'image */
huffman(reader.pixelsP3,reader.depth)
*/
/*
var matrix: Array[Array[Array[Int]]] = Array(Array(Array(1,2,3),Array(4,5,6),Array(7,8,9)),Array(Array(1,2,3),Array(1,5,6),Array(5,7,9)),Array(Array(8,7,4),Array(5,4,8),Array(5,4,8)))
var codes = huffman(matrix,9);
*/