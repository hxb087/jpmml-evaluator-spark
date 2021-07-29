package org.jpmml.evaluator.spark;

import com.beust.jcommander.JCommander;

import java.util.List;


/**
 * @author: ljf
 * @date: 2021/7/26 16:15
 * @description:
 * @modified By:
 * @version: $ 1.0
 */
public class ParamsParser {

    public static <T> void parse(T t, String[] args) {
        JCommander.newBuilder()
                .addObject(t)
                .build()
                .parse(args);
    }

    public static <T> void parse(T t, List<String> args) {
        JCommander.newBuilder()
                .addObject(t)
                .build()
//                .parse((String[]) args.toArray());
                .parse(args.toArray(new String[0]));


    }


}
