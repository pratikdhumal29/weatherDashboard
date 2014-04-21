package weather.wind.max;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class FirstReducer extends MapReduceBase implements Reducer<IntWritable, Text, IntWritable, Text> {

	private static final int WIND_TRESHOLD = 280;
	/*private static final int WIND_TRESHOLD;
	
	public void configure(JobConf job) {
		WIND_TRESHOLD = Integer.parseInt(job.get("WIND_TRESHOLD"));
	}*/
	
	@Override
	public void reduce(IntWritable inputKey, Iterator<Text> inputValue,
			OutputCollector<IntWritable, Text> output, Reporter arg3)
			throws IOException
	{	
		// substring infos:
		// ID 0-6
		// date 6-18
		// geoloc 18-30
		// wind 30-34
		// rain 34-??
		
		// input = id + date + geoloc + wind + rain

		final ArrayList<String> xtremWindInfo = new ArrayList<String>();
		
		while(inputValue.hasNext())
		{
			String infoString = inputValue.next().toString();
			int wind = Integer.parseInt(infoString.substring(30, 34));
			
			if (wind > WIND_TRESHOLD) // rainFall > RAIN_TRESHOLD
				xtremWindInfo.add(infoString);
		}
		
		for(int i=0; i<xtremWindInfo.size(); i++)
		{
			IntWritable outputKey = new IntWritable(Integer.parseInt(xtremWindInfo.get(i).substring(6, 14)));
			Text outputValue = new Text(xtremWindInfo.get(i));
			output.collect(outputKey, outputValue);
		}
	}
}
