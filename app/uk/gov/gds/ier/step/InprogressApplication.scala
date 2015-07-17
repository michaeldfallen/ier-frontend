package uk.gov.gds.ier.step

abstract class InprogressApplication[T] {
  val sessionId: Option[String]
  def merge(other: T):T
}