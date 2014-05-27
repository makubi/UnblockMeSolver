name := "unblockMeSolver"

version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.1.5" % "test"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.3"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.3.3"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.3"% "test"

mainClass := Some("solver.SampleProblem")

