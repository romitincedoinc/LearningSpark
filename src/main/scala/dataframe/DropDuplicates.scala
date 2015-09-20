package dataframe

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

object DropDuplicates {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("DataFrame-DropDuplicates").setMaster("local[4]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    import sqlContext.implicits._

    // create an RDD of tuples with some data
    val custs = Seq(
      (1, "Widget Co", 120000.00, 0.00, "AZ"),
      (2, "Acme Widgets", 410500.00, 500.00, "CA"),
      (3, "Widgetry", 410500.00, 200.00, "CA"),
      (4, "Widgets R Us", 410500.00, 0.0, "CA"),
      (3, "Widgetry", 410500.00, 200.00, "CA"),
      (5, "Ye Olde Widgete", 500.00, 0.0, "MA"),
      (6, "Widget Co", 12000.00, 10.00, "AZ")
    )
    val customerRows = sc.parallelize(custs, 4)

    // convert RDD of tuples to DataFrame by supplying column names
    val customerDF = customerRows.toDF("id", "name", "sales", "discount", "state")

    println("*** Here's the whole DataFrame with duplicates")

    customerDF.printSchema()

    customerDF.show()

    // drop fully identical rows
    val withoutDuplicates = customerDF.dropDuplicates()

    println("*** Now without duplicates")

    withoutDuplicates.show()

    // drop fully identical rows
    val withoutPartials = customerDF.dropDuplicates(Seq("name", "state"))

    println("*** Now without partial duplicates too")

    withoutPartials.show()

  }
}
