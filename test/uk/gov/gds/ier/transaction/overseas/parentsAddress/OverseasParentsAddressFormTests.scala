package uk.gov.gds.ier.transaction.overseas.parentsAddress

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model.{PartialManualAddress, Addresses, PartialAddress}

class OverseasParentsAddressFormTests
  extends FormTestSuite
  with ParentsAddressForms {

  behavior of "parentsAddressForms.parentsAddressForm"

  it should "successfully bind a valid address" in {
    val js = Json.toJson(
      Map(
        "parentsAddress.uprn" -> "12345678",
        "parentsAddress.postcode" -> "SW1A1AA"
      )
    )
    parentsAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.parentsAddress.isDefined should be(true)
        val parentsAddress = success.parentsAddress.get
        parentsAddress.uprn should be(Some("12345678"))
        parentsAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "successfully bind a valid manual input address" in {
    val js = Json.toJson(
      Map(
        "parentsAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "parentsAddress.manualAddress.lineTwo" -> "Moseley Road",
        "parentsAddress.manualAddress.lineThree" -> "Hallow",
        "parentsAddress.manualAddress.city" -> "Worcester",
        "parentsAddress.postcode" -> "SW1A1AA"
      )
    )
    parentsAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.parentsAddress.isDefined should be(true)
        val parentsAddress = success.parentsAddress.get
        parentsAddress.manualAddress should be(Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester"))))
        parentsAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    parentsAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("parentsAddress") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }


  it should "error out on empty values" in {
    val js =  Json.toJson(
      Map(
        "parentsAddress.address" -> "",
        "parentsAddress.postcode" -> ""
      )
    )
    parentsAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("parentsAddress") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty values in manual address" in {
    val js =  Json.toJson(
      Map(
        "parentsAddress.manualAddress" -> "",
        "parentsAddress.postcode" -> ""
      )
    )
    parentsAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("parentsAddress") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind possible Address list" in {
    val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"),
                                         uprn = Some("12345678"),
                                         postcode = "AB12 3CD",
                                         manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "parentsAddress.uprn" -> "12345678",
        "parentsAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    parentsAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.parentsAddress.isDefined should be(true)
        val Some(parentsAddress) = success.parentsAddress

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        parentsAddress.uprn should be(Some("12345678"))
        parentsAddress.postcode should be("SW1A 1AA")

        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "error out if it looks like you haven't selected your address" in {
    val possibleAddress = PartialAddress(
      addressLine = Some("123 Fake Street"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None
    )
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "parentsAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    parentsAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("parentsAddress.uprn") should be(
          Seq("Please select your address")
        )
        hasErrors.errorMessages("parentsAddress.manualAddress") should be(
          Seq("Please select your address")
        )
        hasErrors.globalErrorMessages should be(Seq("Please select your address"))
      },
      success => {
        fail("Should have errored out")
      }
    )
  }

  it should "not error if you haven't selected your address but there is a manual address" in {
    val possibleAddress = PartialAddress(
      addressLine = Some("123 Fake Street"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None
    )
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "parentsAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "parentsAddress.manualAddress.lineTwo" -> "Moseley Road",
        "parentsAddress.manualAddress.lineThree" -> "Hallow",
        "parentsAddress.manualAddress.city" -> "Worcester",
        "parentsAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    parentsAddressForm.bind(js).fold(
      hasErrors => fail("Should not fail"),
      success => {
        success.parentsAddress.isDefined should be(true)
        val Some(parentsAddress) = success.parentsAddress

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        parentsAddress.manualAddress should be(Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester"))))
        parentsAddress.postcode should be("SW1A 1AA")

        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "not error out with empty text" in {
    val js = Json.toJson(
      Map(
        "parentsAddress.uprn" -> "87654321",
        "parentsAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> "",
        "possibleAddresses.postcode" -> ""
      )
    )
    parentsAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.parentsAddress.isDefined should be(true)
        val Some(parentsAddress) = success.parentsAddress

        success.possibleAddresses.isDefined should be(false)

        parentsAddress.uprn should be(Some("87654321"))
        parentsAddress.postcode should be("SW1A 1AA")
      }
    )
  }

  behavior of "parentsAddressForms.lookupForm"

  it should "succeed on valid postcode" in {
    val js = Json.toJson(
      Map(
        "parentsAddress.postcode" -> "SW1A 1AA"
      )
    )

    parentsLookupAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.parentsAddress.isDefined should be(true)
        val Some(parentsAddress) = success.parentsAddress

        parentsAddress.postcode should be("SW1A 1AA")
        parentsAddress.uprn should be(None)
        parentsAddress.manualAddress should be(None)
        parentsAddress.addressLine should be (None)
      }
    )
  }

  it should "fail out on no postcode" in {
    val js = Json.toJson(Map("parentsAddress.postcode" -> ""))

    parentsLookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("parentsAddress.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }

  it should "fail out on empty json" in {
    val js = JsNull

    parentsLookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("parentsAddress.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }

  it should "fail out on missing values" in {
    val js = Json.toJson(Map("" -> ""))

    parentsLookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("parentsAddress.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }

  behavior of "parentsAddressForms.manualAddressForm"

  it should "succeed on valid input" in {
    val js = Json.toJson(
      Map(
        "parentsAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "parentsAddress.manualAddress.lineTwo" -> "Moseley Road",
        "parentsAddress.manualAddress.lineThree" -> "Hallow",
        "parentsAddress.manualAddress.city" -> "Worcester",
        "parentsAddress.postcode" -> "SW1A1AA"
      )
    )
    parentsManualAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.parentsAddress.isDefined should be(true)
        val parentsAddress = success.parentsAddress.get
        parentsAddress.manualAddress should be(Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester"))))
        parentsAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "error out on empty values for manual address" in {
    val js =  Json.toJson(
      Map(
        "parentsAddress.manualAddress.lineOne" -> "",
        "parentsAddress.manualAddress.lineTwo" -> "",
        "parentsAddress.manualAddress.lineThree" -> "",
        "parentsAddress.manualAddress.city" -> "",
        "parentsAddress.postcode" -> "SW1A 1AA"
      )
    )
    parentsManualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("parentsAddress.manualAddress") should be(
          Seq("Please answer this question")
        )
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty json for manual address" in {
    val js =  JsNull

    parentsManualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("parentsAddress.manualAddress") should be(
          Seq("Please answer this question")
        )
      },
      success => fail("Should have errored out")
    )
  }
}
