package org.dtb9096.hadoop.job1;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class TitleActorsMapper extends Mapper<Object, Text, Text, Text> {
    protected Text out_key = new Text();
    protected Text out_value = new Text();

    /**
     * Maps each line from the imdb00.title.actors.csv into a record with
     * titleID as the key and
     * "actor",actorID,actorName as the value
     * @param key Unused
     * @param value A single line from imdb00.title.actors.csv
     * @param context The context the output will be written to
     * @throws IOException Thrown by Context.write()
     * @throws InterruptedException Thrown by Context.write()
     */
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer itr = new StringTokenizer(value.toString(), ",");
        assert itr.countTokens() == 3;

        String titleID = itr.nextToken();
        String actorID = itr.nextToken();
        String actorName = itr.nextToken();

        out_key.set(titleID);
        out_value.set("actor," + actorID + "," + actorName);

        context.write(out_key, out_value);
    }
}
