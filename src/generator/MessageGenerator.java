package generator;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;

import configurations.Configuration;

/**
 * Message generator to generate FlexRay messages.
 * Find and adjust the configuration below.
 * Messages are saved to a *.txt file in the format: 
 * sender, message name, length in bytes, period in ms, deadline in ms, {receivers}
 * 
 * @author TUM CREATE RP03 - Philipp Mundhenk
 *
 */
public class MessageGenerator
{	
	private Configuration config;
	private int ecuRatiosCumulated[];
	private int messageSizeRatiosCumulated[];
	private Random rand = new Random();
	private LinkedHashSet<String> ecuNames = new LinkedHashSet<String>();
	private double periodsInMillis[];
	private HashMap<String, LinkedHashSet<Double>> ecuPeriods = new HashMap<String, LinkedHashSet<Double>>();
	private FileOutputStream fstream;
	private FileOutputStream fstream_oldFormat;
	private DataOutputStream outStream;
	private DataOutputStream outStream_oldFormat;
	private BufferedWriter bw;
	private BufferedWriter bw_oldFormat;
	private Double avgMsgLength = 0.0;
	
	public MessageGenerator(Configuration config)
	{
		this.config = config;
		
		ecuRatiosCumulated = new int[config.getEcuratios().length];
		messageSizeRatiosCumulated = new int[config.getMessagesizeratios().length];
		periodsInMillis = new double[config.getPeriodsincycles().length];
	}
	
