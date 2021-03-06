package weather.rain;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class RainAnomalies {

	public static void main(String[] args) throws Exception {
		if (args.length != 6) {
			System.err.println("Usage: CMD <input path> <output Path> <begin year> <end year> <deviation> <time granularity>");
			System.exit(-1);
		}
		int begin = Integer.parseInt(args[2]);
		int end = Integer.parseInt(args[3]);
		String granularity="";
		if(args[5].equals("month") || args[5].equals("day"))
			granularity = args[5];
		else{
			System.err.println("Only allowed values for time granularity are \"month\" and \"day\"");
			System.exit(-1);
		}

		Job job2 = RainAnomalies.getJobAvgYearMonth(args[0], args[1]+"-tmp");
		job2.getConfiguration().setInt("beginyear", begin);
		job2.getConfiguration().setInt("endyear", end);
		job2.getConfiguration().set("granul", granularity);
		job2.waitForCompletion(true);

		Job job3 = RainAnomalies.getJobAnomaliesDetection(args[1]+"-tmp", args[1]);
		job3.getConfiguration().set("deviation",args[4]);
		job3.getConfiguration().setInt("beginyear", begin);
		job3.getConfiguration().setInt("endyear", end);
		job3.getConfiguration().set("granul", granularity);
		job3.waitForCompletion(true);

		System.exit(0);

	}

	public static Job getJobAnomaliesDetection(String inputPath1, String outputPath) throws IOException {
		Job job = Job.getInstance();
		job.setJarByClass(RainAnomalies.class);
		job.setJobName("Detecting of Anomalies in Rainfall");

		job.setMapperClass(AnomaliesDetectionMapper.class);
		job.setReducerClass(AnomaliesDetectionReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		fs.delete(new Path(outputPath), true);
		FileInputFormat.addInputPath(job, new Path(inputPath1));
		//FileInputFormat.addInputPath(job, new Path(inputPath2));

		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		return job;
	}

	public static Job getJobAvgYearMonth(String inputPath, String outputPath) throws IOException {
		Job job = Job.getInstance();
		job.setJarByClass(RainAnomalies.class);
		job.setJobName("Average YearMonth Rainfall");

		job.setMapperClass(AvgYearMonthMapper.class);
		job.setReducerClass(AvgYearMonthReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);

		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		fs.delete(new Path(outputPath), true);
		if (inputPath.endsWith(".txt") || inputPath.endsWith(".gz"))
			FileInputFormat.addInputPath(job, new Path(inputPath));
		else {
			FileStatus[] status_list = fs.listStatus(new Path(inputPath));
			if (status_list != null) {
				for (FileStatus status : status_list) {
					FileInputFormat.addInputPath(job, status.getPath());
				}
			}
		}
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		return job;
	}



}
