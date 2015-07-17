package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.model._
import org.joda.time.DateTime
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model.{
  Name,
  PreviousName,
  WaysToVote}
import uk.gov.gds.ier.transaction.overseas.confirmation.ConfirmationForms
import org.joda.time.DateTime

class ConfirmationBlocksTests
  extends FormTestSuite
  with MockitoHelpers
  with WithMockOverseasControllers
  with ConfirmationForms {

  when(mockDateLeftCouncilStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/date-left-council"))
  when(mockDateLeftCrownStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/date-left-crown"))
  when(mockDateLeftArmyStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/date-left-army"))
  when(mockDateLeftUkStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/date-left-uk"))
  when(mockLastRegisteredToVoteStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/last-registered-to-vote"))
  when(mockPassportCheckStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/passport"))
  when(mockPassportDetailsStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/passport-details"))
  when(mockCitizenDetailsStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/citizen-details"))
  when(mockNameStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/name"))
  when(mockDateOfBirthStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/date-of-birth"))
  when(mockNinoStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/nino"))
  when(mockAddressStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/address"))
  when(mockOpenRegisterStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/open-register"))
  when(mockWaysToVoteStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/ways-to-vote"))
  when(mockContactStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/contact"))
  when(mockLastUkAddressStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/last-uk-address"))
  when(mockLastUkAddressManualStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/last-uk-address/manual"))
  when(mockLastUkAddressSelectStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/last-uk-address/select"))

  behavior of "ConfirmationBlocks.applicantBlocks"

  it should "return correct blocks for young voter" in {
    val filledForm = confirmationForm.fillAndValidate(incompleteYoungApplication)
    val blocks = new ConfirmationBlocks(filledForm, overseas)

    blocks.applicantBlocks() should be(
      List(
        blocks.lastRegistered,
        blocks.dateLeftUk,
        blocks.passport,
        blocks.name,
        blocks.previousName,
        blocks.dateOfBirth,
        blocks.nino,
        blocks.address,
        blocks.openRegister,
        blocks.waysToVote,
        blocks.contact
      )
    )
  }

  it should "return correct blocks for new voter" in {
    val filledForm = confirmationForm.fillAndValidate(incompleteNewApplication)
    val blocks = new ConfirmationBlocks(filledForm, overseas)

    blocks.applicantBlocks() should be(
      List(
        blocks.lastRegistered,
        blocks.dateLeftUk,
        blocks.lastUkAddress,
        blocks.passport,
        blocks.name,
        blocks.previousName,
        blocks.dateOfBirth,
        blocks.nino,
        blocks.address,
        blocks.openRegister,
        blocks.waysToVote,
        blocks.contact
      )
    )
  }

  it should "return correct blocks for renewer voter" in {
    val filledForm = confirmationForm.fillAndValidate(incompleteRenewerApplication)
    val blocks = new ConfirmationBlocks(filledForm, overseas)

    blocks.applicantBlocks() should be(
      List(
        blocks.lastRegistered,
        blocks.dateLeftUk,
        blocks.lastUkAddress,
        blocks.name,
        blocks.previousName,
        blocks.dateOfBirth,
        blocks.nino,
        blocks.address,
        blocks.openRegister,
        blocks.waysToVote,
        blocks.contact
      )
    )
  }

  it should "return correct blocks for crown voter" in {
    val filledForm = confirmationForm.fillAndValidate(incompleteCrownApplication)
    val blocks = new ConfirmationBlocks(filledForm, overseas)

    blocks.applicantBlocks() should be(
      List(
        blocks.lastRegistered,
        blocks.dateLeftCrown,
        blocks.lastUkAddress,
        blocks.passport,
        blocks.name,
        blocks.previousName,
        blocks.dateOfBirth,
        blocks.nino,
        blocks.address,
        blocks.openRegister,
        blocks.waysToVote,
        blocks.contact
      )
    )
  }

  it should "return correct blocks for council voter" in {
    val filledForm = confirmationForm.fillAndValidate(incompleteCouncilApplication)
    val blocks = new ConfirmationBlocks(filledForm, overseas)

    blocks.applicantBlocks() should be(
      List(
        blocks.lastRegistered,
        blocks.dateLeftCouncil,
        blocks.lastUkAddress,
        blocks.passport,
        blocks.name,
        blocks.previousName,
        blocks.dateOfBirth,
        blocks.nino,
        blocks.address,
        blocks.openRegister,
        blocks.waysToVote,
        blocks.contact
      )
    )
  }

  it should "return correct blocks for forces voter" in {
    val filledForm = confirmationForm.fillAndValidate(incompleteForcesApplication)
    val blocks = new ConfirmationBlocks(filledForm, overseas)

    blocks.applicantBlocks() should be(
      List(
        blocks.lastRegistered,
        blocks.dateLeftForces,
        blocks.lastUkAddress,
        blocks.passport,
        blocks.name,
        blocks.previousName,
        blocks.dateOfBirth,
        blocks.nino,
        blocks.address,
        blocks.openRegister,
        blocks.waysToVote,
        blocks.contact
      )
    )
  }
}
