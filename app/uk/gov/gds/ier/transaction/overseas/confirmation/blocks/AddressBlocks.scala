package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.form.AddressHelpers
import uk.gov.gds.ier.validation.constants.NationalityConstants

trait AddressBlocks {
  self: ConfirmationBlock =>

  def address = {
    val addressLine = {
      val line1 = form(keys.overseasAddress.addressLine1).value
      val line2 = form(keys.overseasAddress.addressLine2).value
      val line3 = form(keys.overseasAddress.addressLine3).value
      val line4 = form(keys.overseasAddress.addressLine4).value
      val line5 = form(keys.overseasAddress.addressLine5).value

      val lines = List(line1, line2, line3, line4, line5)
      lines.flatten.mkString(",") match {
        case "" => None
        case str => Some(str)
      }
    }
    val countryKey = form(keys.overseasAddress.country).value
    val country = countryKey flatMap { key =>
      val iso = NationalityConstants.countryNameToCodes.get(key)
      iso.map(_.displayName)
    }
    ConfirmationQuestion(
      title = "Correspondence address",
      editLink = overseas.AddressStep.routing.editGet.url,
      changeName = "correspondence address",
      content = ifComplete(keys.overseasAddress) {
        List(addressLine, country).flatten
      }
    )
  }
}
