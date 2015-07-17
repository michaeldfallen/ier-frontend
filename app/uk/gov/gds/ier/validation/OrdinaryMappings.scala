package uk.gov.gds.ier.validation

import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation
import uk.gov.gds.ier.validation.DateValidator._
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import uk.gov.gds.ier.transaction.ordinary.nationality.NationalityForms
import uk.gov.gds.ier.transaction.ordinary.name.NameForms
import uk.gov.gds.ier.transaction.ordinary.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.transaction.ordinary.nino.NinoForms
import uk.gov.gds.ier.transaction.ordinary.address.AddressForms
import uk.gov.gds.ier.transaction.ordinary.previousAddress.PreviousAddressForms
import uk.gov.gds.ier.transaction.ordinary.otherAddress.OtherAddressForms
import uk.gov.gds.ier.transaction.ordinary.openRegister.OpenRegisterForms
import uk.gov.gds.ier.transaction.ordinary.postalVote.PostalVoteForms
import uk.gov.gds.ier.transaction.ordinary.contact.ContactForms
import uk.gov.gds.ier.transaction.country.CountryForms
import uk.gov.gds.ier.form.OrdinaryFormImplicits

trait OrdinaryMappings
  extends FormKeys
  with ErrorMessages
  with NinoForms
  with NationalityForms
  with NameForms
  with AddressForms
  with OtherAddressForms
  with PreviousAddressForms
  with DateOfBirthForms
  with OpenRegisterForms
  with PostalVoteForms
  with ContactForms
  with CountryForms
  with OrdinaryFormImplicits {
    self: WithSerialiser =>

  val optInMapping = single(
    keys.optIn.key -> boolean
  )

}
