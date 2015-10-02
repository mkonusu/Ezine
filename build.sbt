name := "ezine"

version := "1.0"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.mongodb" % "mongo-java-driver" % "3.0.4",
  "org.jongo" % "jongo" % "1.2",
  "org.apache.commons" % "commons-email" % "1.3.3",
  "org.apache.commons" % "commons-math3" % "3.0",
  "com.google.code.gson" % "gson" % "2.2.4",
  "com.google.apis" % "google-api-services-youtube" % "v3-rev143-1.20.0",
  "com.google.apis" % "google-api-services-youtubeAnalytics" % "v1-rev24-1.17.0-rc",
  "com.google.http-client" % "google-http-client-jackson2" % "1.18.0-rc",
  "com.google.oauth-client" % "google-oauth-client-jetty" % "1.18.0-rc",
  "com.google.collections" % "google-collections" % "1.0",
  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.4"
)

play.Project.playJavaSettings