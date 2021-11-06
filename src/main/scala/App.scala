import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import controllers.PaymentController
import services.PaymentService

import scala.io.StdIn
import scala.util.{Failure, Success}

object App {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem[Nothing](Behaviors.empty, "main-system")

    import system.executionContext

    val paymentService: PaymentService = new PaymentService

    val paymentController = new PaymentController(paymentService)


    val route = concat(
      paymentController.createNewPaymentRoute,
      paymentController.paymentIDRoute,
      paymentController.paymentListRoute,
      paymentController.StatisticByStatsRoute
    )


    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
    bindingFuture.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
}
