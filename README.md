# FlexRay Message Generator
This is a very simple message generator for FlexRay messages. This generator supports the generation of multimode messages, i.e. messages which can switch their period, deadline and message length depending on the situation a vehicle experiences.

## Configuration
The following parameters can be configured (see Configuration.java):
- periodsInCycles: periods that can be used for messages, given in cycles of length cycleLengthMillis
- cycleLengthMillis: length of a cycle
- maxNumberPeriodsPerECU: maximum number of different periods per ECU
- deadlinePeriodRatio: deadline/period
- numberOfMessages: number of messages to generate
- numberOfSenders: number of ECUs in the system
- ecuRatios: distribution of traffic onto ECUs, array needs to be as long as number of ECUs
- messageSizes: possible message sizes
- messageSizeRatios: ratios of message sizes, needs to be as long as message size array
- filename_newFormat/filename_oldFormat: files to save generated messages to
- multiMode_on: multi-mode settings (true/false)
- multiMode_numberOfModes: number of modes, currently only 2 supported
- multiMode_PeriodRatio: minimum ratio of longest message period/shortest message period (0: disabled/free selection)
- multiMode_SizeRatio: minimum ratio of longest message size/shortest message size (0: disabled/free selection)
- multiMode_identicalModeRatio: ratio of messages with all configs identical (0: no identical configs), in relation to 1 for multi-mode messages (e.g.: 2=double amount of non-multi-mode messages than multi-mode msgs)

## Output Format
Message lists are saved in the following formats:
### Old Format (a legacy format used for older projects)
{List of ECUs} <br />
{Message1: Sender,Name,Length,Period/Deadline,Receiver1,Receiver2,...} <br />
{...} <br />
 <br />
Example: <br />
ECU_0,ECU_1,ECU_2,ECU_3, <br />
ECU_0,frame0,8,5.0,ECU_3,ECU_1,ECU_2 <br />
ECU_1,frame1,10,5.0,ECU_0,ECU_3 <br />
ECU_3,frame2,10,5.0,ECU_0,ECU_1,ECU_2 <br />

### New Format (a slightly more extensive format):
{Number of modes (if more than one mode exits for any message)}
{List of ECUs}
{Message1: Sender,Name,Mode,Length,Period,Deadline,Receiver1,Receiver2,...} <br />
{...} <br />
 <br />
Example: <br />
2 <br />
ECU_0,ECU_1, <br />
ECU_1,frame0,0,15,50.0,50.0,ECU_0, <br />
ECU_1,frame1,1,3,5.0,5.0,ECU_0, <br />
ECU_0,frame2,1,9,60.0,60.0,ECU_1, <br />
ECU_0,frame3,0,21,120.0,120.0,ECU_1, <br />

## Reference
Please refer to this project either via this repository or via the paper it was built for:

Philipp Mundhenk, Florian Sagstetter, Sebastian Steinhorst, Martin Lukasiewycz, Samarjit Chakraborty. "Policy-based Message Scheduling Using FlexRay". In: Proceedings of the 12th International Conference on Hardware/Software Codesign and System Synthesis (CODES+ISSS 2014). India, pp. 19:1–19:10. DOI: 10.1145/2656075.2656094

### BibTeX: <br />
@inproceedings{msslc:2014, <br />
	doi = { 10.1145/2656075.2656094 }, <br />
	pages = { 19:1--19:10 }, <br />
	year = { 2014 }, <br />
	location = { India }, <br />
	booktitle = { Proceedings of the 12th International Conference on Hardware/Software Codesign and System Synthesis (CODES+ISSS 2014) }, <br />
	author = { Philipp Mundhenk and Florian Sagstetter and Sebastian Steinhorst and Martin Lukasiewycz and Samarjit Chakraborty }, <br />
	title = { Policy-based Message Scheduling Using FlexRay }, <br />
}