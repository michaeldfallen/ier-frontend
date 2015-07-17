package uk.gov.gds.ier.transaction.crown.confirmation

import uk.gov.gds.ier.model._
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class ConfirmationFormTests
  extends FormTestSuite
  with ConfirmationForms
  with WithMockAddressService {

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
        hasErrors.errorMessages("job") should be(errorMessage)
        hasErrors.errorMessages("contactAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(12)
        hasErrors.errors.size should be(24)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on empty application" in {
    val application = InprogressCrown()
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
        hasErrors.errorMessages("job") should be(errorMessage)
        hasErrors.errorMessages("contactAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(12)
        hasErrors.errors.size should be(24)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "succeed on waysToVote if postalOrProxy filled (InPerson)" in {
    val application = completeCrownApplication.copy(
      waysToVote = Some(WaysToVote(WaysToVoteType.InPerson)),
      postalOrProxyVote = None
    )
    confirmationForm.fillAndValidate(application).hasErrors should be(false)
  }

  it should "succeed on waysToVote if postalOrProxy filled (ByPost)" in {
    val application = completeCrownApplication.copy(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    )
    confirmationForm.fillAndValidate(application).hasErrors should be(false)
  }

  it should "succeed on waysToVote if postalOrProxy filled (ByProxy)" in {
    val application = completeCrownApplication.copy(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    )
    confirmationForm.fillAndValidate(application).hasErrors should be(false)
  }

  it should "error out on waysToVote if postalOrProxy not filled (ByPost)" in {
    val application = completeCrownApplication.copy(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
      postalOrProxyVote = None
    )
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on waysToVote if postalOrProxy not filled (ByProxy)" in {
    val application = completeCrownApplication.copy(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
      postalOrProxyVote = None
    )
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "succeed on hasAddress = true and existing previousAddress" in {
    val application = completeCrownApplication.copy(
      address = Some(LastAddress(
        Some(HasAddressOption.YesAndLivingThere),
        Some(PartialAddress(
          Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None))
      )),
      previousAddress = Some(PartialPreviousAddress(Some(MovedHouseOption.NotMoved), None))
    )
    confirmationForm.fillAndValidate(application).hasErrors should be(false)
  }

  it should "succeed on hasAddress = false and missing previousAddress" in {
    val application = completeCrownApplication.copy(
      address = Some(LastAddress(
        Some(HasAddressOption.No),
        Some(PartialAddress(
          Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None))
      )),
      previousAddress = None
    )
    confirmationForm.fillAndValidate(application).hasErrors should be(false)
  }

  it should "error out on hasAddress = true and missing previousAddress" in {
    val application = completeCrownApplication.copy(
      address = Some(LastAddress(
        Some(HasAddressOption.YesAndLivingThere),
        Some(PartialAddress(
          Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None))
      )),
      previousAddress = None
    )
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("previousAddress") should be(errorMessage)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "bind successfully if the previous address postcode was Northern Ireland" in {
    confirmationForm.fillAndValidate(completeCrownApplication.copy(
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
