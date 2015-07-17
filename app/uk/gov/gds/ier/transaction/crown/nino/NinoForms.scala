package uk.gov.gds.ier.transaction.crown.nino

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.NinoConstraints
import uk.gov.gds.ier.model.{Nino}
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait NinoForms extends NinoCrownConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val ninoMapping = mapping(
    keys.nino.key -> optional(nonEmptyText),
    keys.noNinoReason.key -> optional(nonEmptyText
      .verifying(noNinoReasonMaxLengthError, _.size <= maxExplanationFieldLength))
  ) (
    Nino.apply
  ) (
    Nino.unapply
  ).verifying(ninoIsValidIfProvided)

  val ninoForm = ErrorTransformForm(
    mapping(keys.nino.key -> optional(ninoMapping))
    (
      nino => InprogressCrown(nino = nino)
    ) (
      inprogress => Some(inprogress.nino)
    ) verifying (ninoOrNoNinoReasonDefinedCrown)
  )
}

trait NinoCrownConstraints extends NinoConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val ninoOrNoNinoReasonDefinedCrown = Constraint[InprogressCrown](keys.nino.key) {
    application =>
      if (application.nino.isDefined) {
        Valid
      }
      else {
        Invalid("Please enter your National Insurance number", keys.nino.nino)
      }
  }
}
