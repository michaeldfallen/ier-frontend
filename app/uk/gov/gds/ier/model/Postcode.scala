package uk.gov.gds.ier.model

object Postcode extends ModelMapping {

  def toCleanFormat(postcode: String) = cleanFormat(postcode)

  def toApiFormat(postcode: String) =  {
    val formattedPostcode = cleanFormat(postcode).toUpperCase
    if(formattedPostcode.length > 3) {
      formattedPostcode.take(formattedPostcode.length-3)+" "+formattedPostcode.takeRight(3)
    } else formattedPostcode
  }

  private def cleanFormat(postcode: String) = {
    postcode.replaceAll("[<>|\\s\\t]", "").toLowerCase
  }

}
