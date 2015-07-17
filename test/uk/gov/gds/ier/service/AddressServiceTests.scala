package uk.gov.gds.ier.service

import uk.gov.gds.ier.test.{WithMockConfig, MockingTestSuite}
import uk.gov.gds.ier.model.{PartialManualAddress, Address, PartialAddress}
import uk.gov.gds.ier.config.Config

class AddressServiceTests extends MockingTestSuite with WithMockConfig {

  behavior of "AddressService.formFullAddress"

  it should "perform a lookup against Locate Service when a uprn is provided" in {
    val mockLocateService = mock[LocateService]
    val service = getAddressService(mockLocateService)
    val partial = PartialAddress(
      addressLine = None,
      uprn = Some("12345"),
      postcode = "AB12 3CD",
      manualAddress = None,
      gssCode = Some("abc"))

    when(mockLocateService.lookupAddress("AB12 3CD")).thenReturn(List.empty)
    service.formFullAddress(Some(partial))
    verify(mockLocateService).lookupAddress("AB12 3CD")
  }

  it should "pick the correct address out of the returned list" in {
    val mockLocateService = mock[LocateService]
    val service = getAddressService(mockLocateService)
    val partial = PartialAddress(
      addressLine = None,
      uprn = Some("12345"),
      postcode = "AB12 3CD",
      manualAddress = None,
      gssCode = Some("abc")
    )
    val address = Address(
      lineOne = Some("123 Fake Street"),
      lineTwo = None,
      lineThree = None,
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345"),
      postcode = "AB12 3CD",
      gssCode = Some("abc")
    )

    when(mockLocateService.lookupAddress("AB12 3CD")).thenReturn(List(address))
    service.formFullAddress(Some(partial)) should be(Some(address))
    verify(mockLocateService).lookupAddress("AB12 3CD")
  }

  it should "provide a manual address formed when no uprn provided " +
    "and ensure that gssCode is present in full address" in {
    val mockLocateService = mock[LocateService]

    when(mockLocateService.lookupGssCode("AB12 3CD")).thenReturn(Some("E09000007"))

    val addressService = getAddressService(mockLocateService)
    val manualAddress = PartialAddress(
      addressLine = None,
      uprn = None,
      postcode = "AB12 3CD",
      gssCode = None,
      manualAddress = Some(PartialManualAddress(
        lineOne = Some("123 Fake Street"),
        city = Some("Fakerton")))
    )
    val expectedFullAddressResult = Address(
      lineOne = Some("123 Fake Street"),
      lineTwo = None,
      lineThree = None,
      city = Some("Fakerton"),
      county = None,
      uprn = None,
      postcode = "AB12 3CD",
      gssCode = Some("E09000007")
    )

    val fullAddress = addressService.formFullAddress(Some(manualAddress))

    fullAddress should be(Some(expectedFullAddressResult))
  }

  it should "return none if no partial provided" in {
    val mockLocateService = mock[LocateService]
    val service = getAddressService(mockLocateService)

    service.formFullAddress(None) should be(None)
    verify(mockLocateService, never()).lookupAddress(anyString())
  }

  it should "provide correct address containing only a postcode (NI case)" in {
    val mockLocateService = mock[LocateService]
    val service = getAddressService(mockLocateService)
    val partial = PartialAddress(
      addressLine = None,
      uprn = None,
      postcode = "BT7 1AA",
      manualAddress = None,
      gssCode = None
    )
    val address = Address(
      lineOne = None,
      lineTwo = None,
      lineThree = None,
      city = None,
      county = None,
      uprn = None,
      postcode = "BT7 1AA",
      gssCode = None
    )

    service.formFullAddress(Some(partial)) should be(Some(address))
    verify(mockLocateService, never()).lookupAddress("BT7 1AA")
  }

  behavior of "AddressService.formAddressLine"

  it should "combine the 3 lines correctly" in {
    val mockLocateService = mock[LocateService]
    val service = getAddressService(mockLocateService)

    val address = Address(
      lineOne = Some("1A Fake Flat"),
      lineTwo = Some("Fake House"),
      lineThree = Some("123 Fake Street"),
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      gssCode = Some("abc"))

    service.formAddressLine(address) should be(
      "1A Fake Flat, Fake House, 123 Fake Street, Fakerton, Fakesbury"
    )
  }

