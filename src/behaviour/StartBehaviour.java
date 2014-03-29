package behaviour;

import agents.CreatorAgent;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class StartBehaviour extends OneShotBehaviour
{
	private String content, id;
	private int agents;
	private CreatorAgent agent;
	
	public StartBehaviour(Agent a, String id, String content, int agents)
	{
		this.myAgent = a;
		this.agent = (CreatorAgent) a;
		this.content = content;
		this.agents = agents;
		this.id = id;
	}
	
	@Override
	public void action() 
	{
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setConversationId(id);
		msg.setContent(content);
		
		addReceivers("voter", msg, agents);
		
		agent.send(msg);
	}
	
	public void addReceivers( String service, ACLMessage msg, int agents )
    {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType( service );
        dfd.addServices(sd);
        
        SearchConstraints ALL = new SearchConstraints();
        ALL.setMaxResults(new Long(-1));
        
        try
        {
            DFAgentDescription[] result = DFService.search(agent, dfd, ALL);
            
            while (result.length != agents)
            	result = DFService.search(agent, dfd, ALL);
            
            for (int i=0; i<result.length; i++)
//            	Caso o agente pertença aos agentes com falhas
            	if (!agent.isBuggy(agent.extractNumber(result[i].getName().getName())))
            		msg.addReceiver(result[i].getName());

        }
        catch (FIPAException fe) { fe.printStackTrace(); }
    }
}
