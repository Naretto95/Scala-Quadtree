name := "scala-quadtree"
organization := "io.github.naretto95"
version := "1.0.0"

scalaVersion := "2.13.14"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked"
)

Compile / mainClass := Some("quadtree.Main")
