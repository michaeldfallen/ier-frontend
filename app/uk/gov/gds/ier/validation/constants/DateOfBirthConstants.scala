package uk.gov.gds.ier.validation.constants

import org.joda.time.{LocalDate, DateTime}

object DateOfBirthConstants {
  lazy val days = (1 to 31).toSeq.map(i => (i.toString, i.toString))

  lazy val months = Seq(
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"
  ).zip((1 to 12).map(_.toString)).
    map { case (name, number) => (number, name) }

  lazy val monthsByNumber = months.toMap

  lazy val years = {
    lazy val now = new DateTime()
    (now.getYear to 1899 by -1).toSeq.map(i => (i.toString, i.toString))
  }

  lazy val under18 = "under18"
  lazy val is18to70 = "18to70"
  lazy val over70 = "over70"
  lazy val dontKnow = "dontKnow"

  lazy val noDobRanges = Seq(
    under18,
    is18to70,
    over70,
    dontKnow
  )

  lazy val jan1st1983 = new LocalDate()
    .withYear(1983)
    .withMonthOfYear(1)
    .withDayOfMonth(1)
}
