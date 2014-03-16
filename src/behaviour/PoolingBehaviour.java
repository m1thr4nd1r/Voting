package behaviour;

import agents.CreatorAgent;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class PoolingBehaviour extends SimpleBehaviour {
	
	private CreatorAgent agent;
	private int votesLeft;
	private int turn;
	
	public PoolingBehaviour(Agent a)
	{
		this.myAgent = a;
		this.agent = (CreatorAgent) a;
		this.votesLeft = agent.getAgentQnt();
		this.turn = 1;
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
				
				if (!agent.getType().equals("Sequential"))
				{
					ACLMessage reply = msg.createReply();
					reply.setConversationId("Done");
					agent.send(reply);
				}
				else if (turn > 1)
					this.agent.addBehaviour(new StartBehaviour(this.myAgent, "Turn", agent.getOption(0) + " " + agent.getOption(1), agent.getAgentQnt()));
			}
			else if (msg.getConversationId().equals("Turn"))
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
		if (agent.getType().equals("Plurality"))
			pluralityPooling(vote, name);			
		else if (agent.getType().equals("Borda"))
			bordaPooling(vote, name);	
		else if (agent.getType().equals("Sequential"))
			sequentialPooling(vote,name);
	}
	
	public String extractNumber(String s)
	{
		int end = s.indexOf('@',0);
		int begin = s.lastIndexOf('t', end);
		
		return s.substring(begin + 1, end);
	}

	public int find(String vote)
	{
		int i = 0;
		
		while (!agent.getOption(i).equals(vote))
			i++;
		
		return i;
	}
	
	private void sequentialPooling(String vote, String name)
	{
		int i = find(vote);
		
		agent.increment(i);
		votesLeft--;
		
		System.out.println("Eleitor " + name + " - Vota na opção de numero " + (i+1) + " (" + agent.getOption(i) + ") no " + turn + " turno. Total de Votos desta opção: " + agent.getVotes(i));
		
		if (votesLeft == 0 && turn == 1)
		{
			orderOptions();
			turn++;
			agent.resetVotes();
			votesLeft = agent.getAgentQnt();
			System.out.println();
		}	
	}
	
	private void bordaPooling(String vote, String name)
	{
		int end = vote.length() - 1;
		int begin = vote.lastIndexOf(' ', end - 1);
		int count = 1;	
		String op = "";
		String points = "";
		
		while (begin != -1)
		{
			int i = find(vote.substring(begin+1, end));
			end = begin;
			begin = vote.lastIndexOf(' ', end - 1);
			
			agent.increment(i, count);
			count++;
			
			op = agent.getOption(i) + " " + op;
			points = String.valueOf(agent.getVotes(i)) + " " + points;
		}
		
		int i = find(vote.substring(0, end));
		
		agent.increment(i, count);
		count++;
		
		op = agent.getOption(i) + " " + op;
		points = String.valueOf(agent.getVotes(i)) + " " + points;
		
		votesLeft--;
			
		System.out.println("Eleitor " + name + " - Vota na seguinte ordem : " + op);
		System.out.println("      Pontuação de cada opção      : " + points);
	}
	
	private void pluralityPooling(String vote, String name)
	{
		int i = find(vote);
		
		agent.increment(i);
		votesLeft--;
		
		System.out.println("Eleitor " + name + " - Vota na opção de numero " + (i+1) + " (" + agent.getOption(i) + "). Total de Votos desta opção: " + agent.getVotes(i));
	}
	
	private void orderOptions()
	{
		int optionQnt = agent.getOptionQnt();
		String[] options = agent.getOptions();
		int[] votes = agent.getVotes();
		
		for (int i = 0; i < optionQnt; i++)
			for (int j = 0; j < optionQnt; j++)
				if (votes[i] > votes[j])
				{
					int tempI = votes[i];
					String tempS = options[i];
					
					votes[i] = votes[j];
					options[i] = options[j];
					
					votes[j] = tempI;
					options[j] = tempS;
				}
	}
	
	@Override
	public boolean done() {
		
		if (agent.getType().equals("Sequential"))
			return votesLeft == 0 && turn == 2;
		
		return votesLeft == 0;
	}
	
	public int onEnd()
	{
		System.out.println();
		
		if (agent.getType().equals("Plurality") || agent.getType().equals("Sequential"))
		{
			orderOptions();			
			System.out.println("O ganhador da eleição é '" + agent.getOption(0) + "' com " + agent.getVotes(0) + " votos");
		}
		else if (agent.getType().equals("Borda"))
		{
			orderOptions();	
			
			System.out.print("A eleição determinou a seguinte prioridade: ");
						
			for (int i = 0; i < agent.getOptionQnt(); i++)
				System.out.print(agent.getOption(i) + " ");
			
			System.out.print("\n   Cada opção teve a seguinte pontuação   : ");
			
			for (int i = 0; i < agent.getOptionQnt(); i++)
				System.out.print(agent.getVotes(i) + " ");
		}		
		
		agent.doDelete();
		myAgent.doDelete();
		return 0;
	}
}