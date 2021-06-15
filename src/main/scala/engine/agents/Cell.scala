package engine.agents

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import engine.{Vector2D, Tools}


object Cell {
    private def getPatientName(pos: Vector2D, id: Int): String = pos.safeString + "_" + id.toString
    private def getStatsName(pos: Vector2D): String = pos.safeString + "_stats"

    private final case class Refs(supervisor: ActorRef[SupervisorCommand], statistician: ActorRef[ReportCommand], config: Config)

    def apply(supervisor: ActorRef[SupervisorCommand], initialPop: Int, pos: Vector2D, config: Config): Behavior[CellCommand] =
        Behaviors.setup { context =>
            val pop: Vector[ActorRef[PatientCommand]] =
                Vector.tabulate(initialPop) ( x => context.spawn(Patient(context.self, config = config), getPatientName(pos, x)))
            val stats = context.spawn(CellStatistician(context.self), getStatsName(pos))
            cellAutomata(Refs(supervisor, stats, config), pos, pop)
        }


    private def cellAutomata(system: Refs, pos: Vector2D, currentPop: Vector[ActorRef[PatientCommand]]): Behavior[CellCommand] =
        Behaviors.receive { (context, message) => message match {
            case DispatchTick =>
                currentPop foreach ( x => x ! Tick)
                Behaviors.same
            case GetCellReport(replyTo, timeout) =>
                system.statistician ! GenerateCellReport(replyTo, timeout, pos)
                Behaviors.same
            case GetRandomState(replyTo) =>
                Tools.getRandomElement(currentPop) ! GetState(replyTo)
                Behaviors.same
            case GetEveryState(replyTo) =>
//                context.log.debug("Current cell members: {}", currentPop.length)
                currentPop foreach (x => x ! GetState(replyTo))
                Behaviors.same
            case MoveFrom(patient) =>
                val newPop = currentPop filterNot patient .==
                system.supervisor ! MoveFromCell(pos, patient)
                cellAutomata(system, pos, newPop)
            case MoveTo(patient) =>
                patient ! Accommodate(context.self)
                cellAutomata(system, pos, currentPop :+ patient)
            case GetPopulation(replyTo) =>
                replyTo ! PostPopulation(currentPop.length)
                Behaviors.same
            case DebugRandomPatient(cmd) =>
                Tools.getRandomElement(currentPop) ! cmd
                Behaviors.same
            case Poison =>
                currentPop.foreach(x => x ! Poison)
                Behaviors.stopped
            case e =>
                context.log.info("Unhandled event {}", e)
                Behaviors.unhandled
        }}
}