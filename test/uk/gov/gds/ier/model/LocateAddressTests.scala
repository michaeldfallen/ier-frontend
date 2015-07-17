package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.UnitTestSuite

class LocateAddressTests extends UnitTestSuite {

  it should "generate the expected payload with lower-cased, no whitespaces postcode" in {
    generateLocateAddressFor(" aB1  2Cd ").toApiMap("reg") should matchMap(Map("regpostcode" -> "ab12cd"))
    generateLocateAddressFor("ab12cd").toApiMap("reg") should matchMap(Map("regpostcode" -> "ab12cd"))
    generateLocateAddressFor("  A  B  12  C  D  ").toApiMap("reg") should matchMap(Map("regpostcode" -> "ab12cd"))
  }

  private def generateLocateAddressFor(postcode:String) = LocateAddress(
    property = None,
    street = None,
    locality = None,
    town = None,
    area = None,
    postcode = postcode,
    uprn = None,
    gssCode = None)

}
