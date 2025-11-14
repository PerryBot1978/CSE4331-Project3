package org.dtb9096.hadoop.job2;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class TitleCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    private final IntWritable out_value = new IntWritable(0);

    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        for (IntWritable val : values) {
            count += val.get();
        }

        out_value.set(count);
        context.write(key, out_value);
    }
}
