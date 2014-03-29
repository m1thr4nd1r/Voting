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
	private int[] votes = null;
	private String buggy = "";
	private String type = null;
	private boolean receive;
	
	protected void setup()
	{
		Object[] args = getArguments();
		if (args != null && args.length > 0)
		{
// 			Tipo Votacao, Qtd Agentes, Qtd Acoes, Acao(1-N), Falha presente, Agente Falho (1-N)
			
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
			
//			Extraindo se a falha esta presente
			receive = !Boolean.parseBoolean((String) args[optionQnt+3]);

			for (int i = optionQnt + 3; i < args.length; i++)
				buggy += (String) args[i] + " ";
				
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
	
	public String extractNumber(String s)
	{
		int end = s.indexOf('@',0);
		int begin = s.lastIndexOf('t', end);
		
		return s.substring(begin + 1, end);
	}
	
	public boolean isBuggy(String name)
	{
//		Verdadeiro quando o agente pertence ao grupo de agentes com falha, e a falha tambem esta presente
		return buggy.contains(name) && !receive;
	}
	
	public void printOptions()
	{
		String op = "Opcoes: ";
		for (int i = 0; i < options.length; i++)
			op+= options[i] + " ";
		
		System.out.println(op + "\n");
	}
}