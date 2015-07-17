package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.form.AddressHelpers

trait LastUkAddressBlocks extends AddressHelpers {
  self: ConfirmationBlock =>

  def lastUkAddress = {
    ConfirmationQuestion(
      title = "Registration address",
      editLink = if (isManualAddressDefined(form, keys.lastUkAddress.manualAddress)) {
        overseas.LastUkAddressManualStep.routing.editGet.url
      } else {
        overseas.LastUkAddressSelectStep.routing.editGet.url
      },
      changeName = "your registration address",
      content = ifComplete(keys.lastUkAddress) {
        val addressLine = form(keys.lastUkAddress.addressLine).value.orElse{
          manualAddressToOneLine(form, keys.lastUkAddress.manualAddress)
        }.getOrElse("")
        val postcode = form(keys.lastUkAddress.postcode).value.getOrElse("")
        List(addressLine, postcode)
      }
    )
  }
}
