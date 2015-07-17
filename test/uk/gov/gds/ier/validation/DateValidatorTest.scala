package uk.gov.gds.ier.validation

import uk.gov.gds.ier.model.DOB
import org.joda.time.{DateMidnight, DateTime}
import uk.gov.gds.ier.test.UnitTestSuite

class DateValidatorTest extends UnitTestSuite {

  behavior of "DateValidator.isExistingDateInThePast"
  it should "return true for an existing past date" in {
    DateValidator.isExistingDateInThePast(new DateMidnight(1986, 10, 11)) should be(true)
  }

  it should "return true for today" in {
    val now = DateTime.now.toDateMidnight
    DateValidator.isExistingDateInThePast(now) should be(true)
  }

  it should "return false for a non-existing date" in {
    DateValidator.isExistingDate(DOB(1978, 2, 29)) should be(None)
  }

  it should "return false for a future past date" in {
    val tomorrow = DateTime.now.toDateMidnight.plusDays(1)
    DateValidator.isExistingDateInThePast(tomorrow) should be(false)
  }

  behavior of "DateValidator.isTooOldToBeAlive"

  it should "return false for a date newer than 115 years ago" in {
    val almost115yearsAgo = DateTime.now.toDateMidnight.minusYears(115).plusDays(1)
    DateValidator.isTooOldToBeAlive(almost115yearsAgo) should be(false)
  }

  it should "return true for a date equal to 115 years ago" in {
    val exactly115yearsAgo = DateTime.now.toDateMidnight.minusYears(115)
    DateValidator.isTooOldToBeAlive(exactly115yearsAgo) should be(true)
  }

  it should "return true for a date older than 115 years ago" in {
    val moreThan115yearsAgo = DateTime.now.toDateMidnight.minusYears(115).minusDays(1)
    DateValidator.isTooOldToBeAlive(moreThan115yearsAgo) should be(true)
  }

  behavior of "DateValidator.isTooYoungToRegister"

  it should "return false for a date older than 16 years ago" in {
    val moreThan16YearsAgo = DateTime.now.toDateMidnight.minusYears(16).minusDays(1)
    DateValidator.isTooYoungToRegister(getDateOfBirth(moreThan16YearsAgo)) should be(false)
  }

  it should "return false for a date equal to 16 years ago" in {
    val moreThan16YearsAgo = DateTime.now.toDateMidnight.minusYears(16)
    DateValidator.isTooYoungToRegister(getDateOfBirth(moreThan16YearsAgo)) should be(false)
  }

  it should "return true for a date newer than 16 years ago" in {
    val almost16YearsAgo = DateTime.now.toDateMidnight.minusYears(16).plusDays(1)
    DateValidator.isTooYoungToRegister(getDateOfBirth(almost16YearsAgo)) should be(true)
  }

  it should "return true for a date less than 18 years ago" in {
    val lessThan18years = DateTime.now.toDateMidnight.minusYears(18).plusDays(1)
    DateValidator.isLessEighteen(getDateOfBirth(lessThan18years)) should be(true)
  }
  it should "return false for a date more than 18 years ago" in {
    val lessThan18years = DateTime.now.toDateMidnight.minusYears(18).minusDays(1)
    DateValidator.isLessEighteen(getDateOfBirth(lessThan18years)) should be(false)
  }

  behavior of "DateValidator.isCitizenshipTooOld"

  it should "return false for a date newer than 115 years ago" in {
    val almost115yearsAgo = DateTime.now.toDateMidnight.minusYears(115).plusDays(1)
    DateValidator.isCitizenshipTooOld(almost115yearsAgo) should be(false)
  }

  it should "return true for a date equal to 115 years ago" in {
    val exactly115yearsAgo = DateTime.now.toDateMidnight.minusYears(115)
    DateValidator.isCitizenshipTooOld(exactly115yearsAgo) should be(true)
  }

  it should "return true for a date older than 115 years ago" in {
    val moreThan115yearsAgo = DateTime.now.toDateMidnight.minusYears(115).minusDays(1)
    DateValidator.isCitizenshipTooOld(moreThan115yearsAgo) should be(true)
  }

  private def getDateOfBirth(date: DateMidnight) = DOB(date.getYear, date.getMonthOfYear, date.getDayOfMonth)
}
