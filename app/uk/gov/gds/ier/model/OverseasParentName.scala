package uk.gov.gds.ier.model

case class OverseasParentName(
   name: Option[Name],
   previousName: Option[PreviousName] = None) {
  def toApiMap(prevPrefix:String) = {
    name.map(_.toApiMap("pgfn", "pgmn", "pgln")).getOrElse(Map.empty) ++
    previousName.map(_.toApiMap(prevPrefix)).getOrElse(Map.empty)
  }
}
