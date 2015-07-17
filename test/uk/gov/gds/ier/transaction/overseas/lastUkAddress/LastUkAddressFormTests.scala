package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model.{PartialManualAddress, Addresses, Address, PartialAddress}

class LastUkAddressFormTests
  extends FormTestSuite
  with LastUkAddressForms {

  behavior of "LastUkAddressForms.lastUkAddressForm"

  it should "successfully bind a valid address" in {
    val js = Json.toJson(
      Map(
        "lastUkAddress.uprn" -> "12345678",
        "lastUkAddress.postcode" -> "SW1A1AA"
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.lastUkAddress.isDefined should be(true)
        val lastUkAddress = success.lastUkAddress.get
        lastUkAddress.uprn should be(Some("12345678"))
        lastUkAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "successfully bind a valid manual input address" in {
    val js = Json.toJson(
      Map(
        "lastUkAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "lastUkAddress.manualAddress.lineTwo" -> "Moseley Road",
        "lastUkAddress.manualAddress.lineThree" -> "Hallow",
        "lastUkAddress.manualAddress.city" -> "Worcester",
        "lastUkAddress.postcode" -> "SW1A1AA"
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.lastUkAddress.isDefined should be(true)
        val lastUkAddress = success.lastUkAddress.get
        lastUkAddress.manualAddress should be(Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester"))))
        lastUkAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    lastUkAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("lastUkAddress") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }


  it should "error out on empty values" in {
    val js =  Json.toJson(
      Map(
        "lastUkAddress.address" -> "",
        "lastUkAddress.postcode" -> ""
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("lastUkAddress") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty values in manual address" in {
    val js =  Json.toJson(
      Map(
        "lastUkAddress.manualAddress" -> "",
        "lastUkAddress.postcode" -> ""
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("lastUkAddress") should be(Seq("Please answer this question"))
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
        "lastUkAddress.uprn" -> "12345678",
        "lastUkAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.lastUkAddress.isDefined should be(true)
        val Some(lastUkAddress) = success.lastUkAddress

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        lastUkAddress.uprn should be(Some("12345678"))
        lastUkAddress.postcode should be("SW1A 1AA")

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
        "lastUkAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("lastUkAddress.uprn") should be(
          Seq("Please select your address")
        )
        hasErrors.errorMessages("lastUkAddress.manualAddress") should be(
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
        "lastUkAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "lastUkAddress.manualAddress.lineTwo" -> "Moseley Road",
        "lastUkAddress.manualAddress.lineThree" -> "Hallow",
        "lastUkAddress.manualAddress.city" -> "Worcester",
        "lastUkAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => fail("Should not fail"),
      success => {
        success.lastUkAddress.isDefined should be(true)
        val Some(lastUkAddress) = success.lastUkAddress

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        lastUkAddress.manualAddress should be(Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester"))))
        lastUkAddress.postcode should be("SW1A 1AA")

        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "not error out with empty text" in {
    val js = Json.toJson(
      Map(
        "lastUkAddress.uprn" -> "87654321",
        "lastUkAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> "",
        "possibleAddresses.postcode" -> ""
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.lastUkAddress.isDefined should be(true)
        val Some(lastUkAddress) = success.lastUkAddress

        success.possibleAddresses.isDefined should be(false)

        lastUkAddress.uprn should be(Some("87654321"))
        lastUkAddress.postcode should be("SW1A 1AA")
      }
    )
  }

  behavior of "LastUkAddressForms.lookupForm"

  it should "succeed on valid postcode" in {
    val js = Json.toJson(
      Map(
        "lastUkAddress.postcode" -> "SW1A 1AA"
      )
    )

    lookupAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.lastUkAddress.isDefined should be(true)
        val Some(lastUkAddress) = success.lastUkAddress

        lastUkAddress.postcode should be("SW1A 1AA")
        lastUkAddress.uprn should be(None)
        lastUkAddress.manualAddress should be(None)
        lastUkAddress.addressLine should be (None)
      }
    )
  }

  it should "fail out on no postcode" in {
    val js = Json.toJson(Map("lastUkAddress.postcode" -> ""))

    lookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("lastUkAddress.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }

  it should "fail out on empty json" in {
    val js = JsNull

    lookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("lastUkAddress.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }

  it should "fail out on missing values" in {
    val js = Json.toJson(Map("" -> ""))

    lookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("lastUkAddress.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }

  behavior of "LastUkAddressForms.manualAddressForm"

  it should "succeed on valid input" in {
    val js = Json.toJson(
      Map(
        "lastUkAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "lastUkAddress.manualAddress.lineTwo" -> "Moseley Road",
        "lastUkAddress.manualAddress.lineThree" -> "Hallow",
        "lastUkAddress.manualAddress.city" -> "Worcester",
        "lastUkAddress.postcode" -> "SW1A1AA"
      )
    )
    manualAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.lastUkAddress.isDefined should be(true)
        val lastUkAddress = success.lastUkAddress.get
        lastUkAddress.manualAddress should be(Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester"))))
        lastUkAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "error out on empty json for manual address" in {
    val js =  JsNull

    manualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("lastUkAddress") should be(
          Seq("Please answer this question")
        )
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on all empty values for manual address" in {
    val js =  Json.toJson(
      Map(
        "lastUkAddress.manualAddress.lineOne" -> "",
        "lastUkAddress.manualAddress.lineTwo" -> "",
        "lastUkAddress.manualAddress.lineThree" -> "",
        "lastUkAddress.manualAddress.city" -> "",
        "lastUkAddress.postcode" -> "SW1A 1AA"
      )
    )
    manualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("lastUkAddress") should be(
          Seq("Please answer this question")
        )
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on all empty lines for manual address" in {
    val js =  Json.toJson(Map(
      "lastUkAddress.manualAddress.lineOne" -> "",
      "lastUkAddress.manualAddress.lineTwo" -> "",
      "lastUkAddress.manualAddress.lineThree" -> "",
      "lastUkAddress.manualAddress.city" -> "Worcester",
      "lastUkAddress.postcode" -> "SW1A 1AA"
    ))

    manualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "lastUkAddress.manualAddress" -> Seq("At least one address line is required")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind when lineOne is not empty" in {
    val js = Json.toJson(Map(
      "lastUkAddress.manualAddress.lineOne" -> "line one",
      "lastUkAddress.manualAddress.lineTwo" -> "",
      "lastUkAddress.manualAddress.lineThree" -> "",
      "lastUkAddress.manualAddress.city" -> "Worcester",
      "lastUkAddress.postcode" -> "SW1A1AA"
    ))

    manualAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        val Some(lastUkAddress) = success.lastUkAddress
        val Some(manualAddress) = lastUkAddress.manualAddress

        manualAddress should be(PartialManualAddress(
          lineOne = Some("line one"),
          lineTwo = None,
          lineThree = None,
          city = Some("Worcester")
        ))

        lastUkAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "successfully bind when lineTwo is not empty" in {
    val js = Json.toJson(Map(
      "lastUkAddress.manualAddress.lineOne" -> "",
      "lastUkAddress.manualAddress.lineTwo" -> "line two",
      "lastUkAddress.manualAddress.lineThree" -> "",
      "lastUkAddress.manualAddress.city" -> "Worcester",
      "lastUkAddress.postcode" -> "SW1A1AA"
    ))

    manualAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        val Some(lastUkAddress) = success.lastUkAddress
        val Some(manualAddress) = lastUkAddress.manualAddress

        manualAddress should be(PartialManualAddress(
          lineOne = None,
          lineTwo = Some("line two"),
          lineThree = None,
          city = Some("Worcester")
        ))

        lastUkAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "successfully bind when lineThree is not empty" in {
    val js = Json.toJson(Map(
      "lastUkAddress.manualAddress.lineOne" -> "",
      "lastUkAddress.manualAddress.lineTwo" -> "",
      "lastUkAddress.manualAddress.lineThree" -> "line three",
      "lastUkAddress.manualAddress.city" -> "Worcester",
      "lastUkAddress.postcode" -> "SW1A1AA"
    ))

    manualAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        val Some(lastUkAddress) = success.lastUkAddress
        val Some(manualAddress) = lastUkAddress.manualAddress

        manualAddress should be(PartialManualAddress(
          lineOne = None,
          lineTwo = None,
          lineThree = Some("line three"),
          city = Some("Worcester")
        ))

        lastUkAddress.postcode should be("SW1A1AA")
      }
    )
  }

}
