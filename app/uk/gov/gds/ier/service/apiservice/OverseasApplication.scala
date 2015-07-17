package uk.gov.gds.ier.service.apiservice

import uk.gov.gds.common.model.LocalAuthority
import uk.gov.gds.ier.model._

case class OverseasApplication(
    name: Option[Name],
    previousName: Option[PreviousName],
    dateLeftUk: Option[DateLeft],
    dateLeftSpecial: Option[DateLeftSpecial],
    overseasParentName: Option[OverseasParentName] = None,
    lastRegisteredToVote: Option[LastRegisteredToVote],
    dob: Option[DOB],
    nino: Option[Nino],
    address: Option[OverseasAddress],
    lastUkAddress: Option[Address] = None,
    parentsAddress: Option[Address] = None,
    openRegisterOptin: Option[Boolean],
    postalOrProxyVote: Option[PostalOrProxyVote],
    passport: Option[Passport],
    contact: Option[Contact],
    referenceNumber: Option[String],
    ip: Option[String],
    timeTaken: String,
    sessionId: String
) extends CompleteApplication {

  def toApiMap = {
    val authorityGssCodeSource = lastUkAddress.orElse(parentsAddress)
    val apiMap = Map.empty ++
      name.map(_.toApiMap("fn", "mn", "ln")).getOrElse(Map.empty) ++
      previousName.map(_.toApiMap("p")).getOrElse(Map.empty) ++
      lastRegisteredToVote.map(_.toApiMap).getOrElse(Map.empty) ++
      dateLeftUk.map(_.toApiMap()).getOrElse(Map.empty) ++
      dateLeftSpecial.map(_.toApiMap).getOrElse(Map.empty) ++
      overseasParentName.map(_.toApiMap("pgr")).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      lastUkAddress.map(_.toApiMap("reg")).getOrElse(Map.empty) ++
      dob.map(_.toApiMap("dob")).getOrElse(Map.empty) ++
      address.map(_.toApiMap).getOrElse(Map.empty) ++
      lastUkAddress.map(_.toApiMap("reg")).getOrElse(Map.empty) ++
      parentsAddress.map(_.toApiMap("reg")).getOrElse(Map.empty) ++
      openRegisterOptin.map(open => Map("opnreg" -> open.toString)).getOrElse(Map.empty) ++
      postalOrProxyVote.map(_.toApiMap).getOrElse(Map.empty) ++
      passport.map(_.toApiMap).getOrElse(Map.empty) ++
      contact.map(_.toApiMap).getOrElse(Map.empty) ++
      referenceNumber.map(refNum => Map("refNum" -> refNum)).getOrElse(Map.empty) ++
      authorityGssCodeSource.flatMap(_.gssCode.map(gssCode => Map("gssCode" -> gssCode))).getOrElse(Map.empty) ++
      ip.map(ipAddress => Map("ip" -> ipAddress)).getOrElse(Map.empty) ++ Map(
        "applicationType" -> "overseas",
        "timeTaken" -> timeTaken,
        "webHash" -> sessionId
      )

    removeSpecialCharacters(apiMap)
  }
}
