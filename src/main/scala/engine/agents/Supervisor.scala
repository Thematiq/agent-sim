package engine.agents

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import engine.{Vector2D, Tools}


object Supervisor {
    def apply(X: Int, Y: Int, initialPop: Int): Behavior[SupervisorCommand] =
        Behaviors.setup { context =>
            var city: Map[Vector2D, ActorRef[CellCommand]] = Map()
            val stats = context.spawn(Statistician(context.self), "Statistician")
            for (x <- 0 to X; y <- 0 to Y) {
                val vec = Vector2D(x, y)
                city += (vec -> context.spawn(Cell(context.self, initialPop, vec), "Cell" + vec.safeString))
            }

            Behaviors.receive { (context, message) => message match {
                case WorldTick =>
                    city foreach ( k => k._2 ! DispatchTick)
                    Behaviors.same
                case Poison =>
                    city foreach (x => x._2 ! Poison)
                    Behaviors.stopped
                case PostToEveryCell(cmd) =>
                    city foreach (k =>
                        k._2 ! cmd
                    )
                    Behaviors.same
                case GetReport(replyTo, timeout) =>
                    stats ! GenerateReport(replyTo, timeout)
                    Behaviors.same
                case GetRandomStateAt(pos, replyTo) =>
                    city(pos) ! GetRandomState(replyTo)
                    Behaviors.same
                case GetPopulationAt(pos, replyTo) =>
                    city(pos) ! GetPopulation(replyTo)
                    Behaviors.same
                case MoveFromCell(pos, patient) =>
                    val to = Tools.getRandomElement(
                        Vector2D.getNhbd(pos, Vector2D(0, 0), Vector2D(X, Y))
                    )
                    city(to) ! MoveTo(patient)
                    Behaviors.same
                case DebugCell(cmd, cell) =>
                    city(cell) ! cmd
                    Behaviors.same
                case e =>
                    context.log.info("Unhandled event {}", e)
                    Behaviors.unhandled
            }}
        }
}