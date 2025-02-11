name := "PriceBasket"

version := "0.1"

scalaVersion := "2.12.19"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % Test

assembly / assemblyJarName := "PriceBasket-assembly-0.1.jar"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.36",
  "org.slf4j" % "slf4j-simple" % "1.7.36" // or use logback-classic
)
