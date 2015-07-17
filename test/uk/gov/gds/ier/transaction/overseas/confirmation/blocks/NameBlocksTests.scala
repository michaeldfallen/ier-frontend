package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model.{Name, PreviousName}
import uk.gov.gds.ier.transaction.overseas.confirmation.ConfirmationForms
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.transaction.shared.BlockContent

class NameBlocksTests
  extends FormTestSuite
  with WithMockOverseasControllers
  with MockitoHelpers
  with ConfirmationForms {

  when(mockNameStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/name"))

  "In-progress application form with filled name and previous name" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith")),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        hasPreviousNameOption = "true",
        previousName = Some(Name(
          firstName = "Jan",
          middleNames = None,
          lastName = "Kovar"))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm, overseas)

    val nameModel = confirmation.name
    nameModel.content should be(BlockContent("John Smith"))
    nameModel.editLink should be("/register-to-vote/overseas/edit/name")

    val prevNameModel = confirmation.previousName
    prevNameModel.content should be(BlockContent("Jan Kovar"))
    prevNameModel.editLink should be("/register-to-vote/overseas/edit/name")
  }

  "In-progress application form with filled name and previous name with middle names" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      name = Some(Name(
        firstName = "John",
        middleNames = Some("Walker Junior"),
        lastName = "Smith")),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        hasPreviousNameOption = "true",
        previousName = Some(Name(
          firstName = "Jan",
          middleNames = Some("Janko Janik"),
          lastName = "Kovar"))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm, overseas)

    val nameModel = confirmation.name
    nameModel.content should be(BlockContent("John Walker Junior Smith"))
    nameModel.editLink should be("/register-to-vote/overseas/edit/name")

    val prevNameModel = confirmation.previousName
    prevNameModel.content should be(BlockContent("Jan Janko Janik Kovar"))
    prevNameModel.editLink should be("/register-to-vote/overseas/edit/name")
  }

  "In-progress application form with filled name and no previous name" should
    "generate confirmation mustache model with correctly rendered names and information message for previous name" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      name = Some(Name(
        firstName = "John",
        middleNames = Some("Walker Junior"),
        lastName = "Smith")),
      previousName = Some(PreviousName(
        hasPreviousName = false,
        hasPreviousNameOption = "false",
        previousName = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm, overseas)

    val nameModel = confirmation.name
    nameModel.content should be(BlockContent("John Walker Junior Smith"))
    nameModel.editLink should be("/register-to-vote/overseas/edit/name")

    val prevNameModel = confirmation.previousName
    prevNameModel.content should be(BlockContent("I have not changed my name in the last 12 months"))
    prevNameModel.editLink should be("/register-to-vote/overseas/edit/name")
  }

  "In-progress application form with filled name, previous name and a reason for changing it" should
    "generate confirmation mustache model with correctly rendered names and the reason for the name change" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      name = Some(Name(
        firstName = "John",
        middleNames = Some("Walker Junior"),
        lastName = "Smith")),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        hasPreviousNameOption = "true",
        previousName = Some(Name(
          firstName = "Jan",
          middleNames = Some("Janko Janik"),
          lastName = "Kovar")),
        reason = Some("marriage")
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm, overseas)

    val nameModel = confirmation.name
    nameModel.content should be(BlockContent("John Walker Junior Smith"))
    nameModel.editLink should be("/register-to-vote/overseas/edit/name")

    val prevNameModel = confirmation.previousName
    prevNameModel.content should be(BlockContent(List(
      "Jan Janko Janik Kovar",
      "Reason for the name change:",
      "marriage"
    )))
    prevNameModel.editLink should be("/register-to-vote/overseas/edit/name")
  }

}
