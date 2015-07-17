package uk.gov.gds.ier.guice

import uk.gov.gds.ier.test.UnitTestSuite
import com.google.inject.{AbstractModule, Binder}

class GuiceContainerTests extends UnitTestSuite {

  it should "resolve a dependancy once initialised" in {
    val foo = new Foo

    val guice = GuiceContainer { binder =>
      binder bind classOf[Foo] toInstance foo
    }

    val dependency = guice.dependency[Foo]
    dependency should be(foo)
    dependency.foo should be("foo")
  }

  it should "dependencies should not leak across GuiceContainers" in {
    val foo = new Foo

    val guice1 = GuiceContainer { binder =>
      binder bind classOf[Foo] toInstance foo
    }

    guice1.dependency[Foo] should be(foo)

    val notFoo = new Foo {
      override def foo = "bar"
    }

    val guice2 = GuiceContainer { binder =>
      binder bind classOf[Foo] toInstance notFoo
    }

    val dependency = guice2.dependency[Foo]
    dependency should not be(foo)
    dependency should be(notFoo)

    dependency.foo should be("bar")
  }

  it should "be able to inject itself" in {
    val foo = new Foo

    val guice = GuiceContainer { binder =>
      binder bind classOf[Foo] toInstance foo
    }

    val injectedGuice = guice.dependency[GuiceContainer]

    injectedGuice.dependency[Foo] should be(foo)
  }

  it should "inject the correct GuiceContainer" in {
    val foo = new Foo
    val bar = new Foo {
      override def foo = "bar"
    }

    val guice1 = GuiceContainer { binder =>
      binder bind classOf[Foo] toInstance foo
    }
    val guice2 = GuiceContainer { binder =>
      binder bind classOf[Foo] toInstance bar
    }

    val injectedGuice1 = guice1.dependency[GuiceContainer]
    injectedGuice1.dependency[Foo] should be(foo)

    val injectedGuice2 = guice2.dependency[GuiceContainer]
    injectedGuice2.dependency[Foo] should be(bar)
  }
}

class Foo {
  def foo = "foo"
}
