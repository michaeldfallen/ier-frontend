package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.transaction.crown.address.AddressForms
import uk.gov.gds.ier.model.MovedHouseOption

class PreviousAddressFirstFormTests
  extends FormTestSuite
  with AddressForms
  with PreviousAddressFirstForms {

  it should "error out on empty input" in {
    assertUnsuccessfulBinding(
      formData = Map.empty,
      expectedErrorMessage = "Please answer this question")
  }

  it should "error out on missing values in input" in {
    assertUnsuccessfulBinding(
      formData = Map("previousAddress.movedRecently" -> ""),
      expectedErrorMessage = "Please answer this question")
  }

  it should "error out on invalid moved house value (from uk)" in {
    assertUnsuccessfulBinding(
      formData = Map("previousAddress.movedRecently" -> "from-uk"),
      expectedErrorMessage = "Not a valid option")
  }

  it should "error out on invalid moved house value (from abroad)" in {
    assertUnsuccessfulBinding(
      formData = Map("previousAddress.movedRecently" -> "from-abroad"),
      expectedErrorMessage = "Not a valid option")
  }

  it should "error out on invalid moved house value (from abroad registered)" in {
    assertUnsuccessfulBinding(
      formData = Map("previousAddress.movedRecently" -> "from-abroad-registered"),
      expectedErrorMessage = "Not a valid option")
  }

  it should "error out on invalid moved house value (from abroad not registered)" in {
    assertUnsuccessfulBinding(
      formData = Map("previousAddress.movedRecently" -> "from-abroad-not-registered"),
      expectedErrorMessage = "Not a valid option")
  }

  it should "successfully bind when user has previous address (yes)" in {
    assertSuccessfullBinding(
      formData = Map("previousAddress.movedRecently" -> "yes"),
      expected = MovedHouseOption.Yes)
  }

  it should "successfully bind when user does not has previous address (not-moved)" in {
    assertSuccessfullBinding(
      formData = Map("previousAddress.movedRecently" -> "no"),
      expected = MovedHouseOption.NotMoved)
  }


  def assertSuccessfullBinding(formData:Map[String,String], expected:MovedHouseOption) {
    previousAddressFirstForm.bind(Json.toJson(formData)).fold(
      hasErrors => {
        fail("Binding failed with " + hasErrors.errorsAsTextAll)
      },
      success => {
        success.previousAddress.flatMap(_.movedRecently) should be(Some(expected))
      }
    )
  }

  def assertUnsuccessfulBinding(formData:Map[String,String], expectedErrorMessage:String) {
    val js = if(formData.isEmpty) JsNull else Json.toJson(formData)

    previousAddressFirstForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.movedRecently" -> Seq(expectedErrorMessage)
        ))
      },
      success => fail("Should have errored out.")
    )
  }

}
