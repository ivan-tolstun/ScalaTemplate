// The Lagom plugin
addSbtPlugin("com.lightbend.lagom" % "lagom-sbt-plugin" % "1.6.2")
addSbtPlugin("com.lightbend.rp" % "sbt-reactive-app" % "1.7.3")

// Visualize your project's dependencies. (terminal: sbt dependencyBrowseTree)
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")
// Should not enabled by default but can be used to analyze dependency issues
// alternativley hope for sbt-dependency-graph to be made sbt 1.3.x compatible
// addSbtPlugin("io.get-coursier" % "sbt-coursier" % "2.0.0-RC3-5")


//Sonar Scala scoverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")