package uk.gov.gds.ier.validation

import uk.gov.gds.ier.model.DOB
import org.joda.time.{DateTime, DateMidnight}
import org.joda.time.Months
import uk.gov.gds.ier.model.DateLeft

object DateValidator {

  lazy val minimumAge = 16
  lazy val maximumAge = 115
  lazy val maximumCitizenshipDuration = 115


  def isExistingDate(dateOfBirth: DOB):Option[DateMidnight] = {
    try {
      Some(parseToDateMidnight(dateOfBirth))
    } catch {
      case ex: Exception => None
    }
  }

  def isExistingDateInThePast(dateOfBirth: DateMidnight) = {
    try {
      dateOfBirth.isBeforeNow
    } catch {
      case ex: Exception => false
    }
  }

  def isTooOldToBeAlive(dateOfBirth: DateMidnight) = {
    isDateBefore(dateOfBirth, maximumAge)
  }

  def isCitizenshipTooOld(dateOfCitizenship: DateMidnight) =  {
    isDateBefore(dateOfCitizenship, maximumCitizenshipDuration)
  }


  def isTooYoungToRegister(dateOfBirth: DOB) = {
    try {
      parseToDateMidnight(dateOfBirth).plusYears(minimumAge).isAfter(DateTime.now.toDateMidnight)
    } catch {
      case ex: Exception => false
    }
  }
  
  def dateLeftUkOver15Years(dateLeftUk:DateLeft):Boolean = {
    val leftUk = new DateTime().withMonthOfYear(dateLeftUk.month).withYear(dateLeftUk.year)
    val monthDiff = Months.monthsBetween(leftUk, DateTime.now()).getMonths()
    if (monthDiff >= 15 * 12) true
    else false
  }
  
  def isLessEighteen(dateOfBirth: DOB) = {
    try {
      val eighteenYearsAgo = DateTime.now.minusYears(18).toDateMidnight
    	val dob = parseToDateMidnight(dateOfBirth)
      dob.isAfter(eighteenYearsAgo) || dob.isEqual(eighteenYearsAgo)
    } catch {
      case ex: Exception => false
    }
  }

  private def isDateBefore(date: DateMidnight, yearsBack: Int) = {
    try {
      date.plusYears(yearsBack).isBefore(DateTime.now.toDateMidnight.plusDays(1))
    } catch {
      case ex: Exception => false
    }
  }

  private def parseToDateMidnight(dateOfBirth: DOB) = {
    new DateMidnight(
      dateOfBirth.year, 
      dateOfBirth.month, 
      dateOfBirth.day)
  }
}
