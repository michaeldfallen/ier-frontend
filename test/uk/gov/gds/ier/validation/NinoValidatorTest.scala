package uk.gov.gds.ier.validation

import uk.gov.gds.ier.test.UnitTestSuite

class NinoValidatorTest extends UnitTestSuite {

  behavior of "NinoValidator.isValid" 

  it should "return true for a valid NINO with suffix" in {
    NinoValidator.isValid("AB123456C") should be(true)
  }

  it should "return true for a valid NINO without suffix" in {
    NinoValidator.isValid("AB123456") should be(true)
  }

  it should "return false for an empty NINO" in {
    NinoValidator.isValid("") should be(false)
  }

  it should "return true for a valid NINO with spaces" in {
    NinoValidator.isValid(" AB 12 3 456 C ") should be(true)
  }

  it should "be case-insensitive" in {
    NinoValidator.isValid("Ab123456c") should be(true)
  }

  it should "return false for an invalid NINO" in {
    NinoValidator.isValid("AB123456X") should be(false)
    NinoValidator.isValid("AB123456Cx") should be(false)
    NinoValidator.isValid("AB12345") should be(false)
  }
}
