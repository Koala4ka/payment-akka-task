package controllers

import akka.http.scaladsl.server.Route
import exeptions.ErrorInfo
import models.requests.PaymentRequest
import models.responses.{PaymentResponse, StatsResponse}
import services.PaymentService
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.spray._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.Future

class PaymentController(paymentService: PaymentService) {

  private val createNewPayment: Endpoint[PaymentRequest, ErrorInfo, PaymentResponse, Any] =
    endpoint
      .post.in("payment" / "new").in(jsonBody[PaymentRequest])
      .out(jsonBody[PaymentResponse])
      .errorOut(jsonBody[ErrorInfo])

  val createNewPaymentRoute: Route =
    AkkaHttpServerInterpreter().toRoute(createNewPayment) { paymentRequest =>
      Future
        .successful(
          paymentService
            .createPayment(paymentRequest)
            .map(_.toPaymentResponse)
        )
    }

  private val getByPaymentId: Endpoint[String, ErrorInfo, PaymentResponse, Any] = {
    endpoint.get.in("payment" / path[String]).out(jsonBody[PaymentResponse])
      .errorOut(jsonBody[ErrorInfo])

  }

  val paymentIDRoute: Route =
    AkkaHttpServerInterpreter().toRoute(getByPaymentId) { id =>
      Future.successful(paymentService.getById(id).map(_.toPaymentResponse))
    }

  private val getAllPaymentsByCurrency: Endpoint[String, ErrorInfo, List[PaymentResponse], Any] = {
    endpoint.get.in("payments").in(query[String]("currency"))
      .out(jsonBody[List[PaymentResponse]]).errorOut(jsonBody[ErrorInfo])
  }


  val paymentListRoute: Route =
    AkkaHttpServerInterpreter().toRoute(getAllPaymentsByCurrency) { currency =>
      Future.successful(paymentService.getAllPaymentsByCurrency(currency).map(_.map(_.toPaymentResponse)))
    }

  val getStatisticByStats: Endpoint[String, ErrorInfo, StatsResponse, Any] = {
    endpoint.get.in("payments" / "stats").in(query[String]("currency"))
      .out(jsonBody[StatsResponse]).errorOut(jsonBody[ErrorInfo])
  }

  val StatisticByStatsRoute: Route =
    AkkaHttpServerInterpreter().toRoute(getStatisticByStats) { currency =>
        Future.successful(paymentService.getStatisticsByStats(currency))
    }
}
