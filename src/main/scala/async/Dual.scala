package async

import chisel3._

class Dual[T <: Data](A: T) extends Bundle {
  val zeros = A.cloneType
  val ones = A.cloneType

  assert(zeros.getWidth == ones.getWidth)
  assert(zeros.getWidth > 0)

  def unsafeIsCleared: Bool = VecInit(zeros,ones).asUInt === 0.U

  def unsafeIsValid: Bool = (0 until zeros.getWidth).map(i => zeros.asUInt.apply(i) || ones.asUInt.apply(i)).reduce(_ && _)

  def unsafeExtract: T = ones

  def write(x: T) = ???
}

object Dual {
  def apply[T <: Data](A: T): Dual[T] = new Dual[T](A)
}