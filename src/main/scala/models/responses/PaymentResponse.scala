package models.responses

import helpers._
import spray.json.DefaultJsonProtocol

import java.time.LocalDateTime
import java.util.UUID

case class PaymentResponse(
                            id: UUID,
                            fiatAmount: BigDecimal,
                            fiatCurrency: String,
                            coinAmount: BigDecimal,
                            coinCurrency: String,
                            exchangeRate: BigDecimal,
                            eurExchangeRate: BigDecimal,
                            createdAt: LocalDateTime,
                            expirationTime: LocalDateTime
                          )

object PaymentResponse extends DefaultJsonProtocol
  with UuidMarshalling
  with LocalDateTimeMarshalling {

  implicit val paymentResponseFormat = jsonFormat9(PaymentResponse.apply)

}