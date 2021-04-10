package ClassificationKmeansPackage

import Array._
import scala.reflect.ClassTag


class ClassificationKmeans{

	def dist(pixel1:Array[Int],pixel2 : Array[Int]):Int={
		pixel1.zip(pixel2).map{ case (x,y) => (x - y).abs  }.sum
	}

	/* cette fonction ré-affecte chaque pixel à sa bonne classe en regardant sa distance aux barycentres */
	def update_matrix(matrix:Array[Array[Array[Int]]],barycentres:Array[Array[Int]]): Unit = {
		var n = barycentres.size;
		matrix.map{
			_.map{
				pixel=>{
					var rgb = pixel.slice(0,3);
					var min : Int = dist(rgb,barycentres(0));
					var i_min : Int = 0; 
					barycentres.slice(1,n).zipWithIndex.foreach{
						b =>{
							var n_m : Int= dist(rgb,b._1);
							if (n_m < min){
								i_min = b._2+1;
								min = n_m;
							}
						}
					};
					pixel(3) = i_min;
					pixel
				}
			}
		}
	}


	/* cette fonction met à jour les barycentres */
	def update_barycentres(matriceClassifiee:Array[Array[Array[Int]]],barycentres:Array[Array[Int]]):Unit={
		var sizes = Array.ofDim[Int](barycentres.size).mapInPlace(_=>0);
		barycentres.map(_.mapInPlace(_=>0));
		matriceClassifiee.map(_.foreach(p=>{sizes(p(3))+=1;barycentres(p(3)) = p.slice(0,3).zip(barycentres(p(3))).map{case (x,y)=>{x+y}}}));
		sizes.zip(barycentres).foreach(c => {(c._2).mapInPlace(x=>{if(c._1>0){x/c._1}else{x}})})
	}

	def classifie(matriceClassifiee:Array[Array[Array[Int]]],classes:Array[Array[Int]]):Array[Array[Array[Int]]] = {
		matriceClassifiee.map(_.mapInPlace{ x=>classes(x(3))});
		matriceClassifiee.map(_.map(_.slice(0,3)))
	}

	/* l'algorithme des k-means */
	def kmeans(matrix:Array[Array[Array[Int]]], nb_classes:Int):Array[Array[Array[Int]]] = {
		var matriceClassifiee = matrix.map(_.map(_.clone()));
		matriceClassifiee.mapInPlace(_.map(p=>{Array(p(0),p(1),p(2),0)}));
		var m = matrix.size;
		var n = matrix(0).size;
		var c = 100;
		var stop : Int = 0
		var classes : Array[Array[Int]] = Array.ofDim[Int](nb_classes,3);
		var acc = -1;
		classes.mapInPlace(_=>{acc+=100;Array(matrix(acc/n)(acc%n)(0),matrix(acc/n)(acc%n)(1),matrix(acc/n)(acc%n)(2))})
		var new_classes : Array[Array[Int]] = Array.ofDim[Int](nb_classes,3);
		while({c-=1;c>0 && stop==0}){
			update_matrix(matriceClassifiee,classes);
			update_barycentres(matriceClassifiee,new_classes);
			stop = {
				if(classes.corresponds(new_classes){case (a,b) => a.corresponds(b){case (x,y) => x == y}}){
					1
				}else{
					classes = new_classes.clone();
					0
				}
			}
		}
		update_matrix(matriceClassifiee,classes);
		classifie(matriceClassifiee,classes);
	}




	/*
	var reader:ImageReaderWriter = new ImageReaderWriter() /* on crée un lecteur d'image ppm */
	reader.Read("./1331923674.ppm") /* on lit l'image */
	reader.setPixelsP3(kmeans(reader.pixelsP3,5));
	reader.Write("retour5e.ppm")
	*/


	/*
	var matriceClassifiee = Array(Array(Array(151,136,495,0),Array(152,136,495,0),Array(98,108,105,0),Array(154,136,495,0)),Array(Array(155,136,495,0),Array(156,136,495,0),Array(157,136,495,0),Array(158,136,495,0)),Array(Array(152,136,495,0),Array(152,136,1000,0),Array(152,136,495,0),Array(152,136,495,0)))
	var barycentres = Array(Array(156,254,21),Array(23,178,94),Array(86,54,147),Array(18,235,173),Array(99,99,99),Array(112,126,196),Array(183,64,52),Array(151,485,495),Array(215,35,139))
	update_matrix(matriceClassifiee,barycentres)
	matriceClassifiee
	*/
}
