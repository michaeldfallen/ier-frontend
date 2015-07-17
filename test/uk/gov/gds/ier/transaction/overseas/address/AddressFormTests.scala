package uk.gov.gds.ier.transaction.overseas.address

import uk.gov.gds.ier.test.FormTestSuite

class AddressFormTests
  extends FormTestSuite
  with AddressForms {

  it should "error out on empty json" in {
    val js = JsNull
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("overseasAddress.country") should be(Seq("Please enter your country"))
        hasErrors.errorMessages("overseasAddress.addressLine1") should be(Seq("Please enter your address"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your country", "Please enter your address" ))
        hasErrors.errors.size should be(4)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "overseasAddress.country" -> "",
        "overseasAddress.addressLine1" -> ""
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("overseasAddress.country") should be(Seq("Please enter your country"))
        hasErrors.errorMessages("overseasAddress.addressLine1") should be(Seq("Please enter your address"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your country", "Please enter your address" ))
        hasErrors.errors.size should be(4)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing country" in {
    val js = Json.toJson(
      Map(
        "overseasAddress.country" -> "",
        "overseasAddress.addressLine1" -> "some address"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("overseasAddress.country") should be(Seq("Please enter your country"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your country"))
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }
  
  it should "error out on missing address" in {
    val js = Json.toJson(
      Map(
        "overseasAddress.country" -> "United Kingdom",
        "overseasAddress.addressLine1" -> ""
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("overseasAddress.addressLine1") should be(Seq("Please enter your address"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your address" ))
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }
  
  it should "successfully parse" in {
    val js = Json.toJson(
      Map(
        "overseasAddress.country" -> "United Kingdom",
        "overseasAddress.addressLine1" -> "some address"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        fail("Should have errored out.")
      },
      success => {
        val Some(overseasAddress) = success.address
        overseasAddress.country.get should be ("United Kingdom")
      }
    )
  }
}
