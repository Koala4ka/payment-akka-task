package configs

import pureconfig.ConfigSource
import pureconfig.generic.auto._

object ApiConfig {

  val apiConfig: ConfigModel = ConfigSource.default.load[ConfigModel] match {
    case Right(conf) => conf
    case Left(error) => throw new Exception(error.toString())
  }

}
