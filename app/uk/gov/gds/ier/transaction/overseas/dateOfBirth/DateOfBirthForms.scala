package uk.gov.gds.ier.transaction.overseas.dateOfBirth

import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, ErrorTransformForm}
import uk.gov.gds.ier.validation.constraints.DateOfBirthConstraints
import play.api.data.Form
import play.api.data.Forms._
import scala.Some
import uk.gov.gds.ier.model.DateOfBirth
import uk.gov.gds.ier.model.noDOB
import uk.gov.gds.ier.model.DOB
import scala.Some
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait DateOfBirthForms extends DateOfBirthConstraints {
    self:  FormKeys
      with ErrorMessages =>

  lazy val dobMapping = mapping(
    keys.year.key -> text
      .verifying("Please enter your year of birth", _.nonEmpty)
      .verifying("The year you provided is invalid", year => year.isEmpty || year.matches("\\d+")),
    keys.month.key -> text
      .verifying("Please enter your month of birth", _.nonEmpty)
      .verifying("The month you provided is invalid", month => month.isEmpty || month.matches("\\d+")),
    keys.day.key -> text
      .verifying("Please enter your day of birth", _.nonEmpty)
      .verifying("The day you provided is invalid", day => day.isEmpty || day.matches("\\d+"))
  ) {
    (year, month, day) => DOB(year.toInt, month.toInt, day.toInt)
  } {
    dateOfBirth => 
      Some(
        dateOfBirth.year.toString, 
        dateOfBirth.month.toString, 
        dateOfBirth.day.toString
      )
  }.verifying(validDateOverseas)
  
  

  val dateOfBirthForm = ErrorTransformForm(
    mapping(
      keys.dob.key -> optional(dobMapping)
    ) (
      dob => InprogressOverseas(dob = dob)
    ) (
      inprogress => Some(inprogress.dob)
    ) verifying dateOfBirthOverseasRequired
  )
}