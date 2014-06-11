package behaviour;

import java.util.ArrayList;
import java.util.Arrays;

import agents.CreatorAgent;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class PoolingBehaviour extends SimpleBehaviour {
	
	private CreatorAgent agent;
	private int turn,turns,votesLeft,step;
	private double execTime, startTime;
	private ArrayList<Integer> votes;
	private ArrayList<String> options;
	private String choice[], type;
	
	public PoolingBehaviour(Agent a)
	{
		this.myAgent = a;
		this.agent = (CreatorAgent) a;
		
		this.votesLeft = agent.getAgentQnt();
		
		this.startTime = System.currentTimeMillis();
		
		this.choice = new String[votesLeft];
		this.options = new ArrayList<String>(Arrays.asList(agent.getOptions().clone()));
		this.votes = new ArrayList<Integer>();
		
		for (int i = 0; i < options.size(); i++)
			this.votes.add(0);
		
		this.turn = 0;
		this.step = -1;
		this.type = agent.getMethod();
		this.turns = options.size() - 1;
		
		if (!this.agent.isReceiving() && agent.getRound() > 1)
			votesLeft-= agent.getFlawedQnt();
		
		System.out.println("-------------- Inicio do Round " + agent.getRound() + " --------------");
	}
	
	@Override
	public void action() 
	{
		ACLMessage msg = agent.receive();
		
		while (msg != null && msg.getConversationId() != null)
		{
			pool(msg.getContent(), agent.extractNumber(msg.getSender().getName()));
			
			if (msg.getConversationId().equals("Sequential") && 
				this.turn < this.turns && 
				votesLeft == agent.getVotesQnt())
					this.agent.addBehaviour(new StartBehaviour(this.agent, this.agent.getType(), getSequentialOptions(), agent.getAgentQnt()));
			
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
		
		while (!options.get(i).equals(vote) && i < options.size())
			i++;
		
		return i;
	}
	
	public void pluralityPooling(String vote, String name)
	{
		int i = find(vote);
		this.startTime = System.currentTimeMillis();
		
		votes.set(i, votes.get(i) + 1);
		votesLeft--;
		choice[Integer.valueOf(name) - 1] = options.get(i);
		
		System.out.println("Eleitor " + name + " - Vota na opção de numero " + (i+1) + " (" + options.get(i) + "). Total de Votos desta opção: " + votes.get(i));
	}
	
	public void bordaPooling(String vote, String name)
	{
		this.startTime = System.currentTimeMillis();
		int end = vote.length() - 1;
		int begin = vote.lastIndexOf(' ', end - 1);
		int count = 1;
		int i = 0;
		String op = "";
		String points = "";
		
		choice[Integer.valueOf(name) - 1] = vote.replace(' ', ',');
		
		while (begin != -1)
		{
			i = find(vote.substring(begin+1, end));
			end = begin;
			begin = vote.lastIndexOf(' ', end - 1);
			
			votes.set(i, votes.get(i) + count);
			count++;
			
			op = options.get(i) + " " + op;
			points = String.valueOf(votes.get(i)) + " " + points;
		}
		
		i = find(vote.substring(0, end));
		
		votes.set(i, votes.get(i) + count);
		count++;
		
		op = options.get(i) + " " + op;
		points = String.valueOf(votes.get(i)) + " " + points;
		
		votesLeft--;
			
		System.out.println("Eleitor " + name + " - Vota na seguinte ordem : " + op);
		System.out.println("      Pontuação de cada opção      : " + points);
	}
	
	public void sequentialPooling(String vote, String name)
	{
		int i = find(vote);
		this.startTime = System.currentTimeMillis();

		votes.set(i, votes.get(i) + 1);
		votesLeft--;
		choice[Integer.valueOf(name) - 1] = options.get(i);
		
		System.out.println("Eleitor " + name + " - Vota na opção de numero " + (i+1) + " (" + options.get(i) + ") no " + (turn+1) + " turno. Total de Votos desta opção: " + votes.get(i));
		
		if (votesLeft == 0 && turn <= turns)
		{
			int loser;
			
			if (type.contains("Forward"))
				loser = (this.votes.get(step) >= this.votes.get(1 + step)) ? step + 1 : step;
			else 			
				loser = (this.votes.get(votes.size() - step) > this.votes.get(votes.size() - step - 1)) ? votes.size() - step - 1 : votes.size() - step; 
			
			votes.remove(loser);
			options.remove(loser);
			
			createCSV();
			for (int j = 0 ; j < choice.length; j++)
				choice[j] = null;
			
			votesLeft = agent.getAgentQnt();
			
			if (!this.agent.isReceiving() && agent.getRound() > 1)
				votesLeft-= agent.getFlawedQnt();			
			
			System.out.println();
		}	
	}
	
	public void orderOptions()
	{
//		Bubble Sort
		for (int i = 0; i < options.size(); i++)
			for (int j = 0; j < options.size(); j++)
				if (votes.get(i) > votes.get(j))
				{
					int tempI = votes.get(i);
					String tempS = options.get(i);
					
					votes.set(i, votes.get(j));
					options.set(i, options.get(j));
					
					votes.set(j, tempI);
					options.set(j, tempS);
				}
	}
	
	public String getSequentialOptions() 
	{
		// Step começa de 0
		// Turn começa de 1
		step++;
		turn++;
		
		boolean overflow = false;
		
		if (step >= options.size() - 1)
		{			
			votes.clear();
			for (int i = 0; i < options.size(); i++)
				votes.add(0);
			
			overflow = true;
		}
		
		if (type.contains("M") && overflow)
				type = (type.contains("Forward")) ? "BackwardsM" : "ForwardM";
			
		if (type.contains("Forward"))
		{
			if (overflow)
				step = 0;
			
			return options.get(step) + " " + options.get(step + 1);
		}
		else // if (type.contains("Backwards"))
		{
			if (overflow)
				step = 1;
			
			return options.get(options.size() - step) + " " + options.get(options.size() - step - 1);
		}	
	}
	
	public boolean timeOut()
	{
		return this.execTime > this.agent.getTimeOut();
	}
	
	@Override
	public boolean done() {
		
		if (agent.getRound() == 1 || this.agent.isReceiving())
		{
			if (agent.getType().equals("Sequential"))
				return options.size() == 1;
			else
				return votesLeft == 0;
		}
		else
		{
			if (agent.getType().equals("Sequential"))
				return options.size() == 1 || timeOut();
			else
				return votesLeft == 0 || timeOut();
		}
	}
	
	public int onEnd()
	{
		System.out.println();
		long time = agent.getTime();
		
		declareWinner();
		
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
				this.agent.chooseFlawed();
			
			agent.incrementRound();
			agent.setStartTime();
			
			PoolingBehaviour behaviour = new PoolingBehaviour(agent);
			
			agent.addBehaviour(behaviour);
			agent.addBehaviour(new StartBehaviour(agent, agent.getType(), behaviour.getSequentialOptions() , agent.getAgentQnt()));
		}
		
		return 0;
	}
	
	public void declareWinner()
	{
		if (agent.getType().equals("Plurality"))
		{
			orderOptions();	
			System.out.print("O ganhador da eleição é '" + options.get(0) + "' com " + votes.get(0) + " votos");			
			createCSV();
		}
		else if (agent.getType().equals("Sequential"))
			System.out.print("O ganhador da eleição é '" + options.get(0) + "' com " + votes.get(0) + " votos");
		else if (agent.getType().equals("Borda"))
		{
			orderOptions();	
			System.out.print("A eleição determinou a seguinte prioridade: ");
						
			for (int i = 0; i < options.size(); i++)
				System.out.print(options.get(i) + " ");
			
			System.out.print("\n   Cada opção teve a seguinte pontuação   : ");
			
			for (int i = 0; i < votes.size(); i++)
				System.out.print(votes.get(i) + " ");
			
			createCSV();
		}	
	}
	
	public void createCSV()
	{
		String txt = "";
		
		if (agent.getType().equals("Plurality"))
		{
			txt = options.get(0) + ";";
			for (int i = 0 ; i < choice.length; i++)
				txt += choice[i] + ";";
		}
		else if (agent.getType().equals("Sequential"))
		{
			txt = (options.size() == 1)? options.get(0) : ""; 
			
			txt += ";" + options.get(step) + ";";
			
			for (int i = 0 ; i < choice.length; i++)
				txt += choice[i] + ";";
			
			txt += turn + ";";
		}
		else
		{
			for (int i = 0; i < options.size(); i++)
				txt += options.get(i) + ";";
			
			for (int i = 0 ; i < choice.length; i++)
				txt += choice[i] + ";";
		}
		
		txt += agent.getFlawed() + ";\n";
		
		agent.writeToFile(txt);
	}
}