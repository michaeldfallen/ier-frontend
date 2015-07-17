package uk.gov.gds.ier.transaction.overseas.passport

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.validation.constraints.PassportConstraints
import play.api.data.Forms._
import uk.gov.gds.ier.model.{
  Passport,
  PassportDetails,
  CitizenDetails,
  DOB}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait PassportForms extends PassportConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val dateMapping = mapping(
    keys.year.key -> text
      .verifying("Please enter a year", _.nonEmpty)
      .verifying("The year you provided is invalid", year => year.isEmpty || year.matches("\\d+")),
    keys.month.key -> text
      .verifying("Please enter a month", _.nonEmpty)
      .verifying("The month you provided is invalid", month => month.isEmpty || month.matches("\\d+")),
    keys.day.key -> text
      .verifying("Please enter a day", _.nonEmpty)
      .verifying("The day you provided is invalid", day => day.isEmpty || day.matches("\\d+"))
  ) {
    (year, month, day) => DOB(year.toInt, month.toInt, day.toInt)
  } {
    date => Some(date.year.toString, date.month.toString, date.day.toString)
  }

  lazy val passportDetailsMapping = mapping(
    keys.passportNumber.key -> text,
    keys.authority.key -> text,
    keys.issueDate.key -> dateMapping.verifying(validPassportDate)
  ) (PassportDetails.apply) (PassportDetails.unapply)

  lazy val citizenDetailsMapping = mapping(
    keys.dateBecameCitizen.key -> dateMapping.verifying(validCitizenDate),
    keys.howBecameCitizen.key -> text,
    keys.birthplace.key -> text
  ) (CitizenDetails.apply) (CitizenDetails.unapply)

  lazy val passportMapping = mapping(
    keys.hasPassport.key -> boolean,
    keys.bornInsideUk.key -> optional(boolean),
    keys.passportDetails.key -> optional(passportDetailsMapping),
    keys.citizenDetails.key -> optional(citizenDetailsMapping)
  ) (Passport.apply) (Passport.unapply)

  val passportForm = ErrorTransformForm(
    mapping(
      keys.passport.key -> optional(passportMapping)
    ) (
      passport => InprogressOverseas(
        passport = passport
      )
    ) (
      inprogress => Some(inprogress.passport)
    )
  )

  val passportCheckForm = ErrorTransformForm(
    passportForm.mapping.verifying(
      passportRequired,
      ifNoPassportBornInsideRequired
    )
  )

  val citizenDetailsForm = ErrorTransformForm(
    passportForm.mapping.verifying(
      citizenDetailsFilled,
      ifDefinedHowBecameCitizenRequired,
      ifDefinedBirthplaceRequired,
      passportRequired,
      ifNoPassportBornInsideRequired
    )
  )

  val passportDetailsForm = ErrorTransformForm(
    passportForm.mapping.verifying(
      passportDetailsFilled,
      ifDefinedPassportNumberRequired,
      ifDefinedAuthorityRequired,
      passportRequired,
      ifNoPassportBornInsideRequired
    )
  )
}
