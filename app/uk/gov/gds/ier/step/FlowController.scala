package uk.gov.gds.ier.step

trait FlowController[T] {
  type ApplicationStep = (T, Step[T])
  type FlowControl = ApplicationStep => ApplicationStep

  class FlowRecursionException extends Exception("Recursion encountered in FlowControl")

  object GoToNextStep {
    def apply(): FlowControl = {
      case (currentState, step) => {
        val nextStep = step.nextStep(currentState)
        (currentState, GoTo(nextStep.routing.get))
      }
    }
  }

  object GoToNextIncompleteStep {

    def apply(): FlowControl = {
      case (currentState, step) => {
        (currentState, getNextStep(currentState, step))
      }
    }

    def getNextStep(app:T, step:Step[T]):Step[T] = {
      if (step.isStepComplete(app)) {
        val nextStep = step.nextStep(app)
        if (nextStep == step) throw new FlowRecursionException
        getNextStep(app, nextStep)
      } else {
        GoTo(step.routing.get)
      }
    }
  }

  object TransformApplication {

    def apply(transform:T=>T): FlowControl = {
      case (currentState, step) => {
        (transform(currentState), step)
      }
    }
  }

  object BranchOn {
    def apply[A](extractA:T => A)(branchOn:A=>FlowControl): FlowControl = {
      case (currentState, step) => {
        val a = extractA(currentState)
        val flowControl = branchOn(a)
        flowControl(currentState, step)
      }
    }
  }
}
