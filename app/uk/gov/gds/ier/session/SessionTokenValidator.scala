package uk.gov.gds.ier.session

import org.joda.time.DateTime
import uk.gov.gds.ier.guice.WithConfig

trait SessionTokenValidator {
  self: WithConfig =>

  implicit class SessionTokenWithValidation(token: SessionToken) {
    def isValid() = {
      try {
        token.latest.isAfter(DateTime.now.minusMinutes(config.sessionTimeout))
      } catch {
        case e:IllegalArgumentException => false
      }
    }
  }
}
