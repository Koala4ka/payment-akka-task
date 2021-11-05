package helpers

import spray.json.{JsString, JsValue, JsonFormat}

import java.util.UUID

trait UuidMarshalling {

  implicit object UuidJsonFormat extends JsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString())

    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => throw new RuntimeException("Expected UUID as JsString, but got " + x)
    }
  }
}
