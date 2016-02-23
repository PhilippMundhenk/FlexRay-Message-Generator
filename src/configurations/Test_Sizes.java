package configurations.paper.sizes.msgs100;

import java.io.File;

import configurations.Configuration;

public class Test_Sizes implements Configuration
{
	/*****************/
	/* CONFIGURATION */
	/*****************/
	/* periods that can be used for messages, given in cycles of length cycleLengthMillis */
	private static final double periodsInCycles[] = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,45,50,55,60,64,65,70,75,80,85,90,95,100,120,140,160,180,200};
	/* length of a cycle */
	private static final double cycleLengthMillis = 5;
	/* maximum number of different periods per ECU */
	private static final int maxNumberPeriodsPerECU = 3;
	/* deadline/period */
	private static final double deadlinePeriodRatio = 1;
	/* number of messages to generate */
	private static final int numberOfMessages = 100;
	/* number of ECUs in the system */
	private static final int numberOfSenders = 5;
	/* distribution of traffic onto ECUs, array needs to be as long as number of ECUs */
	private static final int ecuRatios[] = {1,1,1,1,1};
	/* possible message sizes */
	private static final int messageSizes[] =		{1,2,3,4,5,6,8,9,10,13,15,20,32,40,64};
	/* ratios of message sizes, needs to be as long as message size array */
	private static final int messageSizeRatios[] =	{1,1,1,1,1,5,5,5,20,20,20,30,70,70,50};
	/* file to save generated messages to */
	private static String filename;// = "Test_Periods1";
	private static String filename_newFormat;// 
	private static String filename_oldFormat;//
	
	/* multi-mode settings */
	private static final Boolean multiMode_on = true;
	/* number of modes, currently only 2 supported */
	private static final int multiMode_numberOfModes = 2;
	/* minimum ratio of longest message period/shortest message period (0: disabled/free selection) */
	private static final double multiMode_PeriodRatio = 10.0;
	/* minimum ratio of longest message size/shortest message size (0: disabled/free selection) */
	private static final double multiMode_SizeRatio = 5.0;
	/* ratio of messages with all configs identical (0: no identical configs), in relation to 1 for multi-mode messages (e.g.: 2=double amount of non-multi-mode messages than multi-mode msgs) */
	private static final double multiMode_identicalModeRatio = 1;
	
	public Test_Sizes(Integer runNumber)
	{
		filename = "Test"+runNumber+"_Sizes";
		filename_newFormat = new File("").getAbsolutePath().concat("/output/"+filename+"_newFormat.txt");
		filename_oldFormat = new File("").getAbsolutePath().concat("/output/"+filename+"_oldFormat.txt");
	}
	
	public double[] getPeriodsincycles()
	{
		return periodsInCycles;
	}
	public double getCyclelengthmillis()
	{
		return cycleLengthMillis;
	}
	public int getMaxnumberperiodsperecu()
	{
		return maxNumberPeriodsPerECU;
	}
	public double getDeadlineperiodratio()
	{
		return deadlinePeriodRatio;
	}
	public int getNumberofmessages()
	{
		return numberOfMessages;
	}
	public int getNumberofsenders()
	{
		return numberOfSenders;
	}
	public int[] getEcuratios()
	{
		return ecuRatios;
	}
	public int[] getMessagesizes()
	{
		return messageSizes;
	}
	public int[] getMessagesizeratios()
	{
		return messageSizeRatios;
	}
	public String getFilename()
	{
		return filename;
	}
	public String getFilenameNewformat()
	{
		return filename_newFormat;
	}
	public String getFilenameOldformat()
	{
		return filename_oldFormat;
	}
	public Boolean getMultimodeOn()
	{
		return multiMode_on;
	}
	public int getMultimodeNumberofmodes()
	{
		return multiMode_numberOfModes;
	}
	public double getMultimodePeriodratio()
	{
		return multiMode_PeriodRatio;
	}
	public double getMultimodeSizeratio()
	{
		return multiMode_SizeRatio;
	}
	public double getMultimodeIdenticalmoderatio()
	{
		return multiMode_identicalModeRatio;
	}
}
