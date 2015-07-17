package uk.gov.gds.ier.guice

import play.api.mvc.Controller

/**
 * This object is the play controller that handles the incoming requests for the licence application process.
 * It acts as a simple facade between the play framework and our internal application, which is managed by Guice for
 * dependency injection. Because play controllers are scala objects created by the play framework
 * they cannot participate in dependecy injection, so we resolve dependencies here using the service / locator
 * pattern
 */

abstract class DelegatingController[A <: AnyRef](implicit m: Manifest[A]) extends Controller {

  private lazy val app = play.api.Play.maybeApplication getOrElse {
    throw new IllegalStateException("Play Application not started.")
  }
  private lazy val delegateClass = m.runtimeClass.asInstanceOf[Class[A]]

  protected lazy val delegate = app.global.getControllerInstance(delegateClass)
}
