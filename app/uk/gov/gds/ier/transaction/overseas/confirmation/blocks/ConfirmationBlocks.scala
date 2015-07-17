package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.form.OverseasFormImplicits
import uk.gov.gds.ier.model.ApplicationType._
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.validation.{FormKeys, ErrorTransformForm, Key}
import uk.gov.gds.ier.transaction.overseas.{InprogressOverseas, WithOverseasControllers, OverseasControllers}
import uk.gov.gds.ier.transaction.shared.{BlockContent, BlockError, EitherErrorOrContent}

case class ConfirmationQuestion(
  content: EitherErrorOrContent,
  title: String,
  editLink: String,
  changeName: String
)

trait ConfirmationBlock
  extends Logging
  with FormKeys
  with WithOverseasControllers
  with OverseasFormImplicits {

  val form: ErrorTransformForm[InprogressOverseas]

  val completeThisStepMessage = "Please complete this step"

  def ifComplete(key:Key)(confirmationTexts: => List[String]): EitherErrorOrContent = {
    if (form(key).hasErrors) {
      BlockError(completeThisStepMessage)
    } else {
      BlockContent(confirmationTexts)
    }
  }
}

class ConfirmationBlocks (
    val form:ErrorTransformForm[InprogressOverseas],
    val overseas: OverseasControllers
) extends ConfirmationBlock
  with LastRegisteredToVoteBlocks
  with LastUkAddressBlocks
  with DateLeftBlocks
  with NinoBlocks
  with AddressBlocks
  with DateOfBirthBlocks
  with OpenRegisterBlocks
  with NameBlocks
  with WaysToVoteBlocks
  with ContactBlocks
  with PassportBlocks
  with ParentNameBlocks
  with ParentsAddressBlocks {

  def parentBlocks() = {
    form.identifyApplication match {
      case YoungVoter => List(
        parentName,
        parentPreviousName,
        parentsAddress
      )
      case _ => List.empty
    }
  }

  def applicantBlocks() = {
    form.identifyApplication match {
      case YoungVoter => youngVoterBlocks()
      case NewVoter => newVoterBlocks()
      case SpecialVoter => specialVoterBlocks()
      case RenewerVoter => renewerVoterBlocks()
      case _ => List.empty
    }
  }

  def youngVoterBlocks():List[ConfirmationQuestion] = {
    List(
      lastRegistered,
      dateLeftUk,
      passport,
      name,
      previousName,
      dateOfBirth,
      nino,
      address,
      openRegister,
      waysToVote,
      contact
    )
  }

  def newVoterBlocks():List[ConfirmationQuestion] = {
    List(
      lastRegistered,
      dateLeftUk,
      lastUkAddress,
      passport,
      name,
      previousName,
      dateOfBirth,
      nino,
      address,
      openRegister,
      waysToVote,
      contact
    )
  }

  def renewerVoterBlocks():List[ConfirmationQuestion] = {
    List(
      lastRegistered,
      dateLeftUk,
      lastUkAddress,
      name,
      previousName,
      dateOfBirth,
      nino,
      address,
      openRegister,
      waysToVote,
      contact
    )
  }

  def specialVoterBlocks():List[ConfirmationQuestion] = {
    List(
      lastRegistered,
      dateLeftSpecial,
      lastUkAddress,
      passport,
      name,
      previousName,
      dateOfBirth,
      nino,
      address,
      openRegister,
      waysToVote,
      contact
    )
  }
}

