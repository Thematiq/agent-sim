package engine.agents

import engine.Vector2D


final case class Report(summary: Map[Vector2D, CellReport])
final case class CellReport(summary: Map[Health.Health, Int])
