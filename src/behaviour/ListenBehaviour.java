package behaviour;

import java.util.Random;

import agents.VoterAgent;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class ListenBehaviour extends CyclicBehaviour
{
	private VoterAgent agent;
	
	public ListenBehaviour(Agent a)
	{
		this.myAgent = a;
		this.agent = (VoterAgent) a;
	}
	
	public void action()
	{
		ACLMessage msg = myAgent.receive();
		boolean flag = false;
		
		while (msg != null && msg.getConversationId() != null)
		{
			if (msg.getConversationId().equals("Start"))
			{
				if (msg.getContent().equals("Simple"))
				{
					ACLMessage reply = msg.createReply();
					reply.setContent(simpleVoting());
					this.myAgent.send(reply);
				}
			}
			else if (msg.getConversationId().equals("Received"))
				flag = true;
			
			msg = myAgent.receive();
		}
		
		if (flag)
			myAgent.removeBehaviour(this);
	}
	
	private String simpleVoting()
	{
		return shuffle()[0];		
	}
	
	private String[] shuffle()
	{
		int i,j;
		Random generator = new Random();
		String[] options = agent.getOptions();
		
		for (i = options.length - 1; i > 0; i--)
		{
			j = generator.nextInt(i);
			String temp = options[i];
			options[i] = options[j];
			options[j] = temp;
		}
		print(options);
		
		return options;
	}
	
	private void print(String[] options)
	{
		int i;
		System.out.println("Ordem de prioridade das opções do eleitor " + extractNumber(myAgent.getName()) + " : ");
		for (i = 0; i < options.length; i++)
			System.out.print(options[i] + " ");
		System.out.println();
	}
	
	public String extractNumber(String s)
	{
		int end = s.indexOf('@',0);
		int begin = s.lastIndexOf('t', end);
		
		return s.substring(begin + 1, end);
	}
}