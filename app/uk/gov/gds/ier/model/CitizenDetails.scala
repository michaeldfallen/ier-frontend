package uk.gov.gds.ier.model

case class CitizenDetails(
    dateBecameCitizen: DOB,
    howBecameCitizen: String,
    birthplace: String) {

  def toApiMap = {
    dateBecameCitizen.toApiMap("dbritcit") ++
      Map(
        "hbritcit" -> howBecameCitizen,
        "birthplace" -> birthplace
      )
  }
}
