package uk.gov.gds.ier.service.apiservice

trait CompleteApplication {
  def timeTaken:String

  def toApiMap:Map[String, String]

  def removeSpecialCharacters (apiMap: Map[String, String]): Map[String, String] = {
    apiMap.mapValues(apiFieldValue =>
      apiFieldValue
        .replaceAll("[<>|]", "")
        .replaceAll("\t"," ")
        .trim)
  }
}
