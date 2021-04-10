package ClassificationHuffmanPackage

import Array._
import scala.collection.mutable.HashMap

class ClassificationHuffman{

    def hashage(pixel : Array[Int]):Int = {
        pixel(0)*1000000 + pixel(1)*100 + pixel(2)   
    }

    def computeFrequency(matriceClassifie:Array[Array[Array[Int]]],matrix:Array[Array[Array[Int]]]): HashMap[Int,Int] = {
        var tabCategory = new HashMap[Int,Int]()
        for (a <- 0 until matrix.size){
            for (b <- 0 until matrix(0).size){
                matrix(a)(b) match{ 
                    case p => {
                        matriceClassifie(a)(b) = Array(p(0),p(1),p(2),hashage(p))
                        if(tabCategory.contains(hashage(p))){
                            tabCategory(hashage(p))+=1
                        }else{
                            tabCategory(hashage(p))=1
                        }
                    }
                }
            }
        }
        // HashMap(tabCategory.toSeq.sortBy(_._2):_*)
        tabCategory
    }

     def showHashMap(hm:HashMap[Int,Int]) = {
         var acc=0
         hm.keys.foreach{
             i => acc+=1 ;i match{ 
                    case p => {
                        if (hm.contains(p) ){
                        // if (p!=null && hm(p)>1){                        
                            print("Key = "+ p)
                            println(" & Value = ", hm(p))
                        }
                                               
                    }
                }
         }
         print(acc)
    }

    def updateMatriceClassifie(new_pixel:(Int,Int),old_pixel:(Int,Int),matriceClassifie:Array[Array[Array[Int]]]): Array[Array[Array[Int]]] = {
        for (a <- 0 until matriceClassifie.size){
            for (b <- 0 until matriceClassifie(0).size){
                if (matriceClassifie(a)(b)(3) == old_pixel._1){
                    matriceClassifie(a)(b)(3) = new_pixel._1
                }
            }
        }  
        matriceClassifie 
    }

    def merge2LowValue(hm:HashMap[Int,Int],matriceClassifie:Array[Array[Array[Int]]]) = {
        var tmp = hm.iterator.min(Ordering.by[(Int,Int),Int](_._2))
        hm.remove(tmp._1)
        var tmp2 = hm.iterator.min(Ordering.by[(Int,Int),Int](_._2))
        updateMatriceClassifie(tmp2,tmp,matriceClassifie)
        var tabb = Array(tmp2._1,tmp2._2)
        tabb(1) +=  tmp._2
        hm.put(tabb(0),tabb(1))
        // (hm.iterator.min(Ordering.by[(Int,Int),Int](_._2)))._2 += tmp._2 
    }

    def decodage(key: Int):Array[Int] = {
      var tab = new Array[Int](3);
      var tmp = 4;
      tab.map(x => {tmp -=1;((key%(scala.math.pow(10,(tmp)*3).toInt))/scala.math.pow(10,(tmp-1)*3).toInt).toInt})
    }

    def classifie(matriceClassifiee:Array[Array[Array[Int]]]):Array[Array[Array[Int]]] = {
        matriceClassifiee.map(_.mapInPlace{ x=>decodage(x(3))});
    }

    def huffman(matrix:Array[Array[Array[Int]]]): Array[Array[Array[Int]]] = {
        var matriceC = Array.ofDim[Array[Int]](matrix.size,matrix(0).size)
        var hashM = new HashMap[Int,Int]()
        hashM = computeFrequency(matriceC,matrix)
        classifie(matriceC);
    }
}