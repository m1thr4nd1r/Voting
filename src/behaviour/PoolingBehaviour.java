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
	private int votesLeft;
	private int turn;
	private int turns;
	private double execTime, startTime;
	private ArrayList<Integer> votes;
	private ArrayList<String> options;
	private String choice[];
	private String turnWinners[];
	private int step;
	
	public PoolingBehaviour(Agent a)
	{
		this.myAgent = a;
		this.agent = (CreatorAgent) a;
		this.votesLeft = agent.getAgentQnt();
		this.turn = 1;
		this.startTime = System.currentTimeMillis();
		this.choice = new String[votesLeft];
		this.options = new ArrayList<String>(Arrays.asList(agent.getOptions().clone()));
		
		this.votes = new ArrayList<Integer>();
		for (int i = 0; i < options.size(); i++)
			this.votes.add(0);
		
		this.step = 0;
		this.turns = agent.getOptionQnt() - 1;
		
		this.turnWinners = new String[turns];
		
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
			
			if (msg.getConversationId().equals("Sequential") && 
				this.turn < this.turns && 
				(votesLeft == agent.getAgentQnt() || (votesLeft == agent.getAgentQnt() - agent.getFlawedQnt() && 
													!agent.isReceiving() && 
													agent.getRound() > 1)))
				this.agent.addBehaviour(new StartBehaviour(this.agent, this.agent.getType(), getSequentialOptions(""), agent.getAgentQnt()));
			
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
		
		while (!options.get(i).equals(vote))
			i++;
		
		return i;
	}
	
	private void sequentialPooling(String vote, String name)
	{
		int i = find(vote);
		
		votes.set(i, votes.get(i) + 1);
		votesLeft--;
		choice[Integer.valueOf(name) - 1] = options.get(i);
		
		System.out.println("Eleitor " + name + " - Vota na opção de numero " + (i+1) + " (" + options.get(i) + ") no " + turn + " turno. Total de Votos desta opção: " + votes.get(i));
		
		if (votesLeft == 0 && turn <= turns)
		{
			if (this.votes.get(step) >= this.votes.get(1 + step))
			{
				votes.remove(1 + step);
				options.remove(1 + step);
			}
			else
			{
				votes.remove(step);
				options.remove(step);
			}
			
			turnWinners[turn-1] = options.get(step);
			
			for (int j = 0 ; j < choice.length; j++)
				choice[j] = null;
			
			votesLeft = agent.getAgentQnt();
			
			if (!this.agent.isReceiving() && agent.getRound() > 1)
				votesLeft-= agent.getFlawedQnt();			
			
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
		
		choice[Integer.valueOf(name) - 1] = vote.replace(' ', ',');
		
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
		votes.set(i, votes.get(i) + 1);
		votesLeft--;
		choice[Integer.valueOf(name) - 1] = agent.getOption(i);
		
		System.out.println("Eleitor " + name + " - Vota na opção de numero " + (i+1) + " (" + agent.getOption(i) + "). Total de Votos desta opção: " + agent.getVotes(i));
	}
	
	public void orderOptions()
	{
//		Bubble Sort
		int optionQnt = this.options.size();
		int[] v = agent.getVotes();
		String[] o = agent.getOptions();
		
		for (int i = 0; i < optionQnt; i++)
			for (int j = 0; j < optionQnt; j++)
				if (v[i] > v[j])
				{
					int tempI = v[i];
					String tempS = o[i];
					
					v[i] = v[j];
					o[i] = o[j];
					
					v[j] = tempI;
					o[j] = tempS;
				}
	}
	
	public String getSequentialOptions(String type) 
	{
		step++;
		turn++;
		
		if (step >= options.size() - 1)
		{			
			votes.clear();
			for (int i = 0; i < options.size(); i++)
				votes.add(0);
			
			step = 0;
		}
		
		return options.get(step) + " " + options.get(step + 1);
	}
	
	public boolean timeOut()
	{
		return this.execTime > this.agent.getTimeOut();
	}
		
	public void createCSV()
	{
		String txt = "";
		
		if (agent.getType().equals("Plurality"))
		{
			txt = agent.getOption(0) + ";";
			for (int i = 0 ; i < choice.length; i++)
				txt += choice[i] + ";";
		}
		else if (agent.getType().equals("Sequential"))
		{
			txt = options.get(step) + ";";
			for (int i = 0 ; i < turns; i++)
				txt += turnWinners[i] + ";";
			
			for (int i = 0; i < agent.getOptions().length; i++)
				txt += agent.getOption(i) + " ";
			
			txt += ";";
			
//			for (int i = 0 ; i < choice.length; i++)
//				txt += choice[i] + ";";
		}
		else
		{
			for (int i = 0; i < agent.getOptionQnt(); i++)
				txt += agent.getOption(i) + ";";
			
			for (int i = 0 ; i < choice.length; i++)
				txt += choice[i] + ";";
		}
		
		txt += agent.getFlawed() + ";\n";
		
		agent.writeToFile(txt);
	}
	
	@Override
	public boolean done() {
		
		if (agent.getRound() == 1 || this.agent.isReceiving())
		{
			if (agent.getType().equals("Sequential"))
//				return votesLeft == 3 && turn => turns;
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
		
		if (agent.getType().equals("Plurality"))
		{
			orderOptions();	
			System.out.print("O ganhador da eleição é '" + agent.getOption(0) + "' com " + agent.getVotes(0) + " votos");
		}
		else if (agent.getType().equals("Sequential"))
			System.out.print("O ganhador da eleição é '" + options.get(0) + "' com " + votes.get(0) + " votos");
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
		
		createCSV();
		
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
			agent.addBehaviour(new StartBehaviour(agent, agent.getType(), agent.getOption(0) + " " + agent.getOption(1), agent.getAgentQnt()));
		}
		
		return 0;
	}
}