package uk.gov.gds.ier.guice

import com.google.inject.Inject
import com.google.inject.{Guice, Module}

class Injector @Inject()(
    injector: com.google.inject.Injector
) {
  def dependency[A <: AnyRef](implicit m: Manifest[A]):A = {
    injector.getInstance(m.runtimeClass.asInstanceOf[Class[A]])
  }
  def dependency[A](dependencyClass: Class[A]) = {
    injector.getInstance(dependencyClass)
  }
}

object Injector {
  def apply(modules: List[Module]): Injector = {
    new Injector(Guice.createInjector(modules:_*))
  }
}
