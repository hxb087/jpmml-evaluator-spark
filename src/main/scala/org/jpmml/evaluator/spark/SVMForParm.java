package org.jpmml.evaluator.spark;

import com.beust.jcommander.Parameter;
/**
 * @author ：huxb
 * @date ：2021/7/28 16:24
 * @description：TODO
 * @modified By：
 * @version: $ 1.0
 */
public class SVMForParm {

    @Parameter(names = "--outputDataPath", description = "output data path")
    protected String outputDataPath;

    @Parameter(names = "--inputModelPath", description = "input model path")
    protected String inputModelPath;

    @Parameter(names = "--inputDataPath", description = "input data path")
    protected String inputDataPath;

    public String getOutputDataPath() {
        return outputDataPath;
    }

    public String getInputModelPath() {
        return inputModelPath;
    }


    public String getInputDataPath() {
        return inputDataPath;
    }

}
