package uk.gov.gds.ier.transaction.forces.contactAddress

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model.{ContactAddress, PossibleContactAddresses}

class ContactAddressFormTests
  extends FormTestSuite
  with ContactAddressForms {

  it should "error out on empty input" in {
    val emptyRequest = Map.empty[String, String]
    contactAddressForm.bind(emptyRequest).fold(
      formWithErrors => {
        formWithErrors.errors("contactAddress.contactAddressType").head.message should be ("Please answer this question")
        formWithErrors.globalErrorMessages should be (Seq("Please answer this question"))
      },
      formWithSuccess => fail("Should have thrown an error")
    )
  }

  it should "bind successfully on uk address" in {
    val request = Json.toJson(
      Map(
        "contactAddress.contactAddressType" -> "uk"
      )
    )
    contactAddressForm.bind(request).fold(
      formWithErrors => fail(serialiser.toJson(formWithErrors.prettyPrint)),
      formWithSuccess => {
        formWithSuccess.contactAddress.isDefined should be(true)
        formWithSuccess.contactAddress should be(
          Some(PossibleContactAddresses(Some("uk"),None,None,None)))
      }
    )
  }

  it should "bind successfully on bfpo address" in {
    val request = Json.toJson(
      Map(
        "contactAddress.contactAddressType" -> "bfpo",
        "contactAddress.bfpoContactAddress.addressLine1" -> "address line 1",
        "contactAddress.bfpoContactAddress.addressLine2" -> "address line 2, 456 - 457",
        "contactAddress.bfpoContactAddress.addressLine3" -> "London",
        "contactAddress.bfpoContactAddress.postcode" -> "BFPO90-987 XXZ"
      )
    )
    contactAddressForm.bind(request).fold(
      formWithErrors => fail(serialiser.toJson(formWithErrors.prettyPrint)),
      formWithSuccess => {
        formWithSuccess.contactAddress.isDefined should be(true)
        formWithSuccess.contactAddress should be(
          Some(PossibleContactAddresses(
            Some("bfpo"),
            None,
            Some(ContactAddress(
              None,
              Some("BFPO90-987 XXZ"),
              Some("address line 1"),
              Some("address line 2, 456 - 457"),
              Some("London"),
              None,
              None
            )),
            None
          ))
        )
      }
    )
  }

  it should "bind successfully on other address" in {
    val request = Json.toJson(
      Map(
        "contactAddress.contactAddressType" -> "other",
        "contactAddress.otherContactAddress.addressLine1" -> "Francisco de quevedo 54",
        "contactAddress.otherContactAddress.addressLine2" -> "Rubí",
        "contactAddress.otherContactAddress.postcode" -> "08191",
        "contactAddress.otherContactAddress.country" -> "Spain"
      )
    )
    contactAddressForm.bind(request).fold(
      formWithErrors => fail(serialiser.toJson(formWithErrors.prettyPrint)),
      formWithSuccess => {
        formWithSuccess.contactAddress.isDefined should be(true)
        formWithSuccess.contactAddress should be(
          Some(PossibleContactAddresses(
            Some("other"),
            None,
            None,
            Some(ContactAddress(
              Some("Spain"),
              Some("08191"),
              Some("Francisco de quevedo 54"),
              Some("Rubí"),
              None,
              None,
              None
            ))
          ))
        )
      }
    )
  }

}
