package weather.temperature.anomalies.grid;

import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class StationsMonthYearAverageReducer extends MapReduceBase implements
		Reducer<Text, Text, Text, Text> {
	
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {

		// HashMap<String,Integer> monthAverage = new HashMap<String, Integer>();
		// HashMap<String,Integer> nbRecords = new HashMap<String, Integer>();

		String[] keyVals = key.toString().split(",");
		String coord = keyVals[0];
		String year = keyVals[1];
		String month = keyVals[2];
		int temperature = 0;
		int nbRec = 0;
		
		while (values.hasNext()) {
			String[] value = values.next().toString().split(",");
			//String year = value[0];
			//String month = value[1];

			//int temperature = Integer.parseInt(value[2]);	
			temperature += Integer.parseInt(value[0]);
			nbRec++;
			/*
			if (!monthAverage.containsKey(month+","+year)) {
				monthAverage.put(month+","+year, 0);
				nbRecords.put(month+","+year, 0);
			}
			monthAverage.put(month+","+year,monthAverage.get(month+","+year) + temperature);
			nbRecords.put(month+","+year,monthAverage.get(month+","+year) + 1);
			*/

		}
		int average = temperature/nbRec;
		output.collect(new Text(coord), new Text(month+","+year+","+average));
		/*for (String monthyear : monthAverage.keySet()) {
			int avg = monthAverage.get(monthyear)/nbRecords.get(monthyear);
			output.collect(key, new Text(monthyear+","+avg));
		}*/
	}
}