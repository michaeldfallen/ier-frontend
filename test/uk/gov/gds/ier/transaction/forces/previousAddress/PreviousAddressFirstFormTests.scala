package uk.gov.gds.ier.transaction.forces.previousAddress

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.transaction.forces.address.AddressForms
import uk.gov.gds.ier.model.MovedHouseOption

class PreviousAddressFirstFormTests
  extends FormTestSuite
  with AddressForms
  with PreviousAddressFirstForms {

  it should "error out on empty input" in {
    val js = JsNull
    previousAddressFirstForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("previousAddress.movedRecently") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing values in input" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> ""
      )
    )
    previousAddressFirstForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("previousAddress.movedRecently") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "successfully bind when user has previous address" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "yes"
      )
    )
    previousAddressFirstForm.bind(js).fold(
      hasErrors => {
        fail("Binding failed with " + hasErrors.errorsAsTextAll)
      },
      success => {
        success.previousAddress.flatMap(_.movedRecently) should be(Some(MovedHouseOption.Yes))
      }
    )
  }

  it should "successfully bind when user does not has previous address" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "no"
      )
    )
    previousAddressFirstForm.bind(js).fold(
      hasErrors => {
        fail("Binding failed with " + hasErrors.errorsAsTextAll)
      },
      success => {
        success.previousAddress.flatMap(_.movedRecently) should be(Some(MovedHouseOption.NotMoved))
      }
    )
  }
}