  it should "filter out Nones" in {
    val mockLocateService = mock[LocateService]
    val service = getAddressService(mockLocateService)

    val address = Address(
      lineOne = Some("1A Fake Flat"),
      lineTwo = None,
      lineThree = None,
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD")

    service.formAddressLine(address) should be(
      "1A Fake Flat, Fakerton, Fakesbury"
    )
  }


  it should "filter out empty strings" in {
    val mockLocateService = mock[LocateService]
    val service = getAddressService(mockLocateService)

    val address = Address(
      lineOne = Some("1A Fake Flat"),
      lineTwo = Some(""),
      lineThree = Some(""),
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD")

    service.formAddressLine(address) should be(
      "1A Fake Flat, Fakerton, Fakesbury"
    )
  }

  it should "return the PartialAddress with gssCode after partial address lookup" in {
    val mockLocateService = mock[LocateService]
    val service = getAddressService(mockLocateService)

    val partial = PartialAddress(
      addressLine = None,
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None)

    val address = Address(
      lineOne = Some("1A Fake Flat"),
      lineTwo = Some(""),
      lineThree = Some(""),
      city = Some("Fakerton"),
      county = Some("Fakesbury"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      gssCode = Some("gss"))

    when(mockLocateService.lookupAddress(partial)).thenReturn(Some(address))

    val result = partial.copy (addressLine = Some("1A Fake Flat, Fakerton, Fakesbury"),
        gssCode = Some("gss"))

    service.fillAddressLine(partial) should be(result)
  }

  behavior of "isScotland with availableForScotland flag: true"
  it should behave like addressServiceWith(availableForScotlandFlag = true)

  behavior of "isScotland with availableForScotland flag: false"
  it should behave like addressServiceWith(availableForScotlandFlag = false)

  def addressServiceWith(availableForScotlandFlag: Boolean) = {
    val mockLocateService = mock[LocateService]
    val addressService = getAddressService(mockLocateService)

    it should "return false for an address with english postcode" in {
      val englishGssCode = Some("E998989654")

      when(config.availableForScotland).thenReturn(availableForScotlandFlag)
      when(mockLocateService.lookupGssCode("AAA22 1AA")).thenReturn(englishGssCode)

      addressService.isScotland(postcode = "AAA22 1AA") should be(false)
    }

    it should "return false for an address with postcode returning empty list" in {
      when(config.availableForScotland).thenReturn(availableForScotlandFlag)
      when(mockLocateService.lookupGssCode("CCC33 3CC")).thenReturn(None)

      addressService.isScotland("CCC33 3CC") should be(false)
    }

    val expectedResult = if (availableForScotlandFlag) false else true
    it should s"return $expectedResult for an address with scottish postcode" in {
      val scottishGssCode = Some("S123456789")

      when(config.availableForScotland).thenReturn(availableForScotlandFlag)
      when(mockLocateService.lookupGssCode("BBB11 2BB")).thenReturn(scottishGssCode)

      addressService.isScotland(postcode = "BBB11 2BB") should be(expectedResult)
    }
  }

  behavior of "isNorthernIreland"
  it should "positively identify Northern Irish post code" in {
    val mockLocateService = mock[LocateService]
    val addressService = getAddressService(mockLocateService)

    addressService.isNothernIreland(postcode = "BT7 1AA") should be(true)
    addressService.isNothernIreland(postcode = "bt71aa") should be(true)
    addressService.isNothernIreland(postcode = "  BT7 1AA  ") should be(true)
    addressService.isNothernIreland(postcode = "   bt71aa ") should be(true)

    addressService.isNothernIreland(postcode = "SW1 E34") should be(false)
    addressService.isNothernIreland(postcode = "NU2 6UN") should be(false)
    addressService.isNothernIreland(postcode = "ABC DEF") should be(false)
    addressService.isNothernIreland(postcode = "ABCDEF") should be(false)
    addressService.isNothernIreland(postcode = "abcdef") should be(false)
  }

  behavior of "AddressService.validAuthority"
  it should "return false for no postcode" in {
    val mockLocateService = mock[LocateService]
    val addressService = getAddressService(mockLocateService)

    addressService.validAuthority(None) should be(false)
  }

  it should "return false when no authority is found" in {
    val mockLocateService = mock[LocateService]
    val addressService = getAddressService(mockLocateService)

    when(mockLocateService.lookupGssCode("AB12 3CD")).thenReturn(None)

    addressService.validAuthority(Some("AB12 3CD")) should be(false)
  }

  it should "return true when authority is found" in {
    val mockLocateService = mock[LocateService]
    val addressService = getAddressService(mockLocateService)

    when(mockLocateService.lookupGssCode("AB12 3CD")).thenReturn(
      Some("A010000010")
    )

    addressService.validAuthority(Some("AB12 3CD")) should be(true)
  }

  private def getAddressService(locateService: LocateService = mock[LocateService], config: Config = config) = {
    new AddressService(locateService, config)
  }

  class MockConfig extends Config {
    override def locateUrl = "http://locate/addresses"
    override def locateAuthorityUrl = "http://locate/authority"
    override def locateApiAuthorizationToken = "abc"
  }
}
