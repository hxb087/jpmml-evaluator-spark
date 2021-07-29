package org.jpmml.evaluator.spark

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.ml.Transformer
import org.apache.spark.sql._
import org.jpmml.evaluator.Evaluator
import java.io.{ByteArrayInputStream, File, FileInputStream}

/**
 * @author ：huxb
 * @date ：2021/7/28 16:36
 * @description：TODO
 * @modified By：
 * @version: $ 1.0
 */
object SVMExample {

  def main(args: Array[String]) {
    if (args.length > 0) {
      import scala.collection.JavaConversions.bufferAsJavaList
      val param = new SVMForParm
      ParamsParser.parse(param, args.toBuffer)
      run(param)
    } else {
      println("请输入有效参数...")
      sys.exit(0)
    }

  }

  def run(p: SVMForParm): Unit = {

    val spark = SparkSession.builder()
      .master("local")
      .appName("StringIndex")
      .getOrCreate()

    val builder = new StringBuilder
    println(p.inputModelPath)

    spark.sparkContext.textFile(p.inputModelPath).collect().foreach(item => builder.append(item))

    val inputStream = new ByteArrayInputStream(builder.toString().getBytes)

    //    val  file = spark.sparkContext.textFile(p.inputModelPath)
    //    val file = new File(p.inputModelPath)
    //    spark.sparkContext.textFile(p.inputModelPath)
    //    val inputStream = new FileInputStream(file)

    val evaluator: Evaluator = EvaluatorUtil.createEvaluator(inputStream)

    val modelBuilder: TransformerBuilder = new TransformerBuilder(evaluator).withTargetCols.withOutputCols.exploded(true)

    val transformer: Transformer = modelBuilder.build

    /**
     * 利用DataFrameReader从原始数据中构造 DataFrame对象
     * 需要原始数据包含列名
     */
    //    val conf: SparkConf =
    try {
      val sparkContext = spark.sparkContext
      try {
        val sqlContext = new SQLContext(sparkContext)
        val reader: DataFrameReader = sqlContext.read.format("com.databricks.spark.csv").option("header", "true").option("inferSchema", "true")
        //           Dataset<Row> dataFrame = reader.load(args[1]);// 输入数据需要包含列名
        var dataFrame: Dataset[Row] = reader.load(p.inputDataPath)


        /**
         * 使用模型进行预测
         */
        dataFrame = transformer.transform(dataFrame)
        /**
         * 写入数据
         */
        val writer: DataFrameWriter[_] = dataFrame.write.format("com.databricks.spark.csv").option("header", "true")
        //            writer.save(args[2]);
        writer.save(p.outputDataPath)
      } finally if (sparkContext != null) spark.stop()
    }
  }

}
