package org.dtb9096.hadoop.job2;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class TitleCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    private final IntWritable out_value = new IntWritable(0);

    private static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
        }  catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Reduces the (Year, List of 1s) to (Year, Sum)
     * @param key The year in question
     * @param values The list of 1s
     * @param context The context the output will be written to
     * @throws IOException Thrown by Context.write()
     * @throws InterruptedException Thrown by Context.write()
     */
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        // If the year is not a valid integer, don't do anything
        // The given files seem to have an entry where the year is \N
        // Which is not useful for this project
        if (!isInt(key.toString())) {
            return;
        }

        int count = 0;
        for (IntWritable val : values) {
            count += val.get();
        }

        out_value.set(count);
        context.write(key, out_value);
    }
}
