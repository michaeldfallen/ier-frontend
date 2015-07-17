package uk.gov.gds.ier.transaction.forces.confirmation

import uk.gov.gds.ier.test.{WithMockAddressService, FormTestSuite}
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.forces.InprogressForces

class ConfirmationFormTests
  extends FormTestSuite
  with ConfirmationForms
  with WithMockAddressService{

  it should "error out on empty json" in {
    val js = JsNull
    confirmationForm.bind(js).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("statement") should be(errorMessage)
        hasErrors.errorMessages("address") should be(errorMessage)
        hasErrors.errorMessages("nationality") should be(errorMessage)
        hasErrors.errorMessages("dob") should be(errorMessage)
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("service") should be(errorMessage)
        hasErrors.errorMessages("rank") should be(errorMessage)
        hasErrors.errorMessages("contactAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(13)
        hasErrors.errors.size should be(26)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on empty application" in {
    val application = InprogressForces()
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("statement") should be(errorMessage)
        hasErrors.errorMessages("address") should be(errorMessage)
        hasErrors.errorMessages("nationality") should be(errorMessage)
        hasErrors.errorMessages("dob") should be(errorMessage)
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("service") should be(errorMessage)
        hasErrors.errorMessages("rank") should be(errorMessage)
        hasErrors.errorMessages("contactAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(13)
        hasErrors.errors.size should be(26)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on waysToVote when waysToVote form is empty" in {
    val errorMessage = Seq("Please complete this step")
    confirmationForm.fillAndValidate(completeForcesApplication.copy(waysToVote = None,
        postalOrProxyVote = None)).fold (
            hasErrors => {
              hasErrors.errorMessages(keys.waysToVote.key) should be(errorMessage)
            },
            success => fail("Should have errored out")
        )
  }
  it should "bind successfully if the waysToVote is either post or proxy and the postal vote step is" +
    "filled in as well" in {
    val errorMessage = Seq("Please complete this step")
    confirmationForm.fillAndValidate(completeForcesApplication.copy(
        waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
        postalOrProxyVote = Some(PostalOrProxyVote(WaysToVoteType.ByPost,Some(true),
          Some(PostalVoteDeliveryMethod(Some("post"),None)))))).fold (
            hasErrors => fail("the form should be valid"),
            success => {
              success.postalOrProxyVote.isDefined
              success.waysToVote.isDefined
            }
        )
  }

  it should "error out on previous address when movedHouse is an invalid option" in {
    confirmationForm.fillAndValidate(completeForcesApplication.copy(
        previousAddress = Some(PartialPreviousAddress(Some(MovedHouseOption.MovedFromUk), None))
     )).fold (
      hasErrors => {
        hasErrors.errorMessages(keys.previousAddress.movedRecently.key) should be(Seq("Not a valid option"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on previous address when previous address has not been filled in" in {
    val errorMessage = Seq("Please complete this step")
    confirmationForm.fillAndValidate(completeForcesApplication.copy(
      previousAddress = Some(PartialPreviousAddress(Some(MovedHouseOption.Yes), None))
    )).fold (
      hasErrors => {
        hasErrors.errorMessages(keys.previousAddress.key) should be(errorMessage)
      },
      success => fail("Should have errored out")
    )
  }

  it should "bind successfully if the previous address postcode was Northern Ireland" in {
    confirmationForm.fillAndValidate(completeForcesApplication.copy(
        previousAddress = Some(PartialPreviousAddress(
          movedRecently = Some(MovedHouseOption.Yes),
          previousAddress = Some(PartialAddress(
            addressLine = None,
            uprn = None,
            postcode = "bt7 1aa",
            manualAddress = None
          ))
        ))
      )).fold (
      hasErrors => {
        fail("the form should be valid")
      },
      success => {
        success.previousAddress.isDefined
      }
    )
  }
}
