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
		ACLMessage msg = agent.receive();
		
		while (msg != null && msg.getConversationId() != null)
		{
			if (msg.getConversationId().equals("Start"))
			{
				pool(msg.getContent(), extractNumber(msg.getSender().getName()));
				ACLMessage reply = msg.createReply();
				reply.setConversationId("Done");
				agent.send(reply);
			}
			
			msg = agent.receive();
		}
	}
	
	public void pool(String vote, String name)
	{
		int i;
		
		if (agent.getType().equals("Simple"))
		{
			i = Integer.valueOf(vote);
		
			agent.increment(i);
			votesLeft--;
			
			System.out.println("Eleitor " + name + " - Vota na op��o de numero " + (i+1) + " (" + agent.getOption(i) + "). Total de Votos desta op��o: " + agent.getVotes(i));
		}
		else if (agent.getType().equals("Borda"))
		{
			int end = vote.length() - 1;
			int begin = vote.lastIndexOf(' ', end - 1);
			int count = 1;	
			String op = "";
			String points = "";
			
			while (begin != -1)
			{
				i = Integer.valueOf(vote.substring(begin+1, end));
				end = begin;
				begin = vote.lastIndexOf(' ', end - 1);
				
				agent.increment(i, count);
				count++;
				op = agent.getOption(i) + " " + op;
				points = String.valueOf(agent.getVotes(i)) + " " + points;
			}
			
			i = Integer.valueOf(vote.substring(0, end));
			
			agent.increment(i, count);
			count++;
			op = agent.getOption(i) + " " + op;
			points = String.valueOf(agent.getVotes(i)) + " " + points;
			
			votesLeft--;
			
			System.out.println("Eleitor " + name + " - Vota na seguinte ordem : " + op);
			System.out.println("      Pontua��o de cada op��o      : " + points);
		}
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