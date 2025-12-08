package org.dtb9096.hadoop.job2;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class ActorFilterMapper extends Mapper<Text, Text, Text, IntWritable> {
    public static final String ACTOR = "tom holland";

    protected Text out_key = new Text();
    protected IntWritable out_value = new IntWritable(1);

    /**
     * Maps a record of form (actorID, year, actorName) into
     * a record of form (actorID, year)
     * @param key Unused
     * @param value A single line from imdb00.title.basics.tsv
     * @param context The context the output will be written to
     * @throws IOException Thrown by Context.write()
     * @throws InterruptedException Thrown by Context.write()
     */
    @Override
    public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer itr = new StringTokenizer(value.toString(), ",");
        assert itr.countTokens() == 2;

        String year = itr.nextToken();
        String actorName = itr.nextToken();

        if (actorName.equalsIgnoreCase(ACTOR)) {
            out_key.set(year);
            context.write(out_key, out_value);
        }
    }
}
