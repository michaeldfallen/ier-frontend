package uk.gov.gds.ier.transaction.overseas.dateLeftSpecial

import play.api.data.Forms._
import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{DateLeft}
import uk.gov.gds.ier.validation.constraints.overseas.DateLeftSpecialConstraints
import uk.gov.gds.ier.model.LastRegisteredType
import uk.gov.gds.ier.model.DateLeftSpecial
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait DateLeftSpecialForms extends DateLeftSpecialConstraints {
  self: FormKeys
  with ErrorMessages =>

  def dateLeftSpecialMapping = mapping(
    keys.month.key -> text
      .verifying("Please enter the month when you left", _.nonEmpty)
      .verifying("The month you provided is invalid", month => month.isEmpty || month.matches("\\d+")),
    keys.year.key -> text
      .verifying("Please enter the year when you left", _.nonEmpty)
      .verifying("The year you provided is invalid", day => day.isEmpty || day.matches("\\d+"))
  ) {
    (month, year) => DateLeft(year.toInt, month.toInt)
  } {
    dateLeftSpecial =>
      Some(
        dateLeftSpecial.month.toString,
        dateLeftSpecial.year.toString
      )
  }
  
  def dateLeftSpecialTypeMapping = 
    dateLeftSpecialMapping.transform[DateLeftSpecial](
      date =>	DateLeftSpecial(date),
      dateLeftSpecial => dateLeftSpecial.date
  )

  def dateLeftSpecialForm = ErrorTransformForm(
    mapping (
      keys.dateLeftSpecial.key -> optional(dateLeftSpecialTypeMapping)
    ) (
      dateLeftSpecial => InprogressOverseas(dateLeftSpecial = dateLeftSpecial)
    ) (
      inprogress => Some(inprogress.dateLeftSpecial)
    ).verifying (validateDateLeftSpecial)
  )
}
