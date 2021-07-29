package org.jpmml.evaluator.spark;

/**
 * @author ：huxb
 * @date ：2021/7/27 17:14
 * @description：TODO
 * @modified By：
 * @version: $ 1.0
 */


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Transformer;
import org.apache.spark.sql.*;
import org.jpmml.evaluator.Evaluator;

import java.io.File;
import java.io.FileInputStream;

import com.beust.jcommander.Parameter;


//todo use SVMExample.scale deprecated this java class

public class SVMEvaluationSparkExample {

    static
    public void main(String... args) throws Exception {


//        if (args.length != 3) {
//            System.err.println("Usage: java " + SVMEvaluationSparkExample.class.getName() + " <PMML file> <Input file> <Output directory>");
//
//            System.exit(-1);
//        }
        /**
         * 根据pmml文件，构建模型
         */
//        FileSystem fs = FileSystem.get(new Configuration());

        File file = new File("data/part-00000");
        FileInputStream inputStream = new FileInputStream(file);
        Evaluator evaluator = EvaluatorUtil.createEvaluator(inputStream);

//        Evaluator evaluator = EvaluatorUtil.createEvaluator(fs.open(new Path(args[0])));

        TransformerBuilder modelBuilder = new TransformerBuilder(evaluator)
                .withTargetCols()
                .withOutputCols()
                .exploded(true);

        Transformer transformer = modelBuilder.build();

        /**
         * 利用DataFrameReader从原始数据中构造 DataFrame对象
         * 需要原始数据包含列名
         */
        SparkConf conf = new SparkConf().setMaster("local").setAppName("svm");
        try (JavaSparkContext sparkContext = new JavaSparkContext(conf)) {

            SQLContext sqlContext = new SQLContext(sparkContext);

            DataFrameReader reader = sqlContext.read()
                    .format("com.databricks.spark.csv")
                    .option("header", "true")
                    .option("inferSchema", "true");
//           Dataset<Row> dataFrame = reader.load(args[1]);// 输入数据需要包含列名

            Dataset<Row> dataFrame = reader.load("data/sample_test_data.txt");
            /**
             * 使用模型进行预测
             */
            dataFrame = transformer.transform(dataFrame);

            /**
             * 写入数据
             */
            DataFrameWriter writer = dataFrame.write()
                    .format("com.databricks.spark.csv")
                    .option("header", "true");

//            writer.save(args[2]);
            writer.save("sample_out00");
        }
    }
}
