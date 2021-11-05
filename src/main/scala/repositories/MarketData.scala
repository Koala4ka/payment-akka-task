package repositories

object MarketData {

  val exchangeRatesOfBTC: Map[String, BigDecimal] = Map("EUR" -> 42000, "USD" -> 50000)

  val exchangeRatesToEUR: Map[String, BigDecimal] = Map("EUR" -> 1.0, "USD" -> 0.84, "BTC" -> 42000)

}
