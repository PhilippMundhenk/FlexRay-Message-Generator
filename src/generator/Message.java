package generator;

import java.util.Collection;

/**
 * Helping class
 * 
 * @author TUM CREATE RP03 - Philipp Mundhenk
 *
 * @param <receiverType>
 * type of the receiver (e.g. String)
 */
public class Message<receiverType>
{
	public String sender;
	public String name;
	public int length;
	public double deadline;
	public double period;
	public Collection<receiverType> receivers;
	public int mode;
}
