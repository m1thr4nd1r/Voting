package behaviour;

import agents.CreatorAgent;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class PoolingBehaviour extends SimpleBehaviour {
	
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
				reply.setConversationId("Done");
				myAgent.send(reply);
			}
			
			msg = myAgent.receive();
		}
	}
	
	public void pool(String vote, String name)
	{
		int i = Integer.valueOf(vote);
		
		agent.increment(i);
		votesLeft--;
		
		System.out.println("Eleitor " + name + " - Vota na opção de numero " + i + " (" + agent.getOption(i) + "). Total de Votos desta opção: " + agent.getVotes(i));
	}
	
	public String extractNumber(String s)
	{
		int end = s.indexOf('@',0);
		int begin = s.lastIndexOf('t', end);
		
		return s.substring(begin + 1, end);
	}

	@Override
	public boolean done() {
		return votesLeft == 0;
	}
	
	public int onEnd()
	{
		agent.doDelete();
		myAgent.doDelete();
		return 0;
	}
}