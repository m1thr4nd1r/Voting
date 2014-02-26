package behaviour;

import agents.CreatorAgent;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class PoolingBehaviour extends CyclicBehaviour {
	
	private CreatorAgent agent;
	private int votesLeft;
	
	public PoolingBehaviour(Agent a, int numVotes)
	{
		this.myAgent = a;
		this.agent = (CreatorAgent) a;
		this.votesLeft = numVotes ;
	}

	@Override
	public void action() 
	{
		
		ACLMessage msg = myAgent.receive();
		
		while (msg != null && msg.getConversationId() != null)
		{
			if (msg.getConversationId().equals("Start"))
			{
				pool(msg.getContent(), extractNumber(msg.getSender().getName()));
				ACLMessage reply = msg.createReply();
				reply.setConversationId("Received");
				myAgent.send(reply);
			}
			
			msg = myAgent.receive();
		}
		
		if (votesLeft == 0)
			myAgent.removeBehaviour(this);
	}
	
	public void pool(String vote, String name)
	{
		int i = 0;
		
		while (!agent.getOption(i).equals(vote))
			i++;
		
		agent.increment(i);
		votesLeft--;
		
		System.out.println("Eleitor " + name + " - Vota na opção de numero " + (i+1) + " (" + agent.getOption(i) + "). Total de Votos desta opção: " + agent.getVotes(i));
	}
	
	public String extractNumber(String s)
	{
		int end = s.indexOf('@',0);
		int begin = s.lastIndexOf('t', end);
		
		return s.substring(begin + 1, end);
	}
}