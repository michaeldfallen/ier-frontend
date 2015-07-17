package uk.gov.gds.ier.validation

object PostcodeValidator {

  val postcodeRegex = "(?i)((GIR0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY]))))[0-9][A-Z-[CIKMOV]]{2}))"

  def isValid(postcode: String) = {
    postcode.toUpperCase.replaceAll("\\s", "").matches(postcodeRegex)
  }
}
