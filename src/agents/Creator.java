package agents;

import jade.core.Agent;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

@SuppressWarnings("serial")
public class Creator extends Agent {
	
	private int optionQnt = 0;
	private String[] options = null;
	private int agentQnt = 0;
	
	protected void setup()
	{
		Object[] args = getArguments();
		if (args != null && args.length > 0)
		{
//			Numero de A�oes, Ac�o1, Ac�o2,..., Numero de Agentes
			
//			Extraindo numero de op��es disponiveis
			optionQnt = Integer.parseInt((String) args[0]);
			System.out.println("Quantidade de op�oes: " + optionQnt);
			
			options = new String[optionQnt];
			
//			Extraindo op��es disponiveis
			for (int i = 0; i < optionQnt; i++)
			{
				options[i] = (String) args[i+1];
				System.out.println("Op��o " + (i+1) + ": " + options[i]);
			}
			
//			Extraindo o numero de agentes envolvidos na vota��o
			agentQnt = Integer.parseInt((String) args[optionQnt+1]);
			System.out.println("Quantidade de agentes: " + agentQnt);
	
//			Criando variaveis necessarias a cria��o de um novo agente (em um novo container)
			Runtime rt = Runtime.instance();
			Profile p = new ProfileImpl();
			ContainerController agentContainer = rt.createAgentContainer(p);
			
//			Criando agentes
			for (int i = 0; i < agentQnt; i++)
			{
				try {
					AgentController ac = agentContainer.createNewAgent("Voter"+(i+1), "agents.Voter", options);
					ac.start();
				} catch (StaleProxyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}