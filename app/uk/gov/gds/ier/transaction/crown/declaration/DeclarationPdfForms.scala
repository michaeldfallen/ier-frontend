package uk.gov.gds.ier.transaction.crown.declaration

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.crown.InprogressCrown

/**
 * Validation form for Download PDF, there is no user input, no request variables,
 * what is validated here is a session variable, conveniently merged to current form
 */
trait DeclarationPdfForms extends CommonConstraints {
  self:  FormKeys
    with ErrorMessages =>
  val declarationPdfForm = ErrorTransformForm(
    mapping(
      keys.address.address.postcode.key -> text
    ) (
      postcode => emptyInprogressCrownAsTypeMarker
    ) (
      inprogress => inprogress.address.flatMap(_.address).map(_.postcode)
    )
  )

  val emptyInprogressCrownAsTypeMarker = InprogressCrown()
}
