package org.dtb9096.hadoop.job1;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class TitleBasicsMapper extends Mapper<Object, Text, Text, Text> {
    protected Text out_key = new Text();
    protected Text out_value = new Text();

    /**
     * Maps each line from the imdb00.title.basics.tsv into a record with
     * titleID as the key and
     * "movie",year as the value
     * if and only if titleType is "movie" or "tvMovie"
     * @param key Unused
     * @param value A single line from imdb00.title.basics.tsv
     * @param context The context the output will be written to
     * @throws IOException Thrown by Context.write()
     * @throws InterruptedException Thrown by Context.write()
     */
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer itr = new StringTokenizer(value.toString(), "\t");
        assert itr.countTokens() == 5;

        String titleID = itr.nextToken();
        itr.nextToken();
        itr.nextToken();
        itr.nextToken();
        itr.nextToken();
        // String titleType = itr.nextToken();
        // String title = itr.nextToken();
        String year = itr.nextToken();
        // String genres = itr.nextToken();

        out_key.set(titleID);
        out_value.set("movie," + year);

        if (titleType.equals("movie") || titleType.equals("tvMovie")) {
            context.write(out_key, out_value);
        }
    }
}
