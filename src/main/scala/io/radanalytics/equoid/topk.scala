// type parameter V is type of object values being counted
// class parameters are 'val'; this class is immutable
//

package io.radanalytics.equoid

import scala.util.Random
import org.apache.spark.util.sketch.CountMinSketch

import scala.collection.immutable

class TopK[V] ( 
  val k: Int,
  val cms: CountMinSketch,
  val topk: immutable.Map[V, Int],
  val fmin: Int,
  val epsilon: Double,
  val confidence: Double)  extends Serializable {

  // update the TopK sketch w/ a new element 'v'
  def +(v: V): TopK[V] = {
    println(s"\n\nTopK +")
    val ecms: CountMinSketch = CountMinSketch.create(epsilon, confidence, 13)
    val ucms: CountMinSketch = ecms.mergeInPlace(this.cms)
    ucms.add(v, 1)
    val vf = ucms.estimateCount(v).toInt
    val (utopk, ufmin) = if (topk.size < k) {
      (topk + (v -> vf), math.min(vf, fmin))
    } else if (vf <= fmin) (topk, fmin) else {
      val del = topk.minBy { case (_, f) => f }
      ((topk - del._1) + ((v, vf)), topk.values.min)
    }
    new TopK[V](k, ucms, utopk, ufmin, epsilon, confidence)
  }
  
  // combine two TopK sketches, monoidally
  def ++(that: TopK[V]): TopK[V] = {
    println(s"\n\nTopK ++")
    val ecms: CountMinSketch = CountMinSketch.create(epsilon, confidence, 13)
    val thatcms = ecms.mergeInPlace(that.cms) 
    val ucms = thatcms.mergeInPlace(this.cms)
    val vu: Set[V] = this.topk.keys.toSet ++ that.topk.keys.toSet
    val (utopk, ufmin) = vu.foldLeft((immutable.Map.empty[V, Int], 0)) { case ((tk, fm), v) =>
      val vf = ucms.estimateCount(v).toInt
      if (tk.size < k) {
        (tk + (v -> vf), math.min(vf, fm))
      } else if (vf <= fm) (tk, fm) else {
        val del = tk.minBy { case (_, f) => f }
        val mintk = (tk - del._1) + (v -> vf)
        (mintk, mintk.values.min)
      }
    }
    new TopK[V](k, ucms, utopk, ufmin, epsilon, confidence)
  }
}

// eps, confidence, seed
object TopK {
  val epsilon = 6
  val confidence = 0.9
  def empty[V](k: Int) = new TopK[V](k, CountMinSketch.create(epsilon, confidence, 13), immutable.Map.empty[V, Int], 0, epsilon, confidence)
}
