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
			if (msg.getConversationId().equals("Start"))
			{
				ACLMessage reply = msg.createReply();
				
				printVotingOrder(agent.getOptions());
				
				if (msg.getContent().equals("Plurality"))
					reply.setContent(pluralityVoting());
				else if (msg.getContent().equals("Borda"))
					reply.setContent(bordaVoting());
				else if (msg.getContent().equals("Sequential"))
					reply.setContent(pluralityVoting());
				
				this.agent.send(reply);
			}
			else if (msg.getConversationId().equals("Turn"))
			{
				ACLMessage reply = msg.createReply();
				reply.setContent(pluralityVoting(msg.getContent()));	
				this.agent.send(reply);
			}
			else if (msg.getConversationId().equals("Done"))
				this.done = true;
			
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
//		String[] choice = shuffle(agent.getOptions());
		String[] choice = agent.getOptions();
		String text = "";		
		
		for (int i = 0; i < choice.length; i++)
			text+= choice[i] + " ";
				
		return text;
	}
	
	private String pluralityVoting(String content)
	{
		String[] options = agent.getOptions();
		int middle = content.indexOf(' ');
		
		String new_options[] = { content.substring(0, middle), content.substring(middle+1) };
		
		int i = 0;
		
		while(!options[i].equals(new_options[0]) && !options[i].equals(new_options[1]))
			i++;
		
		return options[i];
	}
	
	private String pluralityVoting()
	{
//		return shuffle(agent.getOptions())[0];
		return agent.getOption(0);
	}
	
//	private String[] shuffle(String[] options)
//	{
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
//		printVotingOrder(options);
//		
//		return options;
//	}
	
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