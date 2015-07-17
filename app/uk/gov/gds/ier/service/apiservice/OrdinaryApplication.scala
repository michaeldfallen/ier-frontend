package uk.gov.gds.ier.service.apiservice

import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.DateOfBirth
import uk.gov.gds.ier.model.IsoNationality
import uk.gov.gds.ier.model.PostalVote
import uk.gov.gds.ier.model.Name
import uk.gov.gds.ier.model.PreviousName
import uk.gov.gds.ier.model.Nino
import uk.gov.gds.ier.model.Address
import uk.gov.gds.ier.model.Contact

case class OrdinaryApplication(
    name: Option[Name],
    previousName: Option[PreviousName],
    lastRegisteredToVote: Option[LastRegisteredToVote],
    dob: Option[DateOfBirth],
    nationality: Option[IsoNationality],
    nino: Option[Nino],
    address: Option[Address],
    previousAddress: Option[Address],
    otherAddress: Option[OtherAddress],
    openRegisterOptin: Option[Boolean],
    postalVote: Option[PostalVote],
    contact: Option[Contact],
    referenceNumber: Option[String],
    ip: Option[String],
    timeTaken: String,
    language: String,
    sessionId: String
) extends CompleteApplication {

  def toApiMap:Map[String, String] = {
    val apiMap = Map.empty ++
      name.map(_.toApiMap("fn", "mn", "ln")).getOrElse(Map.empty) ++
      lastRegisteredToVote.map(_.toApiMap).getOrElse(Map.empty) ++
      previousName.map(_.toApiMap("p")).getOrElse(Map.empty) ++
      dob.map(_.toApiMap).getOrElse(Map.empty) ++
      nationality.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      address.map(_.toApiMap("reg")).getOrElse(Map.empty) ++
      previousAddress.map(_.toApiMap("p")).getOrElse(Map.empty) ++
      otherAddress.map(_.toApiMap).getOrElse(Map.empty) ++
      openRegisterOptin.map(open => Map("opnreg" -> open.toString)).getOrElse(Map.empty) ++
      postalVote.map(postalVote => postalVote.postalVoteOption.map(
        postalVoteOption => Map("pvote" -> postalVoteOption.apiValue.toString)).getOrElse(Map.empty)).getOrElse(Map.empty) ++
      postalVote.map(postalVote => postalVote.deliveryMethod.map(
        deliveryMethod => deliveryMethod.emailAddress.map(
        emailAddress => Map("pvoteemail" -> emailAddress)).getOrElse(Map.empty)).getOrElse(Map.empty)).getOrElse(Map.empty) ++
      contact.map(_.toApiMap).getOrElse(Map.empty) ++
      referenceNumber.map(refNum => Map("refNum" -> refNum)).getOrElse(Map.empty) ++
      address.flatMap(_.gssCode.map(gssCode => Map("gssCode" -> gssCode))).getOrElse(Map.empty)  ++
      previousAddress.flatMap(_.gssCode.map(gssCode => Map("pgssCode" -> gssCode))).getOrElse(Map.empty) ++
      ip.map(ipAddress => Map("ip" -> ipAddress)).getOrElse(Map.empty) ++
      Map(
        "applicationType" -> "ordinary",
        "timeTaken" -> timeTaken,
        "lang" -> language,
        "webHash" -> sessionId
      )

    removeSpecialCharacters(apiMap)
  }
}
