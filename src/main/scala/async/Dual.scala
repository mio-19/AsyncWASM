package async

import chisel3._
import chisel3.experimental.BundleLiterals._

class Dual[T <: Data](A: T) extends Bundle {
  val zeros = A.cloneType
  val ones = A.cloneType

  assert(zeros.getWidth == ones.getWidth)
  assert(zeros.getWidth > 0)

  def unsafeIsCleared: Bool = VecInit(zeros, ones).asUInt === 0.U

  def unsafeIsValid: Bool = (0 until zeros.getWidth).map(i => zeros.asUInt.apply(i) || ones.asUInt.apply(i)).reduce(_ && _)

  def unsafeExtract: T = ones

  def unsafeWrite(x: T): Unit = {
    val width = zeros.getWidth
    val zerov = zeros.asTypeOf(Vec(width, Bool()))
    val onev = ones.asTypeOf(Vec(width, Bool()))
    for (i <- 0 until width) {
      Mux(x.asUInt.apply(i), onev, zerov) := true.B
    }
  }
}

object Dual {
  def apply[T <: Data](A: T): Dual[T] = new Dual[T](A)

  def from[T <: Data](A: T): Dual[T] = (new Dual[T](A.cloneType)).Lit(_.zeros -> (~A.asUInt).asTypeOf(A), _.ones -> A)
}