	/**
	 * This method initializes the system.
	 */
	private void init()
	{
		/* generate ECU names */
		for (int i = 0; i < config.getNumberofsenders(); i++)
		{
			ecuNames.add("ECU_" + i);
		}
		for (Iterator<String> i = ecuNames.iterator(); i.hasNext();)
		{
			String ecu = (String) i.next();
			
			ecuPeriods.put(ecu, new LinkedHashSet<Double>());
		}
		
		for (int i = 0; i < config.getPeriodsincycles().length; i++)
		{
			periodsInMillis[i] = config.getPeriodsincycles()[i]*config.getCyclelengthmillis();
		}
		
		for (int i = 0; i < config.getEcuratios().length; i++)
		{
			if(i == 0)
			{
				ecuRatiosCumulated[i] = config.getEcuratios()[i];
			}
			else
			{
				ecuRatiosCumulated[i] = ecuRatiosCumulated[i-1] + config.getEcuratios()[i];
			}
		}
		for (int i = 0; i < config.getMessagesizeratios().length; i++)
		{
			if(i == 0)
			{
				messageSizeRatiosCumulated[i] = config.getMessagesizeratios()[i];
			}
			else
			{
				messageSizeRatiosCumulated[i] = messageSizeRatiosCumulated[i-1] + config.getMessagesizeratios()[i];
			}
		}
		
		try
		{
			fstream = new FileOutputStream(config.getFilenameNewformat());
			fstream_oldFormat = new FileOutputStream(config.getFilenameOldformat());
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		outStream = new DataOutputStream(fstream);
		outStream_oldFormat = new DataOutputStream(fstream_oldFormat);
		bw = new BufferedWriter(new OutputStreamWriter(outStream));
		bw_oldFormat = new BufferedWriter(new OutputStreamWriter(outStream_oldFormat));
	}
	
	/**
	 * This is the main function of the message generator.
	 * 
	 * @param args
	 * unused
	 */
	public static void main(String[] args)
	{
		HashMap<String, Double> msgLengths = new HashMap<String, Double>();
		for (int j = 1; j <= Config.NUMBER_OF_TESTCASES; j++)
		{
			for (Iterator<Configuration> i = Config.getConfigsToRun(j).iterator(); i.hasNext();)
			{
				Configuration config = (Configuration) i.next();
				
				MessageGenerator msgGen = new MessageGenerator(config);
				msgGen.generateMessages();
				msgLengths.put(config.getFilename(), msgGen.getAvgMsgLength());
			}
			
			double average = 0;
			System.out.println();
			System.out.println("average message lengths (byte):");
			for (Iterator<Map.Entry<String, Double>> i = msgLengths.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry<String, Double> entry= (Map.Entry<String, Double>) i.next();
				average+=entry.getValue();
				System.out.println(entry.getKey()+", "+entry.getValue());
			}
			System.out.println("overall average (byte): "+(average/msgLengths.size()));
		}
	}
	
	/**
	 * This method generates and saves the messages.
	 */
	private void generateMessages()
	{
		init();
		
		/* generate messages */
		LinkedHashSet<Message<String>> messages  = new LinkedHashSet<Message<String>>();
		for (int i = 0; i < config.getNumberofmessages(); i++)
		{
			Message<String> msg = new Message<String>();
			
			msg.sender = getECU();
			msg.receivers = new LinkedHashSet<String>();
			for (int j = 0; j < rand.nextInt(config.getNumberofsenders()-1)+1; j++)
			{
				String ecu = getECU();
				if(!ecu.equals(msg.sender))
				{
					msg.receivers.add(ecu);
				}
				else
				{
					j--;
				}
			}
			msg.length = getMessageSize();
			msg.name = "frame"+i;
			while(0 == msg.period)
			{
				double period = periodsInMillis[rand.nextInt(periodsInMillis.length-1)];
				if(ecuPeriods.get(msg.sender).contains(period))
				{
					msg.period = period;
				}
				else if(ecuPeriods.get(msg.sender).size() <= config.getMaxnumberperiodsperecu())
				{
					msg.period = period;
					ecuPeriods.get(msg.sender).add(period);
				}
			}
			msg.deadline = msg.period*config.getDeadlineperiodratio();
			
			msg.mode = 0;
			
			if(isMultiMode())
			{
				msg.mode = 1;
				
				Message<String> multimodeMessage = new Message<String>();
				
				multimodeMessage.sender = msg.sender;
				multimodeMessage.receivers = msg.receivers;
				multimodeMessage.length = getLongSize(msg.length);
				multimodeMessage.name = msg.name;
				multimodeMessage.period = getLongPeriod(msg.period/config.getCyclelengthmillis());
				multimodeMessage.deadline = multimodeMessage.period*config.getDeadlineperiodratio();
				multimodeMessage.mode = 2;
				
				messages.add(multimodeMessage);
			}
			
			messages.add(msg);
		}
		
		for (Iterator<Message<String>> i = messages.iterator(); i.hasNext();)
		{
			Message<String> msg = (Message<String>) i.next();
			
			avgMsgLength += msg.length;
		}
		avgMsgLength /= messages.size();
		
		/* output and save messages */
		if(config.getMultimodeOn())
		{
			log(""+config.getMultimodeNumberofmodes(), false);
			logln("", false);
		}
		
		for (Iterator<String> i = ecuNames.iterator(); i.hasNext();)
		{
			String ecu = (String) i.next();
			log(ecu + ",", true);
			log(ecu + ",", false);
		}
		logln("", true);
		logln("", false);
		for (Iterator<Message<String>> i = messages.iterator(); i.hasNext();)
		{
			Message<String> msg = (Message<String>) i.next();
			
			log(msg.sender + ",", true);
			log(msg.sender + ",", false);
			
			log(msg.name + ",", true);
			log(msg.name + ",", false);
			
			if(config.getMultimodeOn())
			{
				log(msg.mode + ",", false);
			}
			
			log(msg.length + ",", true);
			log(msg.length + ",", false);
			
			log(msg.period + ",", true);
			log(msg.period + ",", false);
			
			log(msg.deadline + ",", false);
			for (Iterator<String> j = msg.receivers.iterator(); j.hasNext();)
			{
				String receiver = (String) j.next();
				
				if(j.hasNext())
				{
					log(receiver+",", true);
					log(receiver+",", false);
				}
				else
				{
					log(receiver, true);
					log(receiver, false);
				}
			}
			
			if(i.hasNext())
			{
				logln("", true);
				logln("", false);
			}
		}
		
		/* close file access */
		try
		{
			bw.close();
			bw_oldFormat.close();
			outStream.close();
			outStream_oldFormat.close();
			fstream.close();
			fstream_oldFormat.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private Boolean isMultiMode()
	{
		if((rand.nextDouble()*(config.getMultimodeIdenticalmoderatio()+1)) >= config.getMultimodeIdenticalmoderatio())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private Double getLongPeriod(Double smallPeriod)
	{
		int index = -1;
		for (int i = 0; i < config.getPeriodsincycles().length; i++)
		{
			if(config.getPeriodsincycles()[i] == smallPeriod*2)
			{
				index = i;
			}
		}
		
		if(index == -1)
		{
			return smallPeriod*config.getCyclelengthmillis();
		}
		else
		{
			return config.getPeriodsincycles()[rand.nextInt(config.getPeriodsincycles().length-index)+index]*config.getCyclelengthmillis();
		}
	}
	
	private Integer getLongSize(Integer shortSize)
	{
		int index = -1;
		for (int i = 0; i < config.getMessagesizes().length; i++)
		{
			if(config.getMessagesizes()[i] == shortSize*2)
			{
				index = i;
			}
		}
		
		if(index == -1)
		{
			return shortSize;
		}
		else
		{
			return config.getMessagesizes()[rand.nextInt(config.getMessagesizes().length-index)+index];
		}
	}
	
	/**
	 * This method returns an ECU selected randomly with the given distribution.
	 * 
	 * @return
	 * ECU name
	 */
	private String getECU()
	{
		int randomEcu = rand.nextInt(ecuRatiosCumulated[ecuRatiosCumulated.length-1])+1;
		int cnt = 0;
		while(true)
		{
			if(0 == cnt)
			{
				if(randomEcu <= ecuRatiosCumulated[cnt])
				{
					break;
				}
			}
			else
			{
				if((randomEcu <= ecuRatiosCumulated[cnt]) && (randomEcu > ecuRatiosCumulated[cnt-1]))
				{
					break;
				}
			}
			cnt++;
		}
		
		return (String)ecuNames.toArray()[cnt];
	}
	
	/**
	 * This method randomly selects a size for a message with the given distribution.
	 * 
	 * @return
	 * size of message in bytes
	 */
	private int getMessageSize()
	{
		int randomSize = rand.nextInt(messageSizeRatiosCumulated[messageSizeRatiosCumulated.length-1]+1);
		int cnt = 0;
		while(true)
		{
			if(0 == cnt)
			{
				if(randomSize <= messageSizeRatiosCumulated[cnt])
				{
					break;
				}
			}
			else
			{
				if((randomSize <= messageSizeRatiosCumulated[cnt]) && (randomSize > messageSizeRatiosCumulated[cnt-1]))
				{
					break;
				}
			}
			cnt++;
		}
		
		return config.getMessagesizes()[cnt];
	}
	
	/**
	 * This method logs a string to the console and the given textfile and adds a new line.
	 * 
	 * @param str
	 * string to log
	 */
	private void logln(String str, Boolean oldFormat)
	{
		logBase(str, true, oldFormat);
	}
	
	/**
	 * This method logs a string to the console and the given textfile.
	 * 
	 * @param str
	 * string to log
	 */
	private void log(String str, Boolean oldFormat)
	{
		logBase(str, false, oldFormat);
	}
	
	/**
	 * This method is the base for all logging functionality.
	 * 
	 * @param str
	 * string to log
	 * @param newLine
	 * true: add new line after string; 
	 * false: don't add new line after string
	 */
	private void logBase(String str, Boolean newLine, Boolean oldFormat)
	{
		if(newLine)
		{
			System.out.println(str);
			try
			{
				if(oldFormat)
				{
					bw_oldFormat.append(str);
					bw_oldFormat.newLine();
				}
				else
				{
					bw.append(str);
					bw.newLine();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.print(str);
			try
			{
				if(oldFormat)
				{
					bw_oldFormat.append(str);
				}
				else
				{
					bw.append(str);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public Double getAvgMsgLength()
	{
		return avgMsgLength;
	}
}
