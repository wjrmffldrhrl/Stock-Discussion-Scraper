name := "scala"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "2.2.0"
libraryDependencies += "com.lihaoyi" %% "requests" % "0.6.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test



// https://mvnrepository.com/artifact/com.google.api-client/google-api-client
libraryDependencies += "com.google.api-client" % "google-api-client" % "1.31.2"

// https://mvnrepository.com/artifact/com.google.oauth-client/google-oauth-client
libraryDependencies += "com.google.oauth-client" % "google-oauth-client" % "1.31.4"

// https://mvnrepository.com/artifact/com.google.apis/google-api-services-drive
libraryDependencies += "com.google.apis" % "google-api-services-drive" % "v3-rev197-1.25.0"
