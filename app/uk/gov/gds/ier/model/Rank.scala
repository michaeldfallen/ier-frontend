package uk.gov.gds.ier.model

case class Rank(
    serviceNumber: Option[String],
    rank: Option[String]) {

  def toApiMap =
    serviceNumber.map(serviceNumber => Map("servno" -> serviceNumber.toString)).getOrElse(Map.empty) ++
    rank.map(rank => Map("rank" -> rank.toString)).getOrElse(Map.empty)
}
