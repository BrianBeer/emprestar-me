val scala3Version = "3.0.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala3-simple",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    libraryDependencies += "com.github.losizm" %% "scamper" % "23.0.1",
    libraryDependencies += "com.github.losizm" %% "little-json" % "7.2.0"
  )
