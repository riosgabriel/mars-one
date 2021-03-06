lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.9"

lazy val root = (project in file(".")).
  enablePlugins(JavaServerAppPackaging).
  settings(
    inThisBuild(List(
      organization    := "com.rios",
      scalaVersion    := "2.12.4"
    )),
    name := "mars-one",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j"           % akkaVersion,

      "ch.qos.logback" % "logback-classic"          % "1.2.3",
      "com.github.fommil" %% "spray-json-shapeless" % "1.4.0",

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test
    )

  )
