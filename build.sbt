
lazy val scalaVer = "2.12.2"

name := """sjs-play-routes-demo"""

organization := "ch.insign"

version := "1.0-SNAPSHOT"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "ch.insign.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "ch.insign.binders._"

lazy val server = (project in file("server"))
  .settings(
    scalaVersion := scalaVer,
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    libraryDependencies ++= Seq(
      "com.vmunier"       %% "scalajs-scripts"      % "1.1.1",
      "io.circe"          %% "circe-core"           % "0.8.0",
      "io.circe"          %% "circe-generic"        % "0.8.0",
      "io.circe"          %% "circe-parser"         % "0.8.0",
      "org.webjars"       %% "webjars-play"         % "2.6.0",
      "org.webjars.bower" %  "material-design-lite" % "1.3.0",
      "org.typelevel"     %% "cats-core"            % "1.0.0-MF",
      guice
    )
  )
  .enablePlugins(PlayScala)
  .dependsOn(sharedJvm)

lazy val client = (project in file("client"))
  .settings(
    scalaVersion := scalaVer,
    scalajsPlayRoutesFile := "server/conf/routes",
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js"  %%% "scalajs-dom"   % "0.9.1",
      "io.circe"      %%% "circe-core"    % "0.8.0",
      "io.circe"      %%% "circe-generic" % "0.8.0",
      "io.circe"      %%% "circe-parser"  % "0.8.0",
      "com.lihaoyi"   %%% "scalatags"     % "0.6.5",
      "io.suzaku"     %%% "diode"         % "1.1.2"
    )
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb, ScalajsPlayRoutes)
  .dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(
    scalaVersion := scalaVer
  )
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
