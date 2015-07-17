package uk.gov.gds.ier.model

import org.joda.time.{DateTime, LocalDate}
import play.api.libs.json._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.IerForms
import uk.gov.gds.ier.test.{UnitTestSuite, WithMockAddressService}

class IerFormsTests extends UnitTestSuite {

  val forms = new IerForms
    with WithMockAddressService
    with WithSerialiser {
    val serialiser = jsonSerialiser
  }

  "PostcodeForm" should "bind a postcode" in {
    val jsVal = Json.toJson(
      Map(
        "postcode" -> "BT12 5EG"
      )
    )
    forms.postcodeForm.bind(jsVal).fold(
      hasErrors => fail(hasErrors.toString),
      success => {
        success should be("BT12 5EG")
      }
    )
  }

  it should "throw an error on a bad postcode" in {
    val jsVal = Json.toJson(
      Map(
        "postcode" -> "ZX123 BAD"
      )
    )
    forms.postcodeForm.bind(jsVal).fold(
      hasErrors => {
        hasErrors.errors("postcode") should not be(None)
      },
      success => {
        fail("Should not have succeeded " + success)
      }
    )
  }
}
