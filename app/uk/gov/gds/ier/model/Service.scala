package uk.gov.gds.ier.model

case class Service(
    serviceName: Option[ServiceType],
    regiment: Option[String]) {

  def toApiMap =
    serviceName.map(serviceName => Map("serv" -> serviceName.name)).getOrElse(Map.empty) ++
    regiment.map(regiment => Map("reg" -> regiment.toString)).getOrElse(Map.empty)
}
