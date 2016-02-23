package generator;

import java.util.LinkedHashSet;

import configurations.Configuration;
import configurations.*;

public class Config
{
	public static final int NUMBER_OF_TESTCASES = 30;
	
	public static LinkedHashSet<Configuration> getConfigsToRun(int runNumber)
	{
		LinkedHashSet<Configuration> configs = new LinkedHashSet<Configuration>();
		configs.add(new Test_Sizes(runNumber));
		return configs;
	}
}
