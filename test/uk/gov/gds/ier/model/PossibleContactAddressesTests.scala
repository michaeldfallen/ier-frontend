package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.UnitTestSuite

class PossibleContactAddressesTests extends UnitTestSuite {

  it should "generate the expected payload with postcode in the correct format" in {
    val possibleAddress = PossibleContactAddresses(
      contactAddressType = Some("uk"),
      ukAddressLine = None,
      bfpoContactAddress = None,
      otherContactAddress = None
    )

    val possibleAddressMap = possibleAddress.toApiMap(Some(Address(" aB1  2Cd "))).asInstanceOf[Map[String, String]]
    possibleAddressMap should matchMap(Map("corrcountry" -> "uk", "corrpostcode" -> "AB1 2CD"))
  }

}
