package engine.agents

import engine.Vector2D


final case class Report(summary: Map[Vector2D, CellReport]) {
    private def generateTotal = {
        var m: Map[Health.Health, Int] = Map()
        for (cell <- summary.values) m ++= cell.summary.map { case (k, v) => k -> (v + (m getOrElse (k, 0)))}
        m
    }

    lazy val total: Map[Health.Health, Int] = generateTotal

    def getTotal(status: Health.Health): Int = total getOrElse (status, 0)
}

final case class CellReport(summary: Map[Health.Health, Int]) {
    def get(status: Health.Health): Int = summary getOrElse (status, 0)
}
