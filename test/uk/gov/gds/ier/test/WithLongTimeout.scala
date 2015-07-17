package uk.gov.gds.ier.test

import akka.util.Timeout
import java.util.concurrent.TimeUnit

/** use this with test you want to debug, do not use regularly */
trait WithLongTimeout {
  implicit def defaultAwaitTimeout = Timeout(10, TimeUnit.MINUTES)
}
