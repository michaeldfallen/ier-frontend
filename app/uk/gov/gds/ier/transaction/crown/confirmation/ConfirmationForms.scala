package uk.gov.gds.ier.transaction.crown.confirmation

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, ErrorMessages, Key}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.crown.statement.StatementForms
import uk.gov.gds.ier.transaction.crown.nationality.NationalityForms
import uk.gov.gds.ier.transaction.crown.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.transaction.crown.name.NameForms
import uk.gov.gds.ier.transaction.crown.nino.NinoForms
import uk.gov.gds.ier.transaction.crown.address.AddressForms
import uk.gov.gds.ier.transaction.crown.contactAddress.ContactAddressForms
import uk.gov.gds.ier.transaction.crown.openRegister.OpenRegisterForms
import uk.gov.gds.ier.transaction.crown.waysToVote.WaysToVoteForms
import uk.gov.gds.ier.transaction.crown.applicationFormVote.PostalOrProxyVoteForms
import uk.gov.gds.ier.transaction.crown.contact.ContactForms
import uk.gov.gds.ier.transaction.crown.job.JobForms
import uk.gov.gds.ier.transaction.crown.previousAddress.PreviousAddressForms
import uk.gov.gds.ier.transaction.crown.InprogressCrown

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
  with JobForms
  with ContactAddressForms
  with OpenRegisterForms
  with WaysToVoteForms
  with PostalOrProxyVoteForms
  with ContactForms
  with CommonConstraints
  with ConfirmationConstraints {

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.statement.key -> optional(statementMapping),
      keys.address.key -> optional(lastAddressMapping),
      keys.previousAddress.key -> optional(PartialPreviousAddress.mapping.verifying(previousAddressRequiredIfMoved)),
      keys.nationality.key -> optional(PartialNationality.mapping),
      keys.dob.key -> optional(dobAndReasonMapping),
      keys.name.key -> optional(Name.mapping),
      keys.previousName.key -> optional(PreviousName.mapping),
      keys.job.key -> optional(jobMapping),
      keys.nino.key -> optional(ninoMapping),
      keys.contactAddress.key -> optional(possibleContactAddressesMapping),
      keys.openRegister.key -> optional(openRegisterOptInMapping),
      keys.waysToVote.key -> optional(waysToVoteMapping),
      keys.postalOrProxyVote.key -> optional(postalOrProxyVoteMapping),
      keys.contact.key -> optional(contactMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping),
      keys.sessionId.key -> optional(text)
    ) (
      InprogressCrown.apply
    ) (
      InprogressCrown.unapply
    ).verifying (
      statementStepRequired,
      addressStepRequired,
      previousAddressStepRequired,
      nationalityStepRequired,
      dobStepRequired,
      nameStepRequired,
      previousNameStepRequired,
      jobStepRequired,
      ninoStepRequired,
      contactAddressStepRequired,
      openRegisterOptinStepRequired,
      contactStepRequired,
      waysToVoteStepRequired
    )
  )
}

trait ConfirmationConstraints {
  self: FormKeys
    with ErrorMessages =>

  val statementStepRequired = requireThis(keys.statement) { _.statement }
  val addressStepRequired = requireThis(keys.address) { _.address }
  val nationalityStepRequired = requireThis(keys.nationality) { _.nationality }
  val dobStepRequired = requireThis(keys.dob) { _.dob }
  val nameStepRequired = requireThis(keys.name) { _.name }
  val previousNameStepRequired = requireThis(keys.previousName) { _.previousName }
  val jobStepRequired = requireThis(keys.job) { _.job }
  val ninoStepRequired = requireThis(keys.nino) { _.nino }
  val contactAddressStepRequired = requireThis(keys.contactAddress) { _.contactAddress }
  val openRegisterOptinStepRequired = requireThis(keys.openRegister) { _.openRegisterOptin }
  val contactStepRequired = requireThis(keys.contact) { _.contact }

  def requireThis[T](key:Key)(extractT:InprogressCrown => Option[T]) = {
    Constraint[InprogressCrown](s"${key.name}required") { application =>
      extractT(application) match {
        case Some(_) => Valid
        case None => Invalid("Please complete this step", key)
      }
    }
  }

  val previousAddressStepRequired = Constraint[InprogressCrown]("previousAddressStepRequired") {
    application =>
      application.address match {
        case Some(LastAddress(Some(hasAddressOption), _))
          if (hasAddressOption.hasAddress) => {
            application.previousAddress match {
              case Some(_) => Valid
              case None => Invalid("Please complete this step", keys.previousAddress)
            }
          }
        case _ => Valid
      }
  }

  val waysToVoteStepRequired = Constraint[InprogressCrown]("waysToVoteRequired") {
    application =>
      import uk.gov.gds.ier.model.WaysToVoteType._

    val waysToVote = application.waysToVote.map { _.waysToVoteType }
    val postalOrProxy = application.postalOrProxyVote

    waysToVote match {
      case Some(InPerson) => Valid
      case Some(ByPost) if postalOrProxy.isDefined => Valid
      case Some(ByProxy) if postalOrProxy.isDefined => Valid
      case _ => Invalid("Please complete this step", keys.waysToVote)
    }
  }
}
