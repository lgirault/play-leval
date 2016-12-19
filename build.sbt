val scalaV = "2.11.8"
val akkaVersion = "2.4.11"
val circeVersion = "0.6.1"

def commonSettings = Seq[Setting[_]](
  scalaVersion := scalaV,

  libraryDependencies ++= Seq(
    "io.circe" %%% "circe-core", ///%%% retrieve scala js version !
    "io.circe" %%% "circe-generic",
    "io.circe" %%% "circe-parser"
  ).map(_ % circeVersion)/*,

  addCompilerPlugin(
    "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
  )*/
)


lazy val server = (project in file("server"))
  .settings(commonSettings)
  .settings(
  scalaVersion := scalaV,
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline,

  // scalaz-bintray resolver needed for specs2 library
  //resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  resolvers += "Bintary JCenter" at "http://jcenter.bintray.com",

  libraryDependencies ++= Seq(
    "com.vmunier" %% "scalajs-scripts" % "1.0.0",
    specs2 % Test,

    jdbc,
    cache,
    ws,

    "org.webjars" % "flot" % "0.8.3",
    "org.webjars" % "bootstrap" % "3.3.6",

    "play-circe" %% "play-circe" % "2.5-0.6.0",

    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
  ),
  // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
  EclipseKeys.preTasks := Seq(compile in Compile)
).enablePlugins(PlayScala).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).
  settings(commonSettings).
  settings(
  //scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.1"
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(scalaVersion := scalaV).
  //settings(commonSettings).
  jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
