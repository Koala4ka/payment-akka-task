package services

import cats.data.Validated
import configs.ApiConfig
import exeptions.ErrorInfo
import models.Payment
import models.requests.PaymentRequest
import models.responses.{StatsResponse}
import repositories.{DB, MarketData}

import java.time.LocalDateTime
import java.util.UUID

class PaymentService {

  private val repository = DB
  private val paymentConfig = ApiConfig.apiConfig.api.payments

  def createPayment(paymentRequest: PaymentRequest): Either[ErrorInfo, Payment] = {

    import java.time.Duration._

    val checkCurrency = Validated.cond(
      DB.fiatCurrencies.contains(paymentRequest.fiatCurrency) &&
        DB.cryptoCurrencies.contains(paymentRequest.coinCurrency),
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

  def getById(paymentId: String): Either[ErrorInfo, Payment] = {

    val uuid = UUID.fromString(paymentId)

    Validated.cond(
      DB.payments.find(_.id == uuid).isDefined,
      DB.payments.find(_.id == uuid).get,
      ErrorInfo("The payment does not exist."))
      .toEither
  }

  def getAllPaymentsByCurrency(currency: String): Either[ErrorInfo, List[Payment]] = {
    Validated.cond(
      DB.payments.exists(_.fiatCurrency == currency),
      DB.payments.filter(_.fiatCurrency == currency),
      ErrorInfo("The currency does not exist."))
      .toEither
  }

  def getStatisticsByStats(currency: String): Either[ErrorInfo, StatsResponse] = {

    val countAllPayments =
      DB.payments.size.toLong

    val countPerFiatCurrency =
      DB.payments.count(_.fiatCurrency == currency).toLong

    val sumFiatAmount =
      DB.payments.filter(_.fiatCurrency == currency).map(_.fiatAmount).sum

    val sumCryptoAmount = {

      val btc = "BTC"
      DB.payments.filter(_.coinCurrency == btc).map(_.coinAmount).sum
    }

    val eurValueSum = {
      val eur = "EUR"
      DB.payments.filter(_.fiatCurrency == eur).map(_.fiatAmount).sum
    }

    val stats = StatsResponse(
      paymentsCount = countAllPayments,
      paymentsCountPerFiatCurrency = countPerFiatCurrency,
      paymentsSumFiatAmount = sumFiatAmount,
      paymentsSumCryptoAmount = sumCryptoAmount,
      paymentsEURValueSum = eurValueSum)

    Validated
      .cond(
        DB.payments.exists(_.fiatCurrency == currency),
        stats,
        ErrorInfo("There is no any payment")
      ).toEither
  }
}


