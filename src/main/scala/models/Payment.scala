package models

import models.responses.PaymentResponse

import java.time.LocalDateTime
import java.util.UUID

case class Payment(
                    id: UUID,
                    fiatAmount: BigDecimal,
                    fiatCurrency: String,
                    coinAmount: BigDecimal,
                    coinCurrency: String,
                    exchangeRate: BigDecimal,
                    eurExchangeRate: BigDecimal,
                    createdAt: LocalDateTime,
                    expirationTime: LocalDateTime
                  ) {
  def toPaymentResponse: PaymentResponse = responses.PaymentResponse(
    id,
    fiatAmount,
    fiatCurrency,
    coinAmount,
    coinCurrency,
    exchangeRate,
    eurExchangeRate,
    createdAt,
    expirationTime
  )
}
