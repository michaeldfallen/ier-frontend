package uk.gov.gds.ier.validation

object EmailValidator {

  val emailDomainRegex = """^[^@^\.]+(\.[^@^\.]+)+$"""

  def isValid(email: Option[String]): Boolean = {
    email match {
      case Some(str) => isValid(str)
      case None => false
    }
  }

  def isValid(email: String) = {
    val p = email.lastIndexOf('@')
    if (p >= 0) {
      val localPart = email.substring(0, p)
      val domain = email.substring(p + 1)
      (localPart.size > 0 && domain.matches(emailDomainRegex))
    } else false
  }
}
