package uk.gov.gds.ier.transaction.ordinary.confirmation

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.test.WithMockAddressService

class ConfirmationFormTests
  extends FormTestSuite
  with ConfirmationForms
  with WithMockAddressService {

  it should "error out on empty json" in {
    val js = JsNull
    confirmationForm.bind(js).fold(
      hasErrors => {
        val errorMessage = Seq("ordinary_confirmation_error_completeThis")
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
        hasErrors.errorMessages("dob") should be(errorMessage)
        hasErrors.errorMessages("nationality") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("address") should be(errorMessage)
        hasErrors.errorMessages("otherAddress") should be(errorMessage)
        hasErrors.errorMessages("previousAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("postalVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "ordinary_confirmation_error_completeThis") should be(11)
        hasErrors.errors.size should be(22)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on empty application" in {
    val application = InprogressOrdinary()
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        val errorMessage = Seq("ordinary_confirmation_error_completeThis")
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
        hasErrors.errorMessages("dob") should be(errorMessage)
        hasErrors.errorMessages("nationality") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("address") should be(errorMessage)
        hasErrors.errorMessages("otherAddress") should be(errorMessage)
        hasErrors.errorMessages("previousAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("postalVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "ordinary_confirmation_error_completeThis") should be(11)
        hasErrors.errors.size should be(22)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "bind successfully if the previous address postcode was Northern Ireland" in {
	    confirmationForm.fillAndValidate(completeOrdinaryApplication.copy(
	      previousAddress = Some(PartialPreviousAddress(
	        movedRecently = Some(MovedHouseOption.MovedFromUk),
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

  it should "indicate erros if MovedFromUK but manual address is None" in {
	    confirmationForm.fillAndValidate(completeOrdinaryApplication.copy(
	      previousAddress = Some(PartialPreviousAddress(
	        movedRecently = Some(MovedHouseOption.MovedFromUk),
	        previousAddress = Some(PartialAddress(
	          addressLine = None,
	          uprn = None,
	          postcode = "WC2A 2HD",
	          manualAddress = None
	        ))
	      ))
	    )).fold (
	      hasErrors => {
	        hasErrors.globalErrorMessages should be(Seq("ordinary_confirmation_error_completeThis"))
	      },
	      success => fail("Should have errored out.")
	    )
	  }
}
