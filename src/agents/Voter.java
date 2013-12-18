package agents;

import jade.core.Agent;

@SuppressWarnings("serial")
public class Voter extends Agent {

	private String options[];
	
	public Voter() 	{	
	}
	
	protected void setup()
	{
		options = (String[]) getArguments();
		
		for (int i = 0; i < options.length; i++)
			System.out.println("Opção " + (i+1) + ": " + options[i]);
	}	
}
