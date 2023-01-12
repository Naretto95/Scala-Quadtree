# Scala-Quadtree

This repository contains the code for a Scala Quadtree project. 

## Getting Started

Before you can use the package in the Scala file, you will need to compile the Java package. To do this, navigate to the project directory and run the following command:
```
javac -classpath . -d . ImageReaderWriter.java
```
You will also need to compile the Scala packages with:
```
scalac -deprecation -classpath . -d . ImageInterfacePackage.scala ClassificationKmeansPackage.scala CodageHuffmanPackage.scala ClassificationHuffmanPackage.scala
```
Once the packages are compiled, you can import them at the top of your script file (monscript.scala) with:
```
import nomDuPackage._
```
To run the project, start the Scala console with:
```
scala -d .
```
Then, load your script file:
```
:load monscript.scala
```

## Built With

* [Scala](https://www.scala-lang.org/) - Programming language
* [Java](https://www.java.com/) - Programming language

## Contributing

If you would like to contribute to the project, please fork the repository and create a pull request with your changes.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgments

* Inspiration
* etc
