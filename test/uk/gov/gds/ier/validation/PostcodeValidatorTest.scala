package uk.gov.gds.ier.validation

import uk.gov.gds.ier.test.UnitTestSuite

class PostcodeValidatorTest extends UnitTestSuite {

  it should "return true for a valid postcode" in {
    PostcodeValidator.isValid("SW11 2DR") should be(true)
  }

  it should "return true for a postcode that would be a valid postcode without white characters" in {
    PostcodeValidator.isValid(" S W 1 1 2D R    ") should be(true)
  }

  it should "return true for a lowercase postcode" in {
    PostcodeValidator.isValid("sw11 2dr") should be(true)
  }

  it should "return false for invalid postcode" in {
    PostcodeValidator.isValid("SW11X2DR") should be(false)
  }

  it should "return false for empty string" in {
    PostcodeValidator.isValid("") should be(false)
  }

  it should "return false for postcode with hyphen (-)" in {
    PostcodeValidator.isValid("SW11-2DR") should be(false)
  }
}
