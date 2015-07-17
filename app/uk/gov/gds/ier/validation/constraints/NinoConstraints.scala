package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, NinoValidator}
import play.api.data.validation.{Valid, Invalid, Constraint}
import uk.gov.gds.ier.model.{Nino}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait NinoConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val overseasNinoOrNoNinoReasonDefined = Constraint[InprogressOverseas](keys.nino.key) {
    application =>
      if (application.nino.isDefined) {
        Valid
      }
      else {
        Invalid("Please enter your National Insurance number", keys.nino.nino)
      }
  }

  lazy val ninoIsValidIfProvided = Constraint[Nino](keys.nino.nino.key) {
    nino =>
      nino match {
        case Nino(Some(nino), _) if NinoValidator.isValid(nino) => Valid
        case Nino(Some(nino), _) if !NinoValidator.isValid(nino) => {
          Invalid("Your National Insurance number is not correct", keys.nino.nino)
        }
        case _ => Valid
      }
  }
}
