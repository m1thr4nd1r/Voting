package agents;

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
	
//	Needed for testing
	public void setOptions(String[] op)
	{
		this.options = op;
	}
}