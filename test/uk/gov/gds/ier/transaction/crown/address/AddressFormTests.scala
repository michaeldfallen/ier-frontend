package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model.{PartialManualAddress, Addresses, PartialAddress}

class AddressFormTests
  extends FormTestSuite
  with AddressForms {

  behavior of "AddressForms.addressForm"

  it should "successfully bind a valid address" in {
    val js = Json.toJson(
      Map(
        "address.uprn" -> "12345678",
        "address.postcode" -> "SW1A1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.address.isDefined should be(true)
        val lastUkAddress = success.address
        lastUkAddress.isDefined should be(true)

        val address = lastUkAddress.get.address
        address.isDefined should be(true)
        address.get.uprn should be(Some("12345678"))
        address.get.postcode should be("SW1A1AA")

      }
    )
  }

  it should "successfully bind a valid manual input address" in {
    val js = Json.toJson(
      Map(
        "address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "address.manualAddress.lineTwo" -> "Moseley Road",
        "address.manualAddress.lineThree" -> "Hallow",
        "address.manualAddress.city" -> "Worcester",
        "address.postcode" -> "SW1A1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {

        success.address.isDefined should be(true)
        val lastUkAddress = success.address
        lastUkAddress.isDefined should be(true)
        val address = lastUkAddress.get.address
        address.isDefined should be(true)

        address.get.manualAddress should be(Some(
          PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")
          ))
        )
        address.get.postcode should be("SW1A1AA")
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("address") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }


  it should "error out on empty values" in {
    val js =  Json.toJson(
      Map(
        "address.address" -> "",
        "address.postcode" -> ""
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("address") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty values in manual address" in {
    val js =  Json.toJson(
      Map(
        "address.manualAddress" -> "",
        "address.postcode" -> ""
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("address") should be(Seq("Please answer this question"))
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
        "address.uprn" -> "12345678",
        "address.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.address.isDefined should be(true)
        val Some(address) = success.address

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        success.address.isDefined should be(true)
        val lastUkAddress = success.address
        lastUkAddress.isDefined should be(true)
        if (lastUkAddress.isDefined) {
          val address = lastUkAddress.get.address
          address.isDefined should be(true)
          address.get.uprn should be(Some("12345678"))
          address.get.postcode should be("SW1A 1AA")
        }

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
        "address.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("address.uprn") should be(
          Seq("Please select your address")
        )
        hasErrors.errorMessages("address.manualAddress") should be(
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
        "address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "address.manualAddress.lineTwo" -> "Moseley Road",
        "address.manualAddress.lineThree" -> "Hallow",
        "address.manualAddress.city" -> "Worcester",
        "address.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail("Should not fail"),
      success => {
        success.address.isDefined should be(true)
        val Some(address) = success.address

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        success.address.isDefined should be(true)
        val lastUkAddress = success.address
        lastUkAddress.isDefined should be(true)
        if (lastUkAddress.isDefined) {

          val address = lastUkAddress.get.address
          address.isDefined should be(true)

          address.get.manualAddress should be(Some(
            PartialManualAddress(
              lineOne = Some("Unit 4, Elgar Business Centre"),
              lineTwo = Some("Moseley Road"),
              lineThree = Some("Hallow"),
              city = Some("Worcester")
            ))
          )
          address.get.postcode should be("SW1A 1AA")

        }

        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "not error out with empty text" in {
    val js = Json.toJson(
      Map(
        "address.uprn" -> "87654321",
        "address.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> "",
        "possibleAddresses.postcode" -> ""
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.address.isDefined should be(true)
        val Some(address) = success.address

        success.possibleAddresses.isDefined should be(false)

        success.address.isDefined should be(true)
        val lastUkAddress = success.address
        lastUkAddress.isDefined should be(true)
        if (lastUkAddress.isDefined) {
          val address = lastUkAddress.get.address
          address.isDefined should be(true)
          address.get.uprn should be(Some("87654321"))
          address.get.postcode should be("SW1A 1AA")
        }

      }
    )
  }

  behavior of "AddressForms.lookupForm"

  it should "succeed on valid postcode" in {
    val js = Json.toJson(
      Map(
        "address.postcode" -> "SW1A 1AA"
      )
    )

    lookupAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {

        success.address.isDefined should be(true)
        val lastUkAddress = success.address
        lastUkAddress.isDefined should be(true)
        if (lastUkAddress.isDefined) {
          val address = lastUkAddress.get.address
          address.isDefined should be(true)
          address.get.postcode should be("SW1A 1AA")
          address.get.uprn should be(None)
          address.get.manualAddress should be(None)
          address.get.addressLine should be (None)
        }

      }
    )
  }

  it should "fail out on no postcode" in {
    val js = Json.toJson(Map("address.postcode" -> ""))

    lookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("address.postcode") should be(
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
        hasErrors.errorMessages("address.postcode") should be(
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
        hasErrors.errorMessages("address.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }

  behavior of "AddressForms.manualAddressForm"

  it should "succeed on valid input" in {
    val js = Json.toJson(
      Map(
        "address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
        "address.manualAddress.lineTwo" -> "Moseley Road",
        "address.manualAddress.lineThree" -> "Hallow",
        "address.manualAddress.city" -> "Worcester",
        "address.postcode" -> "SW1A1AA"
      )
    )
    manualAddressForm.bind(js).fold(
      hasErrors => fail(hasErrors.errorsAsTextAll),
      success => {
        success.address.isDefined should be(true)
        val lastUkAddress = success.address
        lastUkAddress.isDefined should be(true)

        val address = lastUkAddress.get.address
        address.isDefined should be(true)

        address.get.manualAddress should be(Some(
          PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")
          ))
        )
        address.get.postcode should be("SW1A1AA")

      }
    )
  }

  it should "error out on empty values for manual address" in {
    val js =  Json.toJson(
      Map(
        "address.manualAddress" -> "",
        "address.postcode" -> "SW1A 1AA"
      )
    )
    manualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("address") should be(
          Seq("Please answer this question")
        )
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty json for manual address" in {
    val js =  JsNull

    manualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("address") should be(
          Seq("Please answer this question")
        )
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on all empty values for manual address" in {
    val js =  Json.toJson(Map(
      "address.manualAddress.lineOne" -> "",
      "address.manualAddress.lineTwo" -> "",
      "address.manualAddress.lineThree" -> "",
      "address.manualAddress.city" -> "",
      "address.postcode" -> "SW1A 1AA"
    ))

    manualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "address" -> Seq("Please answer this question")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on all empty lines for manual address" in {
    val js =  Json.toJson(Map(
      "address.manualAddress.lineOne" -> "",
      "address.manualAddress.lineTwo" -> "",
      "address.manualAddress.lineThree" -> "",
      "address.manualAddress.city" -> "Worcester",
      "address.postcode" -> "SW1A 1AA"
    ))

    manualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "address.manualAddress" -> Seq("At least one address line is required")
        ))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind when lineOne is not empty" in {
    val js = Json.toJson(Map(
      "address.manualAddress.lineOne" -> "line one",
      "address.manualAddress.lineTwo" -> "",
      "address.manualAddress.lineThree" -> "",
      "address.manualAddress.city" -> "Worcester",
      "address.postcode" -> "SW1A1AA"
    ))

    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        val Some(lastUkAddress) = success.address
        val Some(partialAddress) = lastUkAddress.address
        val Some(manualAddress) = partialAddress.manualAddress

        manualAddress should be(PartialManualAddress(
          lineOne = Some("line one"),
          lineTwo = None,
          lineThree = None,
          city = Some("Worcester")
        ))

        partialAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "successfully bind when lineTwo is not empty" in {
    val js = Json.toJson(Map(
      "address.manualAddress.lineOne" -> "",
      "address.manualAddress.lineTwo" -> "line two",
      "address.manualAddress.lineThree" -> "",
      "address.manualAddress.city" -> "Worcester",
      "address.postcode" -> "SW1A1AA"
    ))

    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        val Some(lastUkAddress) = success.address
        val Some(partialAddress) = lastUkAddress.address
        val Some(manualAddress) = partialAddress.manualAddress

        manualAddress should be(PartialManualAddress(
          lineOne = None,
          lineTwo = Some("line two"),
          lineThree = None,
          city = Some("Worcester")
        ))

        partialAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "successfully bind when lineThree is not empty" in {
    val js = Json.toJson(Map(
      "address.manualAddress.lineOne" -> "",
      "address.manualAddress.lineTwo" -> "",
      "address.manualAddress.lineThree" -> "line three",
      "address.manualAddress.city" -> "Worcester",
      "address.postcode" -> "SW1A1AA"
    ))

    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        val Some(lastUkAddress) = success.address
        val Some(partialAddress) = lastUkAddress.address
        val Some(manualAddress) = partialAddress.manualAddress

        manualAddress should be(PartialManualAddress(
          lineOne = None,
          lineTwo = None,
          lineThree = Some("line three"),
          city = Some("Worcester")
        ))

        partialAddress.postcode should be("SW1A1AA")
      }
    )
  }

}
