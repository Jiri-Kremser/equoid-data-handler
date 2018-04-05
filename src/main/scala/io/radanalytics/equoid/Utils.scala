package io.radanalytics.equoid

import scala.util.Properties

object Utils {
  def getProp(snakeCaseName: String, defaultValue: String): String = {
    // return the value of 'SNAKE_CASE_NAME' env variable,
    // if ^ not defined, return the value of 'snakeCaseName' JVM property
    // if ^ not defined, return the defaultValue
    val camelCase = "_(.)".r.replaceAllIn(snakeCaseName.toLowerCase, m => m.group(1).toUpperCase)
    Properties.envOrElse(snakeCaseName, Properties.propOrElse(camelCase, defaultValue))
  }

}
