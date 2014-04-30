package weather.nearest_neighbor.advanced;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.JobConf;

public class AverageMonthYearMapper extends MapReduceBase implements
	Mapper<LongWritable, Text, Text, IntWritable> {
	
	private static Integer referenceYear;
	private static int temp;
	private static String stationYearMonthDay;
	
	public void configure(JobConf job) {
		referenceYear = Integer.parseInt(job.get("referenceYear"));
	}

	public void map(LongWritable key, Text value,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
			throws IOException {
		
		String[] lineArray = value.toString().split("\\s+");
		temp = Integer.parseInt(lineArray[1]);
		stationYearMonthDay = lineArray[0];
		
		if (stationYearMonthDay.substring(6,10).equals(referenceYear.toString())){
			output.collect(new Text(stationYearMonthDay), new IntWritable(temp));
		}
	}
}
