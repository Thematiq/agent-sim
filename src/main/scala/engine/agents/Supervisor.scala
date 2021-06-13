package engine.agents

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import engine.{Vector2D, Tools}


object Supervisor {
    def apply(X: Int, Y: Int, initialPop: Int): Behavior[SupervisorCommand] =
        Behaviors.setup { context =>
            var city: Map[Vector2D, ActorRef[CellCommand]] = Map()
            for (x <- 0 to X; y <- 0 to Y) {
                val vec = Vector2D(x, y)
                city += (vec -> context.spawn(Cell(context.self, initialPop, vec), "Cell" + vec.safeString))
            }

            Behaviors.receiveMessage {
                case Poison =>
                    city foreach (x => x._2 ! Poison)
                    Behaviors.stopped
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
            }
        }
}