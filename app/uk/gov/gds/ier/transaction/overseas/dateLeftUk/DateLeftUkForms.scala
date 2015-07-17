package uk.gov.gds.ier.transaction.overseas.dateLeftUk

import play.api.data.Forms._
import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{DateLeft}
import uk.gov.gds.ier.validation.constraints.overseas.DateLeftUkConstraints
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait DateLeftUkForms extends DateLeftUkConstraints {
  self: FormKeys
  with ErrorMessages =>

  val dateLeftUkMapping = mapping(
    keys.month.key -> text
      .verifying("Please enter the month when you left the UK", _.nonEmpty)
      .verifying("The month you provided is invalid", month => month.isEmpty || month.matches("\\d+")),
    keys.year.key -> text
      .verifying("Please enter the year when you left the UK", _.nonEmpty)
      .verifying("The year you provided is invalid", day => day.isEmpty || day.matches("\\d+"))
  ) {
    (month, year) => DateLeft(year.toInt, month.toInt)
  } {
    dateLeftUk =>
      Some(
        dateLeftUk.month.toString,
        dateLeftUk.year.toString
      )
  }

  val dateLeftUkForm = ErrorTransformForm(
    mapping (
      keys.dateLeftUk.key -> optional(dateLeftUkMapping)
    ) (
      dateLeftUk => InprogressOverseas(dateLeftUk = dateLeftUk)
    ) (
      inprogress => Some(inprogress.dateLeftUk)
    ).verifying (validateDateLeftUk)
  )
}
