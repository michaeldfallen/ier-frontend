package uk.gov.gds.ier.transaction.overseas.nino

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.NinoConstraints
import uk.gov.gds.ier.model.{Nino}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait NinoForms extends NinoConstraints {
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
      nino => InprogressOverseas(nino = nino)
    ) (
      inprogress => Some(inprogress.nino)
    ) verifying (overseasNinoOrNoNinoReasonDefined)
  )
}

