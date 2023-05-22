import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {
  
  public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>{
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
    
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
        context.write(word, one);
      }
    }
  }
  
  public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    private IntWritable result = new IntWritable();
    
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
    }
  }
  
  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(WordCount.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}

/* 
To compile and run the code, you can follow these steps:

Save the code in a file named WordCount.java.
Open the terminal and navigate to the directory containing the WordCount.java file.
Compile the code using the following command:
bash
Copy code
javac -cp $(hadoop classpath) WordCount.java
Create an input directory in HDFS and copy some input files into it:
css
Copy code
hdfs dfs -mkdir input
hdfs dfs -put <input-files> input/
Run the MapReduce job using the following command:
lua
Copy code
hadoop jar WordCount.jar WordCount input output
Replace WordCount.jar with the name of the JAR file generated from the compiled Java code.
After the job completes, you can view the output using the following command:
bash
Copy code
hdfs dfs -cat output/part-r-00000
This will display the word count results on the terminal.
Make sure to replace <input-files> with the actual input file(s) you want to process. The output will be stored in the output directory in HDFS.

Note: This example assumes that you have a Hadoop distribution installed and configured on your local standalone setup.
*/