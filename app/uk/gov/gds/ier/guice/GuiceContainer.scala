package uk.gov.gds.ier.guice

import com.google.inject.{Module, Inject, Binder, AbstractModule}

class GuiceContainer @Inject() (val di: Injector) {

  @inline final def dependency[A <: AnyRef](implicit m: Manifest[A]) = {
    di.dependency[A]
  }

  @inline final def dependency[A](dependencyClass: Class[A]) = {
    di.dependency(dependencyClass)
  }
}

object GuiceContainer {
  private[guice] class EmptyModule extends AbstractModule {
    def configure {/* no-op*/}
  }

  def apply(modules: List[Module]): GuiceContainer = {
    new GuiceContainer(Injector(modules))
  }

  def apply(bindings: Binder => Unit): GuiceContainer = {
    GuiceContainer(List(new EmptyModule {
      override def configure {
        bindings(binder)
      }
    }))
  }
}
