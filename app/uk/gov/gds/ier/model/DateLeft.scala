package uk.gov.gds.ier.model

case class DateLeft (year:Int, month:Int) {
  def toApiMap(key:String = "leftuk") = {
    Map(key -> "%04d-%02d".format(year,month))
  }
}

case class DateLeftSpecial (date:DateLeft) {
  def toApiMap = {
    date.toApiMap("dcs")
  }
}
