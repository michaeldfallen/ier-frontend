package uk.gov.gds.ier.model

case class OverseasName(
    name: Option[Name],
    previousName: Option[PreviousName] = None) {

  def toApiMap(prevPrefix:String) = {
    name.map(_.toApiMap("fn", "mn", "ln")).getOrElse(Map.empty) ++
      previousName.map(_.toApiMap(prevPrefix)).getOrElse(Map.empty)
  }
}
