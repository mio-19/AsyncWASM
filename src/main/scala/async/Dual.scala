package async

import chisel3._

class Dual[T <: Data](A: => T) extends Bundle {
  val zeros = A
  val ones = A

  assert(zeros.getWidth == ones.getWidth)
  assert(zeros.getWidth > 0)

  def isCleared: Bool = !(0 until zeros.getWidth).map(i => zeros.asUInt.apply(i) || ones.asUInt.apply(i)).reduce(_ || _)

  def isValid: Bool = (0 until zeros.getWidth).map(i => zeros.asUInt.apply(i) || ones.asUInt.apply(i)).reduce(_ && _)
}

object Dual {
  def apply[T <: Data](A: => T): Dual[T] = new Dual[T](A)
}