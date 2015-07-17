package uk.gov.gds.ier.transaction.shared

/**
 * Variant of Scala Either class tailored for Mustache limitations, specifically confirmation page.
 *
 * Mustache conditions work well only with Options or booleans, not Either; Mustache cannot
 * interpret properly LeftProjection/RightProjection used internally by Either.
 */
case class EitherErrorOrContent(blockContent: Option[List[String]], blockError: Option[String])

/** instantiate positive variant of EitherErrorOrContent */
object BlockContent {
  def apply(values: List[String]) = EitherErrorOrContent(blockContent = Some(values), blockError = None)
  def apply(value: String) = EitherErrorOrContent(blockContent = Some(List(value)), blockError = None)
}

/** instantiate negative variant of EitherErrorOrContent */
object BlockError {
  def apply(value: String) = EitherErrorOrContent(blockContent = None, blockError = Some(value))
}

