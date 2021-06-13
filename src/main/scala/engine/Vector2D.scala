package engine

final case class Vector2D(x: Int, y: Int) {
    def unary_- = new Vector2D(-x, -y)
    def +(o: Vector2D) = new Vector2D(x + o.x, y + o.y)
    def -(o: Vector2D) = this + (-o)
    def *(o: Double) = new Vector2D((x * o).toInt, (y * o).toInt)
    def /(o: Double) = new Vector2D((x / o).toInt, (y / o).toInt)
    override def toString: String = "[" + x + ", " + y + "]"
    def safeString: String = x + "_" + y
    def precedes(o: Vector2D): Boolean = (x <= o.x) && (y <= o.y)
    def follows(o: Vector2D): Boolean = (x >= o.x) && (y >= o.y)
}

object Vector2D {
    def apply() = new Vector2D(0, 0)
    def apply(x: Int, y: Int) = new Vector2D(x, y)
    def getNhbd(vec: Vector2D, from: Vector2D, to: Vector2D): Vector[Vector2D] = {
        val vect = Vector.tabulate(3, 3) ( (x, y) =>
            vec + Vector2D(x-1, y-1)
        )
        vect.flatten
            .filter(x => (x follows from) && (x precedes to) && (x != vec))
    }
}