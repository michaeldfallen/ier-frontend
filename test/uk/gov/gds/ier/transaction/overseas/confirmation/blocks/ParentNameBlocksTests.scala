package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.model._
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model.{
  Name,
  PreviousName}
import uk.gov.gds.ier.transaction.overseas.confirmation.ConfirmationForms
import org.joda.time.DateTime
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.transaction.shared.{BlockContent, BlockError}

class ParentNameBlocksTests
  extends FormTestSuite
  with MockitoHelpers
  with WithMockOverseasControllers
  with ConfirmationForms {

  when(mockParentNameStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/parent-name"))

  behavior of "confirmationBlocks.parentName"

  it should "return 'complete this' (age > 18, leftuk < 15 years, and step incomplete)" in {
    val twentyYearsAgo = new DateTime().minusYears(20).getYear
    val fourteenYearsAgo = new DateTime().minusYears(14).getYear

    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      dob = Some(DOB(twentyYearsAgo, 10, 10)),
      dateLeftUk = Some(DateLeft(fourteenYearsAgo, 10))
    ))

    partialApplication(keys.overseasParentName.parentName).hasErrors should be (true)

    val confirmation = new ConfirmationBlocks(partialApplication, overseas)
    val parentNameModel = confirmation.parentName
    val parentPreviousNameModel = confirmation.parentPreviousName

    val nameModel = parentNameModel
    nameModel.content should be (BlockError("Please complete this step"))
    nameModel.editLink should be ("/register-to-vote/overseas/edit/parent-name")

    val previousNameModel = parentPreviousNameModel
    previousNameModel.content should be (BlockError("Please complete this step"))
    previousNameModel.editLink should be ("/register-to-vote/overseas/edit/parent-name")
  }

  it should "correctly render parent names and previous names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      overseasParentName = Some(OverseasParentName(
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
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm, overseas)

    val nameModel = confirmation.parentName
    nameModel.content should be(BlockContent("John Smith"))
    nameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")

    val prevNameModel = confirmation.parentPreviousName
    prevNameModel.content should be(BlockContent("Jan Kovar"))
    prevNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")
  }


  it should "correctly render parent names and previous names with middle names" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      overseasParentName = Some(OverseasParentName(
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
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm, overseas)

    val parentNameModel = confirmation.parentName
    parentNameModel.content should be(BlockContent("John Walker Junior Smith"))
    parentNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")

    val parentPrevNameModel = confirmation.parentPreviousName
    parentPrevNameModel.content should be(BlockContent("Jan Janko Janik Kovar"))
    parentPrevNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")
  }

  it should "correctly render parent names" in {
    val twentyYearsAgo = new DateTime().minusYears(20).getYear
    val fourteenYearsAgo = new DateTime().minusYears(14).getYear

    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      dob = Some(DOB(twentyYearsAgo, 10, 10)),
      dateLeftUk = Some(DateLeft(fourteenYearsAgo, 10)),
      overseasParentName = Some(OverseasParentName(
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
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm, overseas)

    val parentNameModel = confirmation.parentName
    parentNameModel.content should be(BlockContent("John Walker Junior Smith"))
    parentNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")

    val parentPrevNameModel = confirmation.parentPreviousName
    parentPrevNameModel.content should be(BlockContent("They haven't changed their name since they left the UK"))
    parentPrevNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")
  }
}
