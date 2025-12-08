package org.dtb9096.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import org.dtb9096.hadoop.job1.BasicActorJoinReducer;
import org.dtb9096.hadoop.job1.TitleActorsMapper;
import org.dtb9096.hadoop.job1.TitleBasicsMapper;
import org.dtb9096.hadoop.job2.ActorFilterMapper;
import org.dtb9096.hadoop.job2.TitleCountReducer;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        long startTime = System.currentTimeMillis();

        TextOutputFormat.SEPARATOR = "\t";
        Configuration conf = new Configuration();
        String[] arguments = new GenericOptionsParser(conf, args).getRemainingArgs();
        Path input1 = new Path(arguments[0]);
        Path input2 = new Path(arguments[1]);
        Path intermediate = new Path("temp.txt");
        Path output = new Path(arguments[2]);

        int mapperCount1 = Integer.parseInt(arguments[3]);
        int reducerCount1 = Integer.parseInt(arguments[4]);
        int mapperCount2 = Integer.parseInt(arguments[5]);
        int reducerCount2 = Integer.parseInt(arguments[6]);

        JobConf conf1 = new JobConf(conf);
        conf1.setNumMapTasks(mapperCount1);
        conf1.setNumReduceTasks(1); // There can only be one

        Job job1 = Job.getInstance(conf1, "title/actor join");
        job1.setJarByClass(Main.class);

        MultipleInputs.addInputPath(job1, input1, TextInputFormat.class, TitleBasicsMapper.class);
        MultipleInputs.addInputPath(job1, input2, TextInputFormat.class, TitleActorsMapper.class);
        TextOutputFormat.setOutputPath(job1, intermediate);

        job1.setReducerClass(BasicActorJoinReducer.class);

        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(Text.class);

        boolean result = job1.waitForCompletion(true);
        if (!result) {
            System.exit(1);
        }

        JobConf conf2 = new JobConf(conf);
        conf2.setNumMapTasks(mapperCount2);
        conf2.setNumReduceTasks(reducerCount2);

        Job job2 = Job.getInstance(conf2, "actor count");
        job2.setJarByClass(Main.class);
        job2.setInputFormatClass(KeyValueTextInputFormat.class);

        KeyValueTextInputFormat.addInputPath(job2, intermediate);
        TextOutputFormat.setOutputPath(job2, output);

        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(IntWritable.class);

        job2.setMapperClass(ActorFilterMapper.class);
        job2.setReducerClass(TitleCountReducer.class);

        int code = job2.waitForCompletion(true) ? 0 : 1;
        long finishTime = System.currentTimeMillis();
        long elapsedTime = finishTime - startTime;

        System.out.printf("[TIME] %d%n", elapsedTime);
        System.exit(code);
    }
}