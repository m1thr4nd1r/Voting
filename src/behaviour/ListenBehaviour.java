package behaviour;

//import java.util.Random;

import agents.VoterAgent;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class ListenBehaviour extends SimpleBehaviour
{
	private VoterAgent agent;
	private boolean done;
	
	public ListenBehaviour(Agent a)
	{
		this.myAgent = a;
		this.agent = (VoterAgent) a;
		this.done = false;
	}
	
	public ListenBehaviour(){}

	public void setAgent(Agent a)
	{
		this.agent = (VoterAgent) a;
	}
	
	public void action()
	{
		ACLMessage msg = agent.receive();
		
		while (msg != null && msg.getConversationId() != null)
		{
			ACLMessage reply = msg.createReply();
			
			if (!msg.getConversationId().equals("Done"))
			{
				printVotingOrder(agent.getOptions());
						
				if (msg.getConversationId().equals("Plurality"))
					reply.setContent(pluralityVoting());
				else if (msg.getConversationId().equals("Borda"))
					reply.setContent(bordaVoting());
				else if (msg.getConversationId().equals("Sequential"))
					reply.setContent(sequentialVoting(msg.getContent()));
			}
//			else if (msg.getConversationId().equals("Turn"))
//			{
//				reply.setContent(pluralityVoting(msg.getContent()));	
//				this.agent.send(reply);
//			}
			else
				this.done = true;
						
			this.agent.send(reply);
						
			msg = agent.receive();
		}		
	}
	
	public int find(String vote)
	{
		int i = 0;
		
		while (!agent.getOption(i).equals(vote))
			i++;
		
		return i;
	}
	
	private String bordaVoting()
	{
		String[] choice = agent.getOptions();
		String text = "";		
		
		for (int i = 0; i < choice.length; i++)
			text+= choice[i] + " ";
				
		return text;
	}
	
	private String sequentialVoting(String content)
	{
		String[] options = agent.getOptions();
		int middle = content.indexOf(" ");
		
		String new_options[] = { content.substring(0, middle), content.substring(middle+1) };
		
		int i = 0;
		
		try {
			
		while(!options[i].equals(new_options[0]) && !options[i].equals(new_options[1]))
			i++;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Options " + new_options[0] + " " + new_options[1]);
			for (int j = 0; j < 5; j++)
				System.out.print(options[j]);
			System.out.println();
		}
		
		return options[i];
	}
	
	private String pluralityVoting()
	{
		return agent.getOption(0);
	}
	
	private void printVotingOrder(String[] options)
	{
		int i;
		String text;
		
		text = "Ordem de prioridade das opcoes do eleitor " + extractNumber(myAgent.getName()) + " : ";
		for (i = 0; i < options.length; i++)
			text+= options[i] + " ";
		
		System.out.println(text);
	}
	
	public String extractNumber(String s)
	{
		int end = s.indexOf('@',0);
		int begin = s.lastIndexOf('t', end);
		
		return s.substring(begin + 1, end);
	}
	
	public int onEnd()
	{
		agent.doDelete();
		myAgent.doDelete();
		return 0;
	}

	@Override
	public boolean done() {
		return done;
	}
}