package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.UnitTestSuite
import uk.gov.gds.ier.validation.constants.NameConstants

class PostcodeTests extends UnitTestSuite {

  behavior of "Postcode.toApiFormat"
  it should "return empty string for empty postcode" in {
    Postcode.toApiFormat("") should be("")
  }

  it should "insert a space before last 3 characters" in {
    Postcode.toApiFormat("n") should be("N")
    Postcode.toApiFormat("un") should be("UN")
    Postcode.toApiFormat("6un") should be("6UN")
    Postcode.toApiFormat("n6un") should be("N 6UN")
    Postcode.toApiFormat("nw6un") should be("NW 6UN")
    Postcode.toApiFormat("nw26un") should be("NW2 6UN")
  }

  it should "strip all whitespaces" in {
    Postcode.toApiFormat("  n  w 26  u n  ") should be("NW2 6UN")
    Postcode.toApiFormat("   n W26UN   ") should be("NW2 6UN")
  }

  it should "format short and long postcodes correctly" in {
    Postcode.toApiFormat("s11aa") should be("S1 1AA")
    Postcode.toApiFormat("wc2b6se") should be("WC2B 6SE")
  }

  it should "strip special characters and trailing spaces" in {
    Postcode.toApiFormat(" \t  nw \t2<\t|>6 u			n  \t") should be("NW2 6UN")
  }

  behavior of "Postcode.toCleanFormat"
  it should "return empty string for empty postcode" in {
    Postcode.toCleanFormat("") should be("")
  }

  it should "strip all whitespaces" in {
    Postcode.toCleanFormat("  N  w 26  U n  ") should be("nw26un")
    Postcode.toCleanFormat("   n W26un   ") should be("nw26un")
  }

  it should "format short and long postcodes correctly" in {
    Postcode.toCleanFormat("S1 1AA") should be("s11aa")
    Postcode.toCleanFormat("WC2B 6SE") should be("wc2b6se")
  }

  it should "strip special characters and trailing spaces" in {
    Postcode.toCleanFormat(" \t  Nw \t2<\t|>6 U			n  \t") should be("nw26un")
  }


}
