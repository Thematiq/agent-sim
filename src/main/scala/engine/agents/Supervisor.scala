package engine.agents

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import engine.Vector2D


object Supervisor {
    def apply(X: Int, Y: Int, initialPop: Int): Behavior[SupervisorCommand] =
        Behaviors.setup { context =>
            var city: Map[Vector2D, ActorRef[CellCommand]] = Map()
            for (x <- 0 to X; y <- 0 to Y) {
                val vec = Vector2D(x, y)
                city += (vec -> context.spawn(Cell(initialPop, vec), "Cell" + vec.safeString))
            }

            Behaviors.receiveMessage {
                case Poison =>
                    city foreach (x => x._2 ! Poison)
                    Behaviors.stopped
                case GetRandomStateAt(pos, replyTo) =>
                    city(pos) ! GetRandomState(replyTo)
                    Behaviors.same
            }
        }
}