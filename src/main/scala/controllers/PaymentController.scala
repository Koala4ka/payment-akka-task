package controllers

import akka.http.scaladsl.server.Route
import exeptions.ErrorInfo
import models.requests.PaymentRequest
import models.responses.PaymentResponse
import services.PaymentService
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.spray._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.Future

class PaymentController(paymentService: PaymentService) {

  private val createNewPaymentEndpoint: Endpoint[PaymentRequest, ErrorInfo, PaymentResponse, Any] =
    endpoint
      .post.in("payment" / "new").in(jsonBody[PaymentRequest])
      .out(jsonBody[PaymentResponse])
      .errorOut(jsonBody[ErrorInfo])

  def createNewPaymentRoute: Route =
    AkkaHttpServerInterpreter().toRoute(createNewPaymentEndpoint) { paymentRequest =>
      Future
        .successful(
          paymentService
          .createPayment(paymentRequest)
            .map(_.toPaymentResponse)
     )
    }
}
