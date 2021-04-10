Il faut compiler le package java avant de pourvoir l'utiliser dans le ficher scala.

compiler dans le même répertoire que le fichier scala avec :

javac -classpath . -d . ImageReaderWriter.java

compiler également les packages scala avec :

scalac -deprecation -classpath . -d . ImageInterfacePackage.scala ClassificationKmeansPackage.scala CodageHuffmanPackage.scala ClassificationHuffmanPackage.scala

importer les packages en haut du fichier monscript.scala avec :

import nomDuPackage._

puis lancer la console scala avec :

scala -d .

executer le script principal scala dans la console avec :

:load monscript.scala
