package uk.gov.gds.ier.transaction.ordinary.confirmation

import uk.gov.gds.ier.validation.{OrdinaryMappings, ErrorTransformForm}
import play.api.data.Forms._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait ConfirmationForms extends OrdinaryMappings {
  self: WithSerialiser =>

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.name.key -> optional(Name.mapping).verifying("ordinary_confirmation_error_completeThis", _.isDefined),
      keys.previousName.key -> optional(PreviousName.mapping).verifying("ordinary_confirmation_error_completeThis", _.isDefined),
      keys.dob.key -> optional(dobAndReasonMapping).verifying("ordinary_confirmation_error_completeThis", _.isDefined),
      keys.nationality.key -> optional(PartialNationality.mapping).verifying("ordinary_confirmation_error_completeThis", _.isDefined),
      keys.nino.key -> optional(Nino.mapping).verifying("ordinary_confirmation_error_completeThis", _.isDefined),
      keys.address.key -> optional(partialAddressMapping).verifying("ordinary_confirmation_error_completeThis", _.isDefined),
      keys.previousAddress.key ->
        optional(PartialPreviousAddress.mapping.verifying(previousAddressRequiredIfMoved))
        .verifying("ordinary_confirmation_error_completeThis", _.isDefined),
      keys.otherAddress.key -> optional(OtherAddress.otherAddressMapping).verifying("ordinary_confirmation_error_completeThis", _.isDefined),
      keys.openRegister.key -> optional(optInMapping).verifying("ordinary_confirmation_error_completeThis", _.isDefined),
      keys.postalVote.key -> optional(PostalVote.mapping).verifying("ordinary_confirmation_error_completeThis", _.isDefined),
      keys.contact.key -> optional(Contact.mapping).verifying("ordinary_confirmation_error_completeThis", _.isDefined),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping),
      keys.country.key -> optional(countryMapping),
      keys.sessionId.key -> optional(text)
    ) (InprogressOrdinary.apply) (InprogressOrdinary.unapply)
  )

}
