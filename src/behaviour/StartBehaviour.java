package behaviour;

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
	private String type;
	
	public StartBehaviour(Agent a, String type)
	{
		this.myAgent = a;
		this.type = type;
	}
	
	@Override
	public void action() 
	{
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setConversationId("Start");
		msg.setContent(type);
		
		addReceivers("voter", msg);
		
		myAgent.send(msg);
	}
	
	public void addReceivers( String service, ACLMessage msg )
    {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType( service );
        dfd.addServices(sd);
        
        SearchConstraints ALL = new SearchConstraints();
        ALL.setMaxResults(new Long(-1));

        try
        {
            DFAgentDescription[] result = DFService.search(myAgent, dfd, ALL);
            
            for (int i=0; i<result.length; i++) 
                msg.addReceiver(result[i].getName());

        }
        catch (FIPAException fe) { fe.printStackTrace(); }
    }
}
