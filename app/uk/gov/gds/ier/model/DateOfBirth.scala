package uk.gov.gds.ier.model

import org.joda.time.LocalDate

case class DateOfBirth(dob:Option[DOB],
                       noDob:Option[noDOB]) {
  def toApiMap = {
    dob.map(_.toApiMap("dob")).getOrElse(Map.empty) ++
    noDob.map(_.toApiMap).getOrElse(Map.empty)
  }
}

case class DOB(year:Int,
               month:Int,
               day:Int) {
  def toApiMap(key:String) = {
    Map(key -> (year + "-" + "%02d".format(month) + "-" + "%02d".format(day)))
  }

  def isToday = {
    val now = LocalDate.now

    month == now.monthOfYear.get &&
    day ==  now.dayOfMonth.get
  }

}

case class noDOB(reason:Option[String],
                 range:Option[String]) {
  def toApiMap = {
    reason.map(r => Map("nodobreason" -> r)).getOrElse(Map.empty) ++
      range.map(r => Map("agerange" -> r)).getOrElse(Map.empty)
  }
}
