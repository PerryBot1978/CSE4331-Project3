package org.dtb9096.hadoop.job1;

import org.apache.commons.text.StringTokenizer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class BasicActorJoinReducer extends Reducer<Text, Text, Text, Text> {
    protected Text out_key = new Text();
    protected Text out_value = new Text();

    /**
     * Combines the actors from each movie each into a record of the form
     * key = actorID <br>
     * value = actorName,year
     * @param key The unique titleID of a single movie
     * @param values A list of records, one tagged "movie" and the rest tagged "actor"
     * @param context The context the output will be written to
     * @throws IOException Thrown by Context.write()
     * @throws InterruptedException Thrown by Context.write()
     */
    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // Search for the movie record, and get the year
        String year = "";
        for (Text value : values) {
            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            if (itr.nextToken().equals("movie")) {
                year = itr.nextToken();
                break;
            }
        }
        assert !year.isEmpty();

        // Go through all the records, skipping the movie
        // and write a record to context
        for (Text value : values) {
            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            if (itr.nextToken().equals("actor")) {
                String actorID = itr.nextToken();
                String actorName = itr.nextToken();

                out_key.set(actorID);
                out_value.set(year + "," + actorName);
                context.write(out_key, out_value);
            }
        }
    }
}
