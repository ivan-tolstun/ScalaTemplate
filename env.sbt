import scala.util.Try

fork in ThisBuild := true

envVars in ThisBuild := Map.empty[String, String] +
  ("ENV_TOLSTUN_MYSQL_DATABASE_URL" -> "mysql.infrastructure.aifora.net:3306/ADLER_DATA_STAGE?zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=true") +
  ("ENV_TOLSTUN_MYSQL_DATABASE_USER" -> "demo-service") +
  ("ENV_TOLSTUN_MYSQL_DATABASE_PASSWORD" -> "9K;@zMT=u/6?/Yp=") +
  ("ENV_TOLSTUN_KEYCLOAK_PUBLIC_KEY" -> "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg4y8cqHN/O6KOmCcT093jlWwbOo4cpucGJ/Dj+zZMKRnxqChk3Pd70q+4vKNCGyRGRG3S8AMD5izRMwJz0zgFbfrW1IsCkeNwhGW28brem22AsLo3FtaKxKRrr2SJF/1Wc9Zd4vP/FLmshrmnRoWAESym4eJs5vA0q3z6619edN1bgniptNXfYAtLtpePp0EgxUDN+xu1CToWkouG4NKUVDlTxpckZXf2z1Kx3JVK5vEoThWl071jeL0JXbO6fGRxHNvpeYZxKtetFu0tihs7GK3ZMjfuRJL3L2z3KIfzuGAu5lcBDMOOtKMNpDICWZUW+9liNik0yiLc5d7OtXJfwIDAQAB") +
  ("ENV_TOLSTUN_KEYCLOAK_KID" -> "zMWdP3kMJphybfnifFYzT9DWjxmzscB6BbzlcBTjYSo")

val setEnvTask = TaskKey[Unit]("setEnvTask", "sets development environment variables")


setEnvTask in ThisBuild := {
  println("envVars.value: " + envVars.value)

  for {(k, v) <- envVars.value} {
    println(s"k: $k; v: $v")

    Try {
      System.setProperty(k, v)
    }
      .failed
      .foreach { e => println(e.getLocalizedMessage) }
  }
}


runAll := {
  setEnvTask.value
  runAll.value
}