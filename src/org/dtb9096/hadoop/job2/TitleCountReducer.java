package org.dtb9096.hadoop.job2;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class TitleCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    private final IntWritable out_value = new IntWritable(0);

    /**
     * Reduces the (Year, List of 1s) to (Year, Sum)
     * @param key The year in question
     * @param values The list of 1s
     * @param context The context the output will be written to
     * @throws IOException Thrown by Context.write()
     * @throws InterruptedException Thrown by Context.write()
     */
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        for (IntWritable val : values) {
            count += val.get();
        }

        out_value.set(count);
        context.write(key, out_value);
    }
}
