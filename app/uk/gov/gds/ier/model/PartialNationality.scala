package uk.gov.gds.ier.model

import uk.gov.gds.ier.validation._

case class PartialNationality(
    british: Option[Boolean] = None,
    irish: Option[Boolean] = None,
    hasOtherCountry: Option[Boolean] = None,
    otherCountries: List[String] = List.empty,
    noNationalityReason: Option[String] = None) {

  def checkedNationalities = {
    british.toList.filter(_ == true).map(brit => "British") ++
    irish.toList.filter(_ == true).map(isIrish => "Irish")
  }

  def isoCheckedNationalities = {
    british.toList.filter(_ == true).map(brit => "United Kingdom") ++
    irish.toList.filter(_ == true).map(isIrish => "Ireland")
  }

  /**
   * Output example: "British, Irish and Czech"
   * Do not include other countries if the flag is off.
   */
  def toNiceString() : Option[String] = {
    val allNatList =
      british.filter(_ == true).map(brit => "British").toList ++
      irish.filter(_ == true).map(isIrish => "Irish").toList ++
      hasOtherCountry.filter(_ == true).map(x => otherCountries.filter(_.trim.nonEmpty)).getOrElse(Nil)
    allNatList.filter(_.nonEmpty).splitAt(allNatList.size - 2) match {
      case (Nil, Nil) => None
      case (Nil, lastTwo) => Some(lastTwo.mkString(" and "))
      case (heads, lastTwo) => Some(heads.mkString(", ") + ", " + lastTwo.mkString(" and "))
    }
  }
}

object PartialNationality extends ModelMapping {
  import playMappings._

  private val listStrippingEmptyString = list(text).transform[List[String]](
    list => list.filterNot(_.isEmpty),
    list => list.filterNot(_.isEmpty)
  )

  val mapping = playMappings.mapping(
    keys.british.key -> default(optional(boolean), Some(false)),
    keys.irish.key -> default(optional(boolean), Some(false)),
    keys.hasOtherCountry.key -> optional(boolean),
    keys.otherCountries.key -> listStrippingEmptyString,
    keys.noNationalityReason.key -> optional(nonEmptyText)
  ) (
    (
      british,
      irish,
      hasOtherCountry,
      otherCountries,
      noNationalityReason
    ) => {
      PartialNationality(
        british = british,
        irish = irish,
        hasOtherCountry = hasOtherCountry orElse Some(!otherCountries.isEmpty),
        otherCountries = otherCountries,
        noNationalityReason = noNationalityReason
      )
    }
  ) (
    PartialNationality.unapply
  )
}
