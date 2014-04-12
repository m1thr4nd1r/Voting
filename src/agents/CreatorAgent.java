package agents;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import behaviour.PoolingBehaviour;
import behaviour.StartBehaviour;
import jade.core.Agent;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

@SuppressWarnings("serial")
public class CreatorAgent extends Agent 
{	
	private int optionQnt = 0;
	private int agentQnt = 0;
	private String[] options = null;
	private int[] votes = null;
	private String flawed = "";
	private int flawedQnt = 2;
	private String type = null;
	private boolean receive;
	private int round = 1;
	private int rounds = 2;
	private long startTime, totalTime;
	PrintWriter writer;

//	Tempo medio (Pluralidade) = ( / 10) * 2 = ~ (3 Agentes)
//	Tempo medio (Borda) = ( / 10) * 2 = ~ (3 Agentes)
//	Tempo medio (Sequencial) = ( / 10) * 2 = ~ (3 Agentes)

//	Tempo medio (Pluralidade) = (227 / 10) * 2 = 45~ (10 Agentes)	
//	Tempo medio (Borda) = (237 / 10) * 2 = ~47 (10 Agentes)
//	Tempo medio (Sequencial) = (380 / 10) * 2 = ~76 (10 Agentes)
	
	private int[] timeOut = {45, 47, 76};
			
	protected void setup()
	{
		Object[] args = getArguments();
		if (args != null && args.length > 0)
		{
//			Tipo Votacao, Qtd Agentes, Qtd Acoes, Acao(1-N), Falha presente
			
//			Extraindo tipo de votacao
			type = (String) args[0];

//			Extraindo o numero de agentes envolvidos na votacao
			agentQnt = Integer.parseInt((String) args[1]);
			
//			Extraindo numero de opcoes disponiveis
			optionQnt = Integer.parseInt((String) args[2]);
			
			options = new String[optionQnt];
			votes = new int[optionQnt];
			
//			Extraindo opcoes disponiveis
			for (int i = 0; i < optionQnt; i++)
				options[i] = (String) args[i+3];
			
//			Extraindo se todos os agentes devem receber a mensagem (a falha nao esta presente)
			receive = !Boolean.parseBoolean((String) args[optionQnt+3]);

			if (!receive)
				this.rounds++;
			
//			Criando variaveis necessarias a criacao de um novo agente (em um novo container)
			Runtime rt = Runtime.instance();
			Profile p = new ProfileImpl();
			ContainerController agentContainer = rt.createAgentContainer(p);
			
//			Criando agentes
			for (int i = 0; i < agentQnt; i++)
			{
				String[] voterOptions = options.clone();
				
				try {
					AgentController ac = agentContainer.createNewAgent("VoterAgent"+(i+1), "agents.VoterAgent", voterOptions);
					ac.start();
				} catch (StaleProxyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			printOptions();			
			
			try {
				writer = new PrintWriter(type + ".csv", "UTF-8");				
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			beginWriting();
			this.setStartTime();
			
			this.addBehaviour(new PoolingBehaviour(this));
			this.addBehaviour(new StartBehaviour(this, "Start", type, agentQnt));
		}
	}
	
	protected void takeDown() 
	{
		writer.close();
	}
	
	public String getOption(int index)
	{
		return options[index];
	}
	
	public int getAgentQnt() {
		return agentQnt;
	}

	public int getOptionQnt() {
		return optionQnt;
	}

	public String[] getOptions() {
		return options;
	}

	public int[] getVotes() {
		return votes;
	}

	public int getVotes(int index)
	{
		return votes[index];
	}
	
	public String getType() {
		return type;
	}

	public void increment(int index)
	{
		votes[index]++;
	}
	
	public void increment(int index, int amount)
	{
		votes[index]+= amount;
	}
	
	public void resetVotes()
	{
		for (int i = 0; i < options.length; i++)
			votes[i] = 0;
	}
	
	public String extractNumber(String s)
	{
		int end = s.indexOf('@',0);
		int begin = s.lastIndexOf('t', end);
		
		return s.substring(begin + 1, end);
	}
	
	public String getFlawed()
	{
		return this.flawed;
	}
	
	public boolean isFlawed(String name)
	{
//		Verdadeiro quando o agente pertence ao grupo de agentes com falha, e a falha tambem esta presente
		return flawed.contains(name) && !receive && round > 1;
	}
	
	public void clearFlawed()
	{
		this.flawed = "";
	}
	
	public void chooseFlawed()
	{
		Random r = new Random();
		for (int i = 0; i < flawedQnt; i++)
		{
			String buggy = Integer.toString(r.nextInt(agentQnt) + 1);
			
			while (this.flawed.contains(buggy))
				buggy = Integer.toString(r.nextInt(agentQnt) + 1);
			
			this.flawed += buggy + " ";
		}
		System.out.println("Agente(s) falho(s): " + flawed);
	}
	
	public void printOptions()
	{
		String op = "Opcoes: ";
		for (int i = 0; i < options.length; i++)
			op+= options[i] + " ";
		
		System.out.println(op + "\n");
	}
	
	public void setStartTime()
	{
		startTime = System.currentTimeMillis();
	}
	
	public long getTotalTime()
	{
		return totalTime;
	}
	
	public long getTime()
	{
		long time = System.currentTimeMillis() - startTime;
		totalTime += time;
		return time;
	}
	
	public int getTimeOut()
	{
		switch (this.type)
		{
			case "Plurality" :
								return timeOut[0];
			case "Borda" : 
								return timeOut[1];
			case "Sequential" :
								return timeOut[2];
			default :
								return -1;
		}
	}

	public void beginWriting()
	{
		String txt = "";
		
		if (!type.equals("Borda"))
		{	
			txt = "ganhador;";
			
			for (int i = 0; i < agentQnt; i++)
				txt += "agente"+(i+1)+";";
			
			if (type.equals("Sequential"))
				txt+= "turno;";
			
			txt += "falha;\n";
		}
		else
		{
			for (int i = 0; i < optionQnt; i++)
				txt += "prioridade "+(i+1)+";";
			
			for (int i = 0; i < agentQnt; i++)
				txt += "agente"+(i+1)+";";
			
			txt += "falha;\n";
		}
		
		writeToFile(txt);
	}
	
	public void writeToFile(String txt)
	{
		this.writer.print(txt);
	}
	
	public int getRound() {
		return round;
	}

	public void incrementRound() {
		this.round++;
	}

	public int getRounds() {
		return rounds;
	}
	
	public boolean isReceiving()
	{
		return receive;
	}

	public int getFlawedQnt() {
		return flawedQnt;
	}
}