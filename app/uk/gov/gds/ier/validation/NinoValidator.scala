package uk.gov.gds.ier.validation

object NinoValidator {

  final val ninoRegex = "^[A-CEGHJ-PR-TW-Za-ceghj-pr-tw-z]{1}[A-CEGHJ-NPR-TW-Za-ceghj-npr-tw-z]{1}[0-9]{6}[A-DFMa-dfm]{0,1}$"

  def isValid(nino:String) = {
    nino.toUpperCase.replaceAll("[\\s|\\-]", "").matches(ninoRegex)
  }
}
