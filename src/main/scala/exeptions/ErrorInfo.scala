package exeptions

import spray.json.DefaultJsonProtocol

case class ErrorInfo(error: String)

object ErrorInfo extends DefaultJsonProtocol {

  implicit val errorInfoFormat = jsonFormat1(ErrorInfo.apply)

}
