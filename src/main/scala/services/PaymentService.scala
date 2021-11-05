package services

import cats.data.Validated
import configs.ApiConfig
import exeptions.ErrorInfo
import models.Payment
import models.requests.PaymentRequest
import repositories.{DB, MarketData}

import java.time.LocalDateTime
import java.util.UUID

class PaymentService {

  private val repository = DB
  private val paymentConfig = ApiConfig.apiConfig.api.payments

  def createPayment(paymentRequest: PaymentRequest): Either[ErrorInfo,Payment] = {

    import java.time.Duration._

    val checkCurrency = Validated.cond(
      DB.fiatCurrencies.contains(paymentRequest.fiatCurrency) && DB.cryptoCurrencies.contains(paymentRequest.coinCurrency),
      (),
      ErrorInfo("Not existing currency.")
    )

    val exchangeCurrency = (_: Unit) => {
      val exchangeRate = MarketData.exchangeRatesOfBTC(paymentRequest.fiatCurrency)

      val eurExchangeRate = MarketData.exchangeRatesToEUR(paymentRequest.fiatCurrency)

      val min = paymentConfig.minEurAmount
      val max = paymentConfig.maxEurAmount
      val eurAmount = paymentRequest.fiatAmount / eurExchangeRate

      val test = (min <= eurAmount && eurAmount <= max)

      Validated.cond(
        test,
        {
          val coinAmount = paymentRequest.fiatAmount / exchangeRate

          val now = LocalDateTime.now

          val payment = Payment(
            id = UUID.randomUUID(),
            fiatAmount = paymentRequest.fiatAmount,
            fiatCurrency = paymentRequest.fiatCurrency,
            coinAmount = coinAmount,
            coinCurrency = paymentRequest.coinCurrency,
            exchangeRate = exchangeRate,
            eurExchangeRate = eurExchangeRate,
            createdAt = now,
            expirationTime = now plus ofMillis(paymentConfig.expiration.toMillis)
          )
          repository.payments = payment :: repository.payments

          payment
        },
        ErrorInfo("Min/Max EUR exchange is failed.")
      )
    }
    (checkCurrency andThen exchangeCurrency).toEither
  }


}
