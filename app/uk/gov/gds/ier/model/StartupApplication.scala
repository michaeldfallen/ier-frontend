package uk.gov.gds.ier.model

import uk.gov.gds.ier.step.InprogressApplication

case class StartupApplication(sessionId: Option[String]) extends InprogressApplication[StartupApplication] {
  def merge(other: StartupApplication) = this
}