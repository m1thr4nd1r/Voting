package agents;

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
	private int[] votes;
	private String type = null;
	
	protected void setup()
	{
		Object[] args = getArguments();
		if (args != null && args.length > 0)
		{
//			Numero de Açoes, Acão1, Acão2,..., Numero de Agentes
//			Tipo de Votação,Numero de Açoes, Acão1, Acão2,..., Numero de Agentes
			
//			Extraindo tipo de votação
			type = (String) args[0];
			
//			Extraindo numero de opções disponiveis
			optionQnt = Integer.parseInt((String) args[1]);
//			System.out.println("Quantidade de opçoes: " + optionQnt);
			
			options = new String[optionQnt];
			votes = new int[optionQnt];
			
//			Extraindo opções disponiveis
			for (int i = 0; i < optionQnt; i++)
				options[i] = (String) args[i+2];
			
//			Extraindo o numero de agentes envolvidos na votação
			agentQnt = Integer.parseInt((String) args[optionQnt+2]);
//			System.out.println("Quantidade de agentes: " + agentQnt);
	
//			Criando variaveis necessarias a criação de um novo agente (em um novo container)
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
			
			this.addBehaviour(new PoolingBehaviour(this));
			this.addBehaviour(new StartBehaviour(this, "Start", type, agentQnt));
		}
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
	
	public void printOptions()
	{
		String op = "Opcoes: ";
		for (int i = 0; i < options.length; i++)
			op+= options[i] + " ";
		
		System.out.println(op + "\n");
	}
}