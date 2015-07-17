package uk.gov.gds.ier.model

/**
 * Created by christopherd on 04/04/2014.
 */
case class PassportDetails(
    passportNumber: String,
    authority: String,
    issueDate: DOB) {

  def toApiMap = {
    Map(
      "passno" -> passportNumber,
      "passloc" -> authority
    ) ++ issueDate.toApiMap("passdate")
  }
}
