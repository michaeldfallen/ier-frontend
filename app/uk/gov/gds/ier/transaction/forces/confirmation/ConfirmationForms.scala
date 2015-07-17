package uk.gov.gds.ier.transaction.forces.confirmation

import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, ErrorMessages}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.forces.statement.StatementForms
import uk.gov.gds.ier.transaction.forces.address.AddressForms
import uk.gov.gds.ier.transaction.forces.nationality.NationalityForms
import uk.gov.gds.ier.transaction.forces.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.transaction.forces.name.NameForms
import uk.gov.gds.ier.transaction.forces.nino.NinoForms
import uk.gov.gds.ier.transaction.forces.service.ServiceForms
import uk.gov.gds.ier.transaction.forces.rank.RankForms
import uk.gov.gds.ier.transaction.forces.contactAddress.ContactAddressForms
import uk.gov.gds.ier.transaction.forces.openRegister.OpenRegisterForms
import uk.gov.gds.ier.transaction.forces.waysToVote.WaysToVoteForms
import uk.gov.gds.ier.transaction.forces.applicationFormVote.PostalOrProxyVoteForms
import uk.gov.gds.ier.transaction.forces.contact.ContactForms
import uk.gov.gds.ier.transaction.forces.previousAddress.PreviousAddressForms
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.service.AddressService

trait ConfirmationForms
  extends FormKeys
  with ErrorMessages
  with WithSerialiser
  with StatementForms
  with AddressForms
  with PreviousAddressForms
  with NationalityForms
  with DateOfBirthForms
  with NameForms
  with NinoForms
  with ServiceForms
  with RankForms
  with ContactAddressForms
  with OpenRegisterForms
  with WaysToVoteForms
  with PostalOrProxyVoteForms
  with ContactForms
  with CommonConstraints {

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.statement.key -> stepRequired(statementMapping),
      keys.address.key -> stepRequired(LastAddress.mapping),
      keys.previousAddress.key -> optional(partialPreviousAddressMappingForPreviousAddress),
      keys.nationality.key -> stepRequired(PartialNationality.mapping),
      keys.dob.key -> stepRequired(dobAndReasonMapping),
      keys.name.key -> stepRequired(Name.mapping),
      keys.previousName.key -> stepRequired(PreviousName.mapping),
      keys.nino.key -> stepRequired(ninoMapping),
      keys.service.key -> stepRequired(serviceMapping),
      keys.rank.key -> stepRequired(rankMapping),
      keys.contactAddress.key -> stepRequired(possibleContactAddressesMapping),
      keys.openRegister.key -> stepRequired(openRegisterOptInMapping),
      keys.waysToVote.key -> stepRequired(waysToVoteMapping),
      keys.postalOrProxyVote.key -> optional(postalOrProxyVoteMapping),
      keys.contact.key -> stepRequired(contactMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping),
      keys.sessionId.key -> optional(text)
    )
    (InprogressForces.apply)
    (InprogressForces.unapply)
  )
}
