package engine.agents

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import engine.{Vector2D, Tools}


object Cell {
    private def getPatientName(pos: Vector2D, id: Int): String = pos.safeString + "_" + id.toString

    def apply(supervisor: ActorRef[SupervisorCommand], initialPop: Int, pos: Vector2D): Behavior[CellCommand] =
        Behaviors.setup { context =>
            val pop: Vector[ActorRef[PatientCommand]] =
                Vector.tabulate(initialPop) ( x => context.spawn(Patient(context.self), getPatientName(pos, x)))
            cellAutomata(supervisor, pos, pop)
        }


    def cellAutomata(supervisor: ActorRef[SupervisorCommand], pos: Vector2D, currentPop: Vector[ActorRef[PatientCommand]]): Behavior[CellCommand] =
        Behaviors.receive { (context, message) => message match {
            case GetRandomState(replyTo) =>
                Tools.getRandomElement(currentPop) ! GetState(replyTo)
                Behaviors.same
            case MoveFrom(patient) =>
                supervisor ! MoveFromCell(pos, patient)
                cellAutomata(supervisor, pos, currentPop.filter(_ != patient))
            case MoveTo(patient) =>
                cellAutomata(supervisor, pos, currentPop :+ patient)
            case GetPopulation(replyTo) =>
                replyTo ! PostPopulation(currentPop.length)
                Behaviors.same
            case DebugPatient(cmd) =>
                Tools.getRandomElement(currentPop) ! cmd
                Behaviors.same
            case Poison =>
                currentPop.foreach(x => x ! Poison)
                Behaviors.stopped
        }}
}