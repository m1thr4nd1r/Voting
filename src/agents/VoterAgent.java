package agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import behaviour.ListenBehaviour;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

@SuppressWarnings("serial")
public class VoterAgent extends Agent 
{
	private String options[];
	
	protected void setup()
	{
		options = (String[]) getArguments();
		
		DFAgentDescription dfd = new DFAgentDescription();
	    ServiceDescription sd = new ServiceDescription();
		dfd.setName(getAID());
		sd.setType("voter");
		sd.setName("Voting");
		dfd.addServices(sd);
		
		try {  
	        DFService.register(this, dfd );  
	    }
	    catch (FIPAException fe) { fe.printStackTrace(); }
		
		shuffle(options);
		
		this.addBehaviour(new ListenBehaviour(this));
	}

	protected void takeDown() 
	{
		try { DFService.deregister(this); }
		catch (Exception e) {}
	}
	
	public String[] getOptions() 
	{
		return options;
	}
	
	public String getOption(int index) 
	{
		return options[index];
	}
	
	public String[] shuffle(String[] options)
	{
//		int i,j;
//		Random generator = new Random();
//		
//		for (i = options.length - 1; i > 0; i--)
//		{
//			j = generator.nextInt(i);
//			String temp = options[i];
//			options[i] = options[j];
//			options[j] = temp;
//		}
		
		ArrayList<String> op = new ArrayList<String>(Arrays.asList(options));
		Collections.shuffle(op);
		this.options = op.toArray(new String[op.size()]);
		
		return options;
	}	
	
//	Needed for testing
	public void setOptions(String[] op)
	{
		this.options = op;
	}
}