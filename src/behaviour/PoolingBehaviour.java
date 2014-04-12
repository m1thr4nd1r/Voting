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
	private double execTime, startTime;
	private String votes[];
	
	
	public PoolingBehaviour(Agent a)
	{
		this.myAgent = a;
		this.agent = (CreatorAgent) a;
		this.votesLeft = agent.getAgentQnt();
		this.turn = 1;
		this.startTime = System.currentTimeMillis();
		this.votes = new String[votesLeft];
		
		if (!this.agent.isReceiving() && agent.getRound() > 1)
			votesLeft-= agent.getFlawedQnt();
	}
	
	@Override
	public void action() 
	{
		ACLMessage msg = agent.receive();
		
		while (msg != null && msg.getConversationId() != null)
		{
			pool(msg.getContent(), agent.extractNumber(msg.getSender().getName()));
			
			if (msg.getConversationId().equals("Start"))
			{
				if (!agent.getType().equals("Sequential") && agent.getRound() == agent.getRounds())
				{
//					ACLMessage reply = msg.createReply();
//					reply.setConversationId("Done");
//					agent.send(reply);
				}
				else if (turn > 1)
					this.agent.addBehaviour(new StartBehaviour(this.agent, "Turn", agent.getOption(0) + " " + agent.getOption(1), agent.getAgentQnt()));
			}
//			else if (msg.getConversationId().equals("Turn") && agent.getRound() == agent.getRounds())
//			{
//				ACLMessage reply = msg.createReply();
//				reply.setConversationId("Done");
//				agent.send(reply);
//			}
			
			msg = agent.receive();
		}
		
		this.execTime = System.currentTimeMillis() - this.startTime;
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
		votes[Integer.valueOf(name) - 1] = agent.getOption(i);
		
		System.out.println("Eleitor " + name + " - Vota na opção de numero " + (i+1) + " (" + agent.getOption(i) + ") no " + turn + " turno. Total de Votos desta opção: " + agent.getVotes(i));
		
		if (votesLeft == 0 && turn == 1)
		{
			orderOptions();
			
			createCSV();
			
			for (int j = 0 ; j < votes.length; j++)
				votes[j] = null;
			
			turn++;
			agent.resetVotes();
			
			votesLeft = agent.getAgentQnt();
			if (!this.agent.isReceiving() && agent.getRound() > 1)
				votesLeft--;			
			
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
		
		votes[Integer.valueOf(name) - 1] = vote.replace(' ', ',');
		
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
		votes[Integer.valueOf(name) - 1] = agent.getOption(i);
		
		System.out.println("Eleitor " + name + " - Vota na opção de numero " + (i+1) + " (" + agent.getOption(i) + "). Total de Votos desta opção: " + agent.getVotes(i));
	}
	
	private void orderOptions()
	{
		int optionQnt = agent.getOptionQnt();
		String[] options = agent.getOptions();
		int[] votes = agent.getVotes();
	
//		Bubble Sort
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
	
	public boolean timeOut()
	{
		return this.execTime > this.agent.getTimeOut();
	}
	
	public void createCSV()
	{
		String txt = "";
		
		if (!agent.getType().equals("Borda"))
		{
			txt = agent.getOption(0) + ";";
			for (int i = 0 ; i < votes.length; i++)
				txt += votes[i] + ";";
			
			if (agent.getType().equals("Sequential"))
				txt += this.turn + ";"; 
				
			txt += agent.getFlawed() + ";\n";
		}
		else
		{
			for (int i = 0; i < agent.getOptionQnt(); i++)
				txt += agent.getOption(i) + ";";
			
			for (int i = 0 ; i < votes.length; i++)
				txt += votes[i] + ";";
			
			txt += agent.getFlawed() + ";\n";
		}
		
		agent.writeToFile(txt);
	}
	
	@Override
	public boolean done() {
		
		if (agent.getRound() == 1)
		{
			if (agent.getType().equals("Sequential"))
				return votesLeft == 0 && turn == 2;
			else
			{
				if (votesLeft == 0) 
					System.out.println("Sai");
				return votesLeft == 0;				
			}
		}
		else
		{
			if (agent.getType().equals("Sequential"))
				return votesLeft == 0 && turn == 2 || timeOut();
			else
				return votesLeft == 0 || timeOut();
		}
	}
	
	public int onEnd()
	{
		System.out.println();
		long time = agent.getTime();
		
		if (agent.getType().equals("Plurality") || agent.getType().equals("Sequential"))
		{
			orderOptions();	
			System.out.print("O ganhador da eleição é '" + agent.getOption(0) + "' com " + agent.getVotes(0) + " votos");
			createCSV();
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
			
			createCSV();
		}		
		
		System.out.println("\nTempo de execução: " + time + " milisegundos");
		System.out.println("-------------- Termino do Round " + agent.getRound() + " --------------\n");
		
		if (agent.getRound() == agent.getRounds())
		{
			System.out.println("Tempo de execução total: " + agent.getTotalTime() + " milisegundos");
			agent.addBehaviour(new StartBehaviour(agent, "Done", "", agent.getAgentQnt()));
		}
		else
		{
			if (!this.agent.isReceiving())
			{
				this.agent.clearFlawed();
				this.agent.chooseFlawed();
			}
			
			agent.resetVotes();
			agent.incrementRound();
			agent.setStartTime();
			
			agent.addBehaviour(new PoolingBehaviour(agent));
			agent.addBehaviour(new StartBehaviour(agent, "Start", agent.getType(), agent.getAgentQnt()));
		}
		
		return 0;
	}
}