package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.test.FormTestSuite
import play.api.libs.json.{JsNull, Json}
import uk.gov.gds.ier.model.HasAddressOption

class AddressFirstFormTests
  extends FormTestSuite
  with AddressForms
  with AddressFirstForms {

  it should "error out on empty input" in {
    assertUnsuccessfulBinding(
      formData = Map.empty,
      expectedErrorMessage = "Please answer this question")
  }

  it should "error out on missing values in input" in {
    assertUnsuccessfulBinding(
      formData = Map("address.hasAddress" -> ""),
      expectedErrorMessage = "Please answer this question")
  }

  it should "successfully bind when user has an address (yes and living there)" in {
    assertSuccessfullBinding(
      formData = Map("address.hasAddress" -> "yes-living-there"),
      expected = HasAddressOption.YesAndLivingThere)
  }

  it should "successfully bind when user has an address (yes and not living there)" in {
    assertSuccessfullBinding(
      formData = Map("address.hasAddress" -> "yes-not-living-there"),
      expected = HasAddressOption.YesAndNotLivingThere)
  }

  it should "successfully bind when user does not has previous address" in {
    assertSuccessfullBinding(
      formData = Map("address.hasAddress" -> "no"),
      expected = HasAddressOption.No)
  }

  def assertSuccessfullBinding(formData:Map[String,String], expected:HasAddressOption) {
    addressFirstForm.bind(Json.toJson(formData)).fold(
      hasErrors => {
        fail("Binding failed with " + hasErrors.errorsAsTextAll)
      },
      success => {
        success.address.flatMap(_.hasAddress) should be(Some(expected))
      }
    )
  }

  def assertUnsuccessfulBinding(formData:Map[String,String], expectedErrorMessage:String) {
    val js = if(formData.isEmpty) JsNull else Json.toJson(formData)

    addressFirstForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "address.hasAddress" -> Seq(expectedErrorMessage)
        ))
      },
      success => fail("Should have errored out.")
    )
  }

}
