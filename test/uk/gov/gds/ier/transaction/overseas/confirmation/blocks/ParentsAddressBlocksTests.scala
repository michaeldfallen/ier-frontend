package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.model._
import org.joda.time.DateTime
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model.{
  Name,
  PreviousName,
  WaysToVote}
import uk.gov.gds.ier.transaction.overseas.confirmation.ConfirmationForms
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.transaction.shared.{BlockError, BlockContent}

class ParentsAddressBlocksTests
  extends FormTestSuite
  with MockitoHelpers
  with WithMockOverseasControllers
  with ConfirmationForms {

  when(mockParentsAddressStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/parents-address"))
  when(mockParentsAddressManualStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/parents-address/manual"))
  when(mockParentsAddressSelectStep.routing).thenReturn(routes("/register-to-vote/overseas/edit/parents-address/select"))

  behavior of "ConfirmationBlocks.parentsAddress"
  it should "return parents address" in {
    val twentyYearsAgo = new DateTime().minusYears(20).getYear()
    val fiveYearsAgo = new DateTime().minusYears(5).getYear()

    val partialApplication = confirmationForm.fillAndValidate(
      InprogressOverseas(
        dob = Some(DOB(year = twentyYearsAgo, month = 1, day = 1)),
        dateLeftUk = Some(DateLeft(year = fiveYearsAgo, month = 1)),
        parentsAddress = Some(PartialAddress(
          addressLine = Some("123 Fake Street"),
          postcode = "AB12 34DC",
          uprn = Some("12345678"),
          manualAddress = None
        ))
      )
    )
    val confirmation = new ConfirmationBlocks(partialApplication, overseas)
    val model = confirmation.parentsAddress

    model.title should be("Parent's or guardian's last UK address")
    model.editLink should be("/register-to-vote/overseas/edit/parents-address/select")
    model.changeName should be("your parent's or guardian's last UK address")
    model.content should be(BlockContent(List("123 Fake Street", "AB12 34DC")))
  }

  it should "return parents manual address" in {
    val twentyYearsAgo = new DateTime().minusYears(20).getYear()
    val fiveYearsAgo = new DateTime().minusYears(5).getYear()

    val partialApplication = confirmationForm.fillAndValidate(
      InprogressOverseas(
        dob = Some(DOB(year = twentyYearsAgo, month = 1, day = 1)),
        dateLeftUk = Some(DateLeft(year = fiveYearsAgo, month = 1)),
        parentsAddress = Some(PartialAddress(
          addressLine = None,
          postcode = "AB12 34DC",
          uprn = None,
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")))
        ))
      )
    )
    val confirmation = new ConfirmationBlocks(partialApplication, overseas)
    val model = confirmation.parentsAddress

    model.title should be("Parent's or guardian's last UK address")
    model.editLink should be("/register-to-vote/overseas/edit/parents-address/manual")
    model.changeName should be("your parent's or guardian's last UK address")
    model.content should be(BlockContent(List(
      "Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester",
      "AB12 34DC")))
  }

  it should "return 'complete this step'" in {

    val partialApplication = confirmationForm.fillAndValidate(
      incompleteYoungApplication.copy(
        parentsAddress = Some(PartialAddress(
          addressLine = None,
          postcode = "AB12 34DC",
          uprn = None,
          manualAddress = None
        ))
      )
    )
    val confirmation = new ConfirmationBlocks(partialApplication, overseas)
    val model = confirmation.parentsAddress

    model.title should be("Parent's or guardian's last UK address")
    model.editLink should be("/register-to-vote/overseas/edit/parents-address")
    model.changeName should be("your parent's or guardian's last UK address")
    model.content should be(BlockError("Please complete this step"))
  }
}

