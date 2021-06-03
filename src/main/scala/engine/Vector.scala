package engine

final case class Vector2D(val x: Int, val y: Int) {
    def unary_- = new Vector2D(-x, -y)
    def +(o: Vector2D) = new Vector2D(x + o.x, y + o.y)
    def -(o: Vector2D) = this + (-o)
    def *(o: Double) = new Vector2D((x * o).toInt, (y * o).toInt)
    def /(o: Double) = new Vector2D((x / o).toInt, (y / o).toInt)
    override def toString = "[" + x + ", " + y + "]"
}

object Vector2D {
    def apply() = new Vector2D(0, 0)
    def apply(x: Int, y: Int) = new Vector2D(x, y) 
